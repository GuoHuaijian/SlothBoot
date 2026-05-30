# starter-sms 增强方案

> 优先级: P0 | 当前: 10 文件 | 目标: 18 文件

---

## 一、当前问题

1. **`AliyunSmsClient` 是空壳** — 只 log 并返回 `success=true`，不调用任何 SDK
2. **`TencentSmsClient` 是空壳** — log 警告并返回 `success=false, "not implemented"`
3. **缺少 `@ConditionalOnClass`** — 无论 SDK 是否在 classpath 都会注册 bean
4. **`TencentSmsClient` 构造缺陷** — 不接收 `SmsProperties`，无凭证配置
5. **无健康检查、无指标、无事件、无重试、无限流、无号码校验**

---

## 二、增强方案

### 2.1 真实 SDK 集成（P0）

**AliyunSmsClient 重写**：
```java
// 使用 com.aliyun:dysmsapi20170525 SDK
// 初始化: com.aliyun.dysmsapi20170525.Client
// 发送: SendSmsRequest → SendSmsResponse
// 批量: SendBatchSmsRequest → SendBatchSmsResponse
```

**TencentSmsClient 重写**：
```java
// 使用 com.tencentcloudapi:tencentcloud-sdk-java-sms SDK
// 初始化: com.tencentcloudapi.sms.v20210111.SmsClient
// 发送: SendSmsRequest → SendSmsResponse
```

**配置修复**：
```java
// SmsAutoConfiguration.java
@Bean
@ConditionalOnMissingBean
@ConditionalOnProperty(prefix = "sloth.sms", name = "type", havingValue = "aliyun")
@ConditionalOnClass(name = "com.aliyun.dysmsapi20170525.Client")
public SmsClient aliyunSmsClient(SmsProperties properties) {
    return new AliyunSmsClient(properties);
}

@Bean
@ConditionalOnMissingBean
@ConditionalOnProperty(prefix = "sloth.sms", name = "type", havingValue = "tencent")
@ConditionalOnClass(name = "com.tencentcloudapi.sms.v20210111.SmsClient")
public SmsClient tencentSmsClient(SmsProperties properties) {
    return new TencentSmsClient(properties);
}
```

### 2.2 HealthIndicator（P1）

```
health/SmsHealthIndicator.java
- 检查: SDK Client 能否正常初始化
- 检查: 凭证字段是否非空（不实际调用付费 API）
- 报告: provider type, regionId, credentialPresent
```

### 2.3 Metrics（P1）

```
metrics/SmsMetrics.java
- 计数器: sms.send.total (tags: provider, templateCode)
- 计数器: sms.send.success (tags: provider, templateCode)
- 计数器: sms.send.failure (tags: provider, errorCode)
- 计时器: sms.send.duration (tags: provider)
```

注入位置：在 `SmsTemplate` 中用 `MeterRegistry` 包装每次发送调用。

### 2.4 事件发布（P1）

```
event/SmsSentEvent.java   — phone, templateCode, msgId, success, timestamp
event/SmsFailedEvent.java — phone, templateCode, errorCode, errorMessage, timestamp
```

发布位置：`SmsTemplate` 发送后根据结果发布对应事件。

### 2.5 重试机制（P2）

```
core/RetryableSmsClient.java
- 装饰器模式，包装任意 SmsClient
- 配置: sloth.sms.retry.max-attempts=3, sloth.sms.retry.backoff-ms=500
- 仅对网络异常重试，对业务错误（参数错误、余额不足）不重试
```

### 2.6 号码校验（P2）

```
util/PhoneValidator.java
- 校验格式: 中国大陆手机号（1xx 11位）
- 校验格式: E.164 国际号码（+开头）
- 集成位置: SmsTemplate.send() 调用前
- 配置开关: sloth.sms.phone-validation.enabled=true
```

### 2.7 限流（P2）

```
core/SmsRateLimiter.java
- Semaphore 限流
- 配置: sloth.sms.rate-limit-per-second=100
- 集成位置: SmsTemplate 调用前
```

---

## 三、新增依赖

```xml
<!-- pom.xml 新增 -->
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>dysmsapi20170525</artifactId>
    <version>${aliyun-sms.version}</version>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>com.tencentcloudapi</groupId>
    <artifactId>tencentcloud-sdk-java-sms</artifactId>
    <version>${tencent-sms.version}</version>
    <optional>true</optional>
</dependency>
```

---

## 四、新增文件清单

| 文件 | 类型 | 说明 |
|------|------|------|
| `core/AliyunSmsClient.java` | 重写 | 真实 SDK 集成 |
| `core/TencentSmsClient.java` | 重写 | 真实 SDK 集成 |
| `core/RetryableSmsClient.java` | 新增 | 重试装饰器 |
| `core/SmsRateLimiter.java` | 新增 | 客户端限流 |
| `health/SmsHealthIndicator.java` | 新增 | 健康检查 |
| `metrics/SmsMetrics.java` | 新增 | Micrometer 指标 |
| `event/SmsSentEvent.java` | 新增 | 发送成功事件 |
| `event/SmsFailedEvent.java` | 新增 | 发送失败事件 |
| `util/PhoneValidator.java` | 新增 | 号码格式校验 |
| `config/SmsAutoConfiguration.java` | 修改 | 增加 @ConditionalOnClass |

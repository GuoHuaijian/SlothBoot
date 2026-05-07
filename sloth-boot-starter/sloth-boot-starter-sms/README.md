# Sloth Boot Starter SMS

短信发送统一组件，通过 `SmsTemplate` 门面屏蔽阿里云短信、腾讯云短信差异，支持单发和批量发送。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-sms</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `sloth.sms.enabled` | `boolean` | `true` | 是否启用 SMS Starter |
| `sloth.sms.type` | `String` | `aliyun` | 短信供应商：`aliyun` / `tencent` |
| `sloth.sms.access-key-id` | `String` | - | AccessKey Id |
| `sloth.sms.access-key-secret` | `String` | - | AccessKey Secret |
| `sloth.sms.sign-name` | `String` | - | 短信签名 |
| `sloth.sms.region-id` | `String` | `cn-hangzhou` | 区域 ID |

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `SmsClient` | 短信客户端接口，定义 send / batchSend |
| `AliyunSmsClient` | 阿里云短信实现 |
| `TencentSmsClient` | 腾讯云短信实现 |
| `SmsTemplate` | 门面类，委托给对应 `SmsClient` |
| `SendResult` | 发送结果，包含 success、msgId、message |

## 配置示例

```yaml
sloth:
  sms:
    type: aliyun
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
    sign-name: SlothBoot
    region-id: cn-hangzhou
```

## 发送示例

```java
@Service
@RequiredArgsConstructor
public class NotifyService {

    private final SmsTemplate smsTemplate;

    public void sendVerifyCode(String phone, String code) {
        Map<String, String> params = Map.of("code", code);
        SendResult result = smsTemplate.send(phone, "SMS_123456", params);
        if (!result.isSuccess()) {
            throw new BizException("短信发送失败: " + result.getMessage());
        }
    }

    public void batchNotify(List<String> phones, String notice) {
        Map<String, String> params = Map.of("content", notice);
        smsTemplate.batchSend(phones, "SMS_789012", params);
    }
}
```

## FAQ

**Q: 如何切换短信供应商？**
A: 修改 `sloth.sms.type` 为 `tencent` 即可，需同步配置腾讯云的 AccessKey。

**Q: 如何自定义 SmsClient 实现？**
A: 注册自定义 `SmsClient` Bean，`@ConditionalOnMissingBean` 机制会优先使用您的实现。

**Q: 批量发送有数量限制吗？**
A: 具体限制取决于底层短信供应商的 API 限制（阿里云单次最多 1000 个号码）。

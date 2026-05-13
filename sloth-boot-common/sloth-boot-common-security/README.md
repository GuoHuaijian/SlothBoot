# sloth-boot-common-security

> SlothBoot 安全工具模块，提供 AES/RSA 加解密、HMAC 签名、数据脱敏、XSS 过滤等安全能力。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-common-security</artifactId>
</dependency>
```

## 核心组件

| 组件 | 说明 |
|------|------|
| `AesUtil` | AES 对称加密/解密工具 |
| `RsaUtil` | RSA 非对称加密/解密、签名/验签工具 |
| `HmacUtil` | HMAC 签名工具（HmacSHA256 等） |
| `MaskUtil` | 数据脱敏工具（手机号、身份证、邮箱、银行卡等） |
| `XssFilter` | XSS 过滤器（由 starter-web 自动装配） |

## 使用示例

### AES 加解密

```java
String encrypted = AesUtil.encrypt("敏感数据", "your-secret-key");
String decrypted = AesUtil.decrypt(encrypted, "your-secret-key");
```

### RSA 加解密

```java
KeyPair keyPair = RsaUtil.generateKeyPair();
String encrypted = RsaUtil.encrypt("数据", keyPair.getPublic());
String decrypted = RsaUtil.decrypt(encrypted, keyPair.getPrivate());
```

### 数据脱敏

```java
MaskUtil.mobile("13812345678")     // 138****5678
MaskUtil.idCard("110101199001011234") // 110101********1234
MaskUtil.email("test@example.com")  // t***@example.com
MaskUtil.bankCard("6222021234567890") // 6222********7890
```

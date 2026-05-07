# Sloth Boot Starter OSS

对象存储统一操作组件，通过 `OssTemplate` 门面屏蔽 MinIO、阿里云 OSS、本地存储差异，提供统一的上传、下载、删除及预签名能力。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-oss</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `sloth.oss.type` | `String` | `minio` | 存储类型：`minio` / `aliyun` / `local` |
| `sloth.oss.endpoint` | `String` | - | 服务端点 URL |
| `sloth.oss.access-key` | `String` | - | AccessKey |
| `sloth.oss.secret-key` | `String` | - | SecretKey |
| `sloth.oss.bucket-name` | `String` | - | Bucket 名称 |
| `sloth.oss.region` | `String` | - | 区域（阿里云 OSS 需要） |
| `sloth.oss.domain` | `String` | - | 自定义访问域名 |

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `OssClient` | OSS 操作接口，定义 upload / download / delete / getPresignedUrl / listFiles |
| `MinioOssClient` | MinIO 实现 |
| `AliyunOssClient` | 阿里云 OSS 实现 |
| `LocalOssClient` | 本地文件系统实现（开发测试用） |
| `OssTemplate` | 门面类，委托给对应 `OssClient` 实现 |

## 配置示例

```yaml
sloth:
  oss:
    type: minio
    endpoint: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket-name: sloth-bucket
```

## 上传示例

```java
@RestController
@RequiredArgsConstructor
public class FileController {

    private final OssTemplate ossTemplate;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String path = "uploads/" + file.getOriginalFilename();
        String url = ossTemplate.upload(path, file.getInputStream());
        return Result.ok(url);
    }

    @GetMapping("/download")
    public void download(@RequestParam("path") String path,
                         HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        ossTemplate.download(path, response.getOutputStream());
    }

    @GetMapping("/presigned-url")
    public Result<String> getPresignedUrl(@RequestParam("path") String path) {
        return Result.ok(ossTemplate.getPresignedUrl(path, 60));
    }
}
```

## FAQ

**Q: 如何切换不同的 OSS 实现？**
A: 修改 `sloth.oss.type` 配置即可，支持 `minio`、`aliyun`、`local` 三种类型。

**Q: 预签名 URL 过期时间如何设置？**
A: 调用 `getPresignedUrl(path, expireMinutes)` 时第二个参数为分钟数。

package com.sloth.boot.starter.oss.core;

import com.sloth.boot.starter.oss.model.OssFile;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;

/**
 * OSS 操作门面。
 * <p>
 * 对 {@link OssClient} 的装饰器，提供操作日志记录。
 * 业务方通过注入此模板使用 OSS 能力，可替换底层实现而不影响业务代码。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class OssTemplate implements OssClient {

    private final OssClient delegate;

    /**
     * 创建 OSS 操作模板。
     *
     * @param delegate 底层 OSS 客户端实现
     */
    public OssTemplate(OssClient delegate) {
        this.delegate = delegate;
    }

    /**
     * 上传文件到 OSS。
     *
     * @param path        对象路径（key）
     * @param inputStream 文件输入流
     * @return 上传后的对象访问路径
     */
    @Override
    public String upload(String path, InputStream inputStream) {
        log.debug("[OSS] upload path={}", path);
        return delegate.upload(path, inputStream);
    }

    /**
     * 从 OSS 下载文件。
     *
     * @param path         对象路径（key）
     * @param outputStream 输出流，文件内容将写入此流
     */
    @Override
    public void download(String path, OutputStream outputStream) {
        log.debug("[OSS] download path={}", path);
        delegate.download(path, outputStream);
    }

    /**
     * 删除 OSS 上的文件。
     *
     * @param path 对象路径（key）
     */
    @Override
    public void delete(String path) {
        log.debug("[OSS] delete path={}", path);
        delegate.delete(path);
    }

    /**
     * 获取预签名访问 URL（int 类型过期时间）。
     *
     * @param path          对象路径（key）
     * @param expireMinutes 过期时间（分钟）
     * @return 预签名 URL
     */
    @Override
    public String getPresignedUrl(String path, int expireMinutes) {
        log.debug("[OSS] getPresignedUrl path={}", path);
        return delegate.getPresignedUrl(path, expireMinutes);
    }

    /**
     * 生成预签名访问 URL（Duration 类型过期时间）。
     *
     * @param objectKey 对象路径（key）
     * @param expiry    过期时长
     * @return 预签名 URL
     */
    @Override
    public String generatePresignedUrl(String objectKey, Duration expiry) {
        log.debug("[OSS] generatePresignedUrl objectKey={}", objectKey);
        return delegate.generatePresignedUrl(objectKey, expiry);
    }

    /**
     * 列出指定前缀下的所有文件。
     *
     * @param prefix 对象路径前缀
     * @return 文件列表
     */
    @Override
    public List<OssFile> listFiles(String prefix) {
        log.debug("[OSS] listFiles prefix={}", prefix);
        return delegate.listFiles(prefix);
    }
}

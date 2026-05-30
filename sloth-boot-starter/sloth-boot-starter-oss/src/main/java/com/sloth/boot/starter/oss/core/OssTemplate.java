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

    public OssTemplate(OssClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public String upload(String path, InputStream inputStream) {
        log.debug("[OSS] upload path={}", path);
        return delegate.upload(path, inputStream);
    }

    @Override
    public void download(String path, OutputStream outputStream) {
        log.debug("[OSS] download path={}", path);
        delegate.download(path, outputStream);
    }

    @Override
    public void delete(String path) {
        log.debug("[OSS] delete path={}", path);
        delegate.delete(path);
    }

    @Override
    public String getPresignedUrl(String path, int expireMinutes) {
        log.debug("[OSS] getPresignedUrl path={}", path);
        return delegate.getPresignedUrl(path, expireMinutes);
    }

    @Override
    public String generatePresignedUrl(String objectKey, Duration expiry) {
        log.debug("[OSS] generatePresignedUrl objectKey={}", objectKey);
        return delegate.generatePresignedUrl(objectKey, expiry);
    }

    @Override
    public List<OssFile> listFiles(String prefix) {
        log.debug("[OSS] listFiles prefix={}", prefix);
        return delegate.listFiles(prefix);
    }
}

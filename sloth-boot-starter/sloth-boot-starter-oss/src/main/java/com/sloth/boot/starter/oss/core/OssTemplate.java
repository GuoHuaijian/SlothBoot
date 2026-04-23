package com.sloth.boot.starter.oss.core;

import com.sloth.boot.starter.oss.model.OssFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * OSS 操作门面。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class OssTemplate implements OssClient {

    private final OssClient delegate;

    /**
     * 构造函数。
     *
     * @param delegate OSS 客户端
     */
    public OssTemplate(OssClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public String upload(String path, InputStream inputStream) {
        return delegate.upload(path, inputStream);
    }

    @Override
    public void download(String path, OutputStream outputStream) {
        delegate.download(path, outputStream);
    }

    @Override
    public void delete(String path) {
        delegate.delete(path);
    }

    @Override
    public String getPresignedUrl(String path, int expireMinutes) {
        return delegate.getPresignedUrl(path, expireMinutes);
    }

    @Override
    public List<OssFile> listFiles(String prefix) {
        return delegate.listFiles(prefix);
    }
}

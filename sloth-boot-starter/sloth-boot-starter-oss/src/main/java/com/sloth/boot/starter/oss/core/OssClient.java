package com.sloth.boot.starter.oss.core;

import com.sloth.boot.starter.oss.model.OssFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;

/**
 * OSS 客户端接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface OssClient {

    String upload(String path, InputStream inputStream);

    void download(String path, OutputStream outputStream);

    void delete(String path);

    String getPresignedUrl(String path, int expireMinutes);

    /**
     * 生成预签名访问 URL。
     *
     * @param objectKey 对象Key
     * @param expiry    过期时间
     * @return 预签名URL
     */
    String generatePresignedUrl(String objectKey, Duration expiry);

    List<OssFile> listFiles(String prefix);
}

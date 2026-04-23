package com.sloth.boot.starter.oss.core;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.sloth.boot.starter.oss.config.OssProperties;
import com.sloth.boot.starter.oss.model.OssFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 阿里云 OSS 实现。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class AliyunOssClient implements OssClient {

    private final OSS ossClient;
    private final OssProperties ossProperties;

    /**
     * 构造函数。
     *
     * @param ossClient      阿里云 OSS 客户端
     * @param ossProperties  OSS 配置
     */
    public AliyunOssClient(OSS ossClient, OssProperties ossProperties) {
        this.ossClient = ossClient;
        this.ossProperties = ossProperties;
    }

    @Override
    public String upload(String path, InputStream inputStream) {
        ossClient.putObject(ossProperties.getBucketName(), path, inputStream);
        return getPresignedUrl(path, 60);
    }

    @Override
    public void download(String path, OutputStream outputStream) {
        try (InputStream inputStream = ossClient.getObject(ossProperties.getBucketName(), path).getObjectContent()) {
            inputStream.transferTo(outputStream);
        } catch (Exception ex) {
            throw new IllegalStateException("阿里云 OSS 下载失败", ex);
        }
    }

    @Override
    public void delete(String path) {
        ossClient.deleteObject(ossProperties.getBucketName(), path);
    }

    @Override
    public String getPresignedUrl(String path, int expireMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + expireMinutes * 60L * 1000L);
        URL url = ossClient.generatePresignedUrl(new GeneratePresignedUrlRequest(ossProperties.getBucketName(), path)
                .withExpiration(expiration));
        return url.toString();
    }

    @Override
    public List<OssFile> listFiles(String prefix) {
        List<OssFile> result = new ArrayList<>();
        for (OSSObjectSummary summary : ossClient.listObjects(ossProperties.getBucketName(), prefix).getObjectSummaries()) {
            OssFile ossFile = new OssFile();
            ossFile.setName(summary.getKey());
            ossFile.setPath(summary.getKey());
            ossFile.setUrl(getPresignedUrl(summary.getKey(), 60));
            ossFile.setSize(summary.getSize());
            ossFile.setLastModified(LocalDateTime.ofInstant(Instant.ofEpochMilli(summary.getLastModified().getTime()), ZoneId.systemDefault()));
            result.add(ossFile);
        }
        return result;
    }
}

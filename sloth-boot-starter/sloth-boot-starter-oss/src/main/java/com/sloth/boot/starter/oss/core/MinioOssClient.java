package com.sloth.boot.starter.oss.core;

import com.sloth.boot.common.exception.SystemException;
import com.sloth.boot.starter.oss.config.OssProperties;
import com.sloth.boot.starter.oss.model.OssFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * MinIO OSS 实现。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class MinioOssClient implements OssClient {

    private final io.minio.MinioClient minioClient;
    private final OssProperties ossProperties;

    /**
     * 构造函数。
     *
     * @param minioClient   MinioClient
     * @param ossProperties OSS 配置
     */
    public MinioOssClient(io.minio.MinioClient minioClient, OssProperties ossProperties) {
        this.minioClient = minioClient;
        this.ossProperties = ossProperties;
    }

    @Override
    public String upload(String path, InputStream inputStream) {
        try {
            minioClient.putObject(
                io.minio.PutObjectArgs.builder()
                    .bucket(ossProperties.getBucketName())
                    .object(path)
                    .stream(inputStream, -1, 10 * 1024 * 1024)
                    .build()
            );
            return getPresignedUrl(path, ossProperties.getPresignedUrlExpiry());
        } catch (Exception ex) {
            throw SystemException.of("MinIO 上传失败", ex);
        }
    }

    @Override
    public void download(String path, OutputStream outputStream) {
        try (InputStream inputStream = minioClient.getObject(
            io.minio.GetObjectArgs.builder()
                .bucket(ossProperties.getBucketName())
                .object(path)
                .build())) {
            inputStream.transferTo(outputStream);
        } catch (Exception ex) {
            throw SystemException.of("MinIO 下载失败", ex);
        }
    }

    @Override
    public void delete(String path) {
        try {
            minioClient.removeObject(
                io.minio.RemoveObjectArgs.builder()
                    .bucket(ossProperties.getBucketName())
                    .object(path)
                    .build()
            );
        } catch (Exception ex) {
            throw SystemException.of("MinIO 删除失败", ex);
        }
    }

    @Override
    public String getPresignedUrl(String path, int expireMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                io.minio.GetPresignedObjectUrlArgs.builder().bucket(ossProperties.getBucketName()).object(path)
                    .expiry(expireMinutes * 60).method(io.minio.http.Method.GET).build());
        } catch (Exception ex) {
            throw SystemException.of("MinIO 获取预签名地址失败", ex);
        }
    }

    @Override
    public String generatePresignedUrl(String objectKey, Duration expiry) {
        try {
            return minioClient.getPresignedObjectUrl(
                io.minio.GetPresignedObjectUrlArgs.builder().bucket(ossProperties.getBucketName()).object(objectKey)
                    .expiry((int) expiry.getSeconds()).method(io.minio.http.Method.GET).build());
        } catch (Exception ex) {
            throw SystemException.of("MinIO 获取预签名地址失败", ex);
        }
    }

    @Override
    public List<OssFile> listFiles(String prefix) {
        throw new UnsupportedOperationException("MinIO listFiles not yet implemented. Use AliyunOssClient or LocalOssClient.");
    }
}

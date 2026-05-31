package com.sloth.boot.starter.oss.config;

/**
 * OSS 类型枚举。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum OssTypeEnum {

    /**
     * 本地文件系统。
     */
    LOCAL,

    /**
     * MinIO 对象存储。
     */
    MINIO,

    /**
     * 阿里云 OSS。
     */
    ALIYUN
}

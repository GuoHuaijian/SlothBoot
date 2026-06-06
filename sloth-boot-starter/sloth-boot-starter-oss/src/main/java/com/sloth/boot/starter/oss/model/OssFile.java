package com.sloth.boot.starter.oss.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * OSS 文件信息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class OssFile {

    /** 文件名 */
    private String name;
    /** 文件路径 */
    private String path;
    /** 文件访问 URL */
    private String url;
    /** 文件大小（字节） */
    private Long size;
    /** 文件 MIME 类型 */
    private String contentType;
    /** 最后修改时间 */
    private LocalDateTime lastModified;
}

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

    private String name;
    private String path;
    private String url;
    private Long size;
    private String contentType;
    private LocalDateTime lastModified;
}

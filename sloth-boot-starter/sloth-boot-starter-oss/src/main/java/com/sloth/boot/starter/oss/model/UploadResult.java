package com.sloth.boot.starter.oss.model;

import lombok.Data;

/**
 * 上传结果对象。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class UploadResult {

    /** 文件访问 URL */
    private String url;
    /** 文件存储路径 */
    private String path;
    /** 文件名 */
    private String name;
    /** 文件大小（字节） */
    private Long size;
}

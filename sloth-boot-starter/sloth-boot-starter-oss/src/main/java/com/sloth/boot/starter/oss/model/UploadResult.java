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

    private String url;
    private String path;
    private String name;
    private Long size;
}

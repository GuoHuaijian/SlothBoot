package com.sloth.boot.starter.oss.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 上传结果对象。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "文件上传结果")
public class UploadResult {

    /** 文件访问 URL */
    @Schema(description = "文件访问 URL", example = "https://oss.example.com/images/avatar.png")
    private String url;
    /** 文件存储路径 */
    @Schema(description = "文件存储路径", example = "images/2024/avatar.png")
    private String path;
    /** 文件名 */
    @Schema(description = "文件名", example = "avatar.png")
    private String name;
    /** 文件大小（字节） */
    @Schema(description = "文件大小（字节）", example = "102400")
    private Long size;
}

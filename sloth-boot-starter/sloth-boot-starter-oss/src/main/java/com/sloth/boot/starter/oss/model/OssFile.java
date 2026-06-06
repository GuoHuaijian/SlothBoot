package com.sloth.boot.starter.oss.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * OSS 文件信息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "OSS 文件信息")
public class OssFile {

    /** 文件名 */
    @Schema(description = "文件名", example = "avatar.png")
    private String name;
    /** 文件路径 */
    @Schema(description = "文件路径", example = "images/2024/avatar.png")
    private String path;
    /** 文件访问 URL */
    @Schema(description = "文件访问 URL", example = "https://oss.example.com/images/2024/avatar.png")
    private String url;
    /** 文件大小（字节） */
    @Schema(description = "文件大小（字节）", example = "102400")
    private Long size;
    /** 文件 MIME 类型 */
    @Schema(description = "文件 MIME 类型", example = "image/png")
    private String contentType;
    /** 最后修改时间 */
    @Schema(description = "最后修改时间")
    private LocalDateTime lastModified;
}

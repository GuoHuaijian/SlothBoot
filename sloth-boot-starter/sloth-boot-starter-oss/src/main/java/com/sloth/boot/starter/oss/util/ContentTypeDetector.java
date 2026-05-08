package com.sloth.boot.starter.oss.util;

import java.util.Map;

/**
 * Content-Type 自动检测工具类。
 * <p>
 * 根据文件扩展名推断 MIME 类型，用于上传时自动设置正确的 Content-Type。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ContentTypeDetector {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private static final Map<String, String> MIME_TYPES = Map.ofEntries(
        // 图片
        Map.entry("jpg", "image/jpeg"), Map.entry("jpeg", "image/jpeg"),
        Map.entry("png", "image/png"), Map.entry("gif", "image/gif"),
        Map.entry("bmp", "image/bmp"), Map.entry("webp", "image/webp"),
        Map.entry("svg", "image/svg+xml"), Map.entry("ico", "image/x-icon"),
        Map.entry("tiff", "image/tiff"), Map.entry("tif", "image/tiff"),
        // 文档
        Map.entry("pdf", "application/pdf"),
        Map.entry("doc", "application/msword"),
        Map.entry("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        Map.entry("xls", "application/vnd.ms-excel"),
        Map.entry("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        Map.entry("ppt", "application/vnd.ms-powerpoint"),
        Map.entry("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        Map.entry("csv", "text/csv"),
        // 文本
        Map.entry("txt", "text/plain"), Map.entry("html", "text/html"),
        Map.entry("htm", "text/html"), Map.entry("css", "text/css"),
        Map.entry("js", "application/javascript"), Map.entry("json", "application/json"),
        Map.entry("xml", "application/xml"), Map.entry("yaml", "text/yaml"),
        Map.entry("yml", "text/yaml"), Map.entry("md", "text/markdown"),
        // 音视频
        Map.entry("mp3", "audio/mpeg"), Map.entry("wav", "audio/wav"),
        Map.entry("ogg", "audio/ogg"), Map.entry("flac", "audio/flac"),
        Map.entry("mp4", "video/mp4"), Map.entry("avi", "video/x-msvideo"),
        Map.entry("mov", "video/quicktime"), Map.entry("wmv", "video/x-ms-wmv"),
        Map.entry("flv", "video/x-flv"), Map.entry("webm", "video/webm"),
        // 压缩
        Map.entry("zip", "application/zip"), Map.entry("rar", "application/vnd.rar"),
        Map.entry("7z", "application/x-7z-compressed"),
        Map.entry("tar", "application/x-tar"), Map.entry("gz", "application/gzip"),
        // 字体
        Map.entry("ttf", "font/ttf"), Map.entry("otf", "font/otf"),
        Map.entry("woff", "font/woff"), Map.entry("woff2", "font/woff2")
    );

    private ContentTypeDetector() {
    }

    /**
     * 根据文件名推断 Content-Type。
     *
     * @param filename 文件名（如 "image.png"、"report.pdf"）
     * @return Content-Type，无法识别时返回 application/octet-stream
     */
    public static String detect(String filename) {
        if (filename == null || filename.isEmpty()) {
            return DEFAULT_CONTENT_TYPE;
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return DEFAULT_CONTENT_TYPE;
        }
        String ext = filename.substring(dotIndex + 1).toLowerCase();
        return MIME_TYPES.getOrDefault(ext, DEFAULT_CONTENT_TYPE);
    }

    /**
     * 根据文件路径推断 Content-Type。
     *
     * @param path 文件路径（如 "/uploads/2026/05/image.png"）
     * @return Content-Type
     */
    public static String detectByPath(String path) {
        if (path == null || path.isEmpty()) {
            return DEFAULT_CONTENT_TYPE;
        }
        String filename = path.substring(path.lastIndexOf('/') + 1);
        return detect(filename);
    }
}

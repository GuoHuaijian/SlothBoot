package com.sloth.boot.common.util;

import com.sloth.boot.common.exception.SystemException;

import java.io.*;
import java.util.Set;

/**
 * 文件操作工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class FileUtil {

    private FileUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取文件扩展名（不含点号）
     *
     * @param filename 文件名
     * @return 扩展名，无扩展名返回空字符串
     */
    public static String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * 获取不含扩展名的文件名
     *
     * @param filename 文件名
     * @return 不含扩展名的文件名
     */
    public static String getNameWithoutExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return filename;
        }
        return filename.substring(0, dotIndex);
    }

    /**
     * 格式化文件大小为人类可读格式
     * <p>
     * <pre>
     * FileUtil.formatFileSize(1024)           → "1 KB"
     * FileUtil.formatFileSize(1536)           → "1.5 KB"
     * FileUtil.formatFileSize(1048576)        → "1 MB"
     * FileUtil.formatFileSize(1073741824)     → "1 GB"
     * </pre>
     *
     * @param bytes 字节数
     * @return 格式化后的文件大小字符串
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 0) {
            return "0 B";
        }
        if (bytes < 1024) {
            return bytes + " B";
        }
        String[] units = {"KB", "MB", "GB", "TB"};
        double size = bytes;
        for (String unit : units) {
            size /= 1024;
            if (size < 1024) {
                return String.format("%.1f %s", size, unit);
            }
        }
        return String.format("%.1f PB", size / 1024);
    }

    /**
     * 校验文件扩展名是否在白名单中
     *
     * @param filename    文件名
     * @param allowedExts 允许的扩展名集合（不含点号，不区分大小写）
     * @return 是否允许
     */
    public static boolean isAllowedExtension(String filename, Set<String> allowedExts) {
        if (filename == null || allowedExts == null) {
            return false;
        }
        String ext = getExtension(filename);
        for (String allowed : allowedExts) {
            if (allowed.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 读取 InputStream 为字符串
     *
     * @param inputStream 输入流
     * @param charset     字符集名称
     * @return 字符串内容
     */
    public static String readToString(InputStream inputStream, String charset) {
        if (inputStream == null) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            return sb.toString();
        } catch (IOException e) {
            throw SystemException.of("Read stream failed", e);
        }
    }

    /**
     * 写入字符串到 OutputStream
     *
     * @param content      字符串内容
     * @param outputStream 输出流
     * @param charset      字符集名称
     */
    public static void writeString(String content, OutputStream outputStream, String charset) {
        if (content == null || outputStream == null) {
            return;
        }
        try (Writer writer = new OutputStreamWriter(outputStream, charset)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw SystemException.of("Write stream failed", e);
        }
    }
}

package com.sloth.boot.generator.output;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于本地文件系统的输出器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class FileSystemOutputWriter implements OutputWriter {

    @Override
    public WriteStatus write(Path targetPath, String content, boolean override) {
        if (Files.exists(targetPath) && !override) {
            log.info("文件已存在，跳过: {}", targetPath);
            return WriteStatus.SKIPPED;
        }
        try {
            Files.createDirectories(targetPath.getParent());
            Files.writeString(targetPath, content, StandardCharsets.UTF_8);
            return WriteStatus.WRITTEN;
        } catch (IOException e) {
            throw new IllegalStateException("写入生成文件失败: " + targetPath, e);
        }
    }
}

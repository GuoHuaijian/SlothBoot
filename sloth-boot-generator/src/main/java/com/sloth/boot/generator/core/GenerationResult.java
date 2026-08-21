package com.sloth.boot.generator.core;

import java.util.ArrayList;
import java.util.List;

import com.sloth.boot.generator.output.WriteStatus;

import lombok.Getter;

/**
 * 生成结果报告：记录每个生成文件的状态，支持输出汇总日志。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class GenerationResult {

    private final List<GeneratedFile> files = new ArrayList<>();

    /**
     * 记录一个生成文件。
     */
    public void addFile(String relativePath, WriteStatus status) {
        files.add(new GeneratedFile(relativePath, status));
    }

    public int getWrittenCount() {
        return countByStatus(WriteStatus.WRITTEN);
    }

    public int getSkippedCount() {
        return countByStatus(WriteStatus.SKIPPED);
    }

    /**
     * 输出生成报告。
     *
     * @return 多行汇总文本
     */
    public String summarize() {
        StringBuilder summary = new StringBuilder();
        summary.append("代码生成完成: 写入 ").append(getWrittenCount())
            .append(" 个文件, 跳过 ").append(getSkippedCount()).append(" 个文件\n");
        for (GeneratedFile file : files) {
            summary.append("  [").append(file.status()).append("] ").append(file.relativePath()).append('\n');
        }
        return summary.toString();
    }

    private int countByStatus(WriteStatus status) {
        return (int) files.stream().filter(file -> file.status() == status).count();
    }

    /**
     * 单个生成文件记录。
     *
     * @param relativePath 相对 outputDir 的路径
     * @param status       写入状态
     */
    public record GeneratedFile(String relativePath, WriteStatus status) {
    }
}

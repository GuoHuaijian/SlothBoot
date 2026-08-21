package com.sloth.boot.generator.output;

import java.nio.file.Path;

/**
 * 生成文件输出器抽象，隔离落盘方式（文件系统 / ZIP / 内存预览等）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface OutputWriter {

    /**
     * 写入单个文件。
     *
     * @param targetPath 目标绝对路径
     * @param content    文件内容
     * @param override   目标已存在时是否覆盖
     * @return 写入状态
     */
    WriteStatus write(Path targetPath, String content, boolean override);
}

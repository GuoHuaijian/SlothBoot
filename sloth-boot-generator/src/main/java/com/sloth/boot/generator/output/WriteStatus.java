package com.sloth.boot.generator.output;

/**
 * 文件写入状态。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum WriteStatus {

    /** 新写入 */
    WRITTEN,

    /** 已存在且未开启覆盖，跳过 */
    SKIPPED
}

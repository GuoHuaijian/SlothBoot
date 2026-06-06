package com.sloth.boot.starter.monitor.service;

import com.sloth.boot.starter.monitor.model.JvmInfo;

/**
 * JVM 信息采集能力接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface JvmInfoProvider {

    /**
     * 获取 JVM 详细信息。
     *
     * @return JVM 详细信息
     */
    JvmInfo getJvmInfo();
}

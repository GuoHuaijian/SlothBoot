package com.sloth.boot.common.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生命周期管理基类
 * <p>
 * 管理初始化状态，防止重复初始化，支持优雅关闭。子类实现 {@link #doInit} 和 {@link #doDestroy}。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public abstract class AbstractLifecycle implements Lifecycle {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private volatile boolean initialized = false;

    @Override
    public final void init() {
        if (initialized) {
            log.warn("{} 已初始化，跳过重复初始化", getClass().getSimpleName());
            return;
        }
        synchronized (this) {
            if (initialized) {
                return;
            }
            log.info("{} 正在初始化...", getClass().getSimpleName());
            doInit();
            initialized = true;
            log.info("{} 初始化完成", getClass().getSimpleName());
        }
    }

    @Override
    public final void destroy() {
        if (!initialized) {
            return;
        }
        synchronized (this) {
            if (!initialized) {
                return;
            }
            log.info("{} 正在销毁...", getClass().getSimpleName());
            doDestroy();
            initialized = false;
            log.info("{} 已销毁", getClass().getSimpleName());
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 子类实现初始化逻辑
     */
    protected abstract void doInit();

    /**
     * 子类实现销毁逻辑
     */
    protected abstract void doDestroy();
}

package com.sloth.boot.common.lifecycle;

/**
 * 生命周期接口
 * <p>
 * 定义组件的初始化和销毁行为。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface Lifecycle {

    /**
     * 初始化
     */
    void init();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 是否已初始化
     *
     * @return 是否已初始化
     */
    boolean isInitialized();
}

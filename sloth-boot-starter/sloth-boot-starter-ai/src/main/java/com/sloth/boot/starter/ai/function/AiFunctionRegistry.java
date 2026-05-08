package com.sloth.boot.starter.ai.function;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * AI 函数调用注册中心。
 * <p>
 * 管理可被 LLM 调用的函数实例，按名称索引。 通过 {@code sloth.ai.function.enabled=true} 开启。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class AiFunctionRegistry {

    private final Map<String, Function<?, ?>> functions = new ConcurrentHashMap<>();

    /**
     * 注册一个函数。
     *
     * @param name     函数名称（LLM 调用时使用）
     * @param function 函数实例
     */
    public void register(String name, Function<?, ?> function) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("函数名称不能为空");
        }
        if (function == null) {
            throw new IllegalArgumentException("函数实例不能为空");
        }
        functions.put(name, function);
        log.info("[AI] 注册函数: {}", name);
    }

    /**
     * 获取指定名称的函数。
     *
     * @param name 函数名称
     * @return 函数实例，不存在时返回 {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T, R> Function<T, R> getFunction(String name) {
        return (Function<T, R>) functions.get(name);
    }

    /**
     * 获取所有已注册的函数（不可修改视图）。
     *
     * @return 函数映射
     */
    public Map<String, Function<?, ?>> getAllFunctions() {
        return Collections.unmodifiableMap(functions);
    }
}

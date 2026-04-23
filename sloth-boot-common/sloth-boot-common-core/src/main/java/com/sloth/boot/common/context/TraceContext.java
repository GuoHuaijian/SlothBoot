package com.sloth.boot.common.context;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 追踪上下文
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@EqualsAndHashCode
public class TraceContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 使用 TransmittableThreadLocal 存储追踪信息
     */
    private static final ThreadLocal<TraceInfo> TRACE_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 追踪信息
     */
    @Data
    public static class TraceInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 追踪ID
         */
        private String traceId;

        /**
         * 跨度ID
         */
        private String spanId;

        /**
         * 获取追踪ID
         *
         * @return 追踪ID
         */
        public String getTraceId() {
            return traceId;
        }

        /**
         * 获取跨度ID
         *
         * @return 跨度ID
         */
        public String getSpanId() {
            return spanId;
        }
    }

    /**
     * 设置追踪信息
     *
     * @param traceInfo 追踪信息
     */
    public static void set(TraceInfo traceInfo) {
        TRACE_THREAD_LOCAL.set(traceInfo);
    }

    /**
     * 获取追踪信息
     *
     * @return 追踪信息
     */
    public static TraceInfo get() {
        return TRACE_THREAD_LOCAL.get();
    }

    /**
     * 获取追踪ID
     *
     * @return 追踪ID
     */
    public static String getTraceId() {
        TraceInfo traceInfo = get();
        return traceInfo != null ? traceInfo.getTraceId() : null;
    }

    /**
     * 获取跨度ID
     *
     * @return 跨度ID
     */
    public static String getSpanId() {
        TraceInfo traceInfo = get();
        return traceInfo != null ? traceInfo.getSpanId() : null;
    }

    /**
     * 生成追踪ID
     *
     * @return 追踪ID
     */
    public static String generateTraceId() {
        return StrUtil.uuid().replace("-", "").substring(0, 16);
    }

    /**
     * 清除追踪信息
     */
    public static void clear() {
        TRACE_THREAD_LOCAL.remove();
    }
}

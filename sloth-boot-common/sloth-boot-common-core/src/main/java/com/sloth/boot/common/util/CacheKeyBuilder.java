package com.sloth.boot.common.util;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 缓存 Key 构建器
 * <p>
 * 统一缓存 key 的构建格式，确保 key 格式一致性。
 * <p>
 * 使用示例：
 * <pre>
 * String key = CacheKeyBuilder.build("sloth", "lock", "order:123");
 * // "sloth:lock:order:123"
 *
 * String key = CacheKeyBuilder.build("sloth", "user", userId.toString());
 * // "sloth:user:1001"
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class CacheKeyBuilder {

    private CacheKeyBuilder() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final String SEPARATOR = ":";

    /**
     * 构建缓存 key（用 : 分隔）
     *
     * @param prefix 前缀
     * @param parts  key 部分
     * @return 缓存 key
     */
    public static String build(String prefix, String... parts) {
        if (prefix == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(prefix);
        if (parts != null) {
            for (String part : parts) {
                if (part != null && !part.isEmpty()) {
                    sb.append(SEPARATOR).append(part);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 构建带参数的缓存 key
     *
     * @param prefix 前缀
     * @param params 参数 Map（按 key 排序，确保一致性）
     * @return 缓存 key
     */
    public static String build(String prefix, Map<String, String> params) {
        if (prefix == null) {
            return "";
        }
        if (params == null || params.isEmpty()) {
            return prefix;
        }
        String paramStr = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        return prefix + SEPARATOR + paramStr;
    }

    /**
     * 构建用户相关缓存 key
     *
     * @param prefix 前缀
     * @param userId 用户 ID
     * @return 缓存 key
     */
    public static String buildUserKey(String prefix, Long userId) {
        return build(prefix, "user", String.valueOf(userId));
    }
}

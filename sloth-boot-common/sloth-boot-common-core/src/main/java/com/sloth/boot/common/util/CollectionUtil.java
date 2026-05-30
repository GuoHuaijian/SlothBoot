package com.sloth.boot.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 集合工具类
 * <p>
 * 提供常用的集合操作方法，所有方法均为 null 安全。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class CollectionUtil {

    private CollectionUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否非空
     *
     * @param collection 集合
     * @return 是否非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断 Map 是否为空
     *
     * @param map Map
     * @return 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断 Map 是否非空
     *
     * @param map Map
     * @return 是否非空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 将列表按指定大小分割为多个子列表
     * <p>
     * <pre>
     * List&lt;Integer&gt; list = Arrays.asList(1, 2, 3, 4, 5);
     * List&lt;List&lt;Integer&gt;&gt; parts = CollectionUtil.partition(list, 2);
     * // parts: [[1, 2], [3, 4], [5]]
     * </pre>
     *
     * @param list 列表
     * @param size 每个子列表的最大大小
     * @param <T>  元素类型
     * @return 分割后的子列表
     * @throws IllegalArgumentException 如果 list 为 null 或 size <= 0
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (list == null) {
            throw new IllegalArgumentException("参数 list 不能为 null");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("参数 size 必须大于 0");
        }
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(new ArrayList<>(list.subList(i, Math.min(i + size, list.size()))));
        }
        return result;
    }

    /**
     * 转换集合中的元素
     * <p>
     * <pre>
     * List&lt;String&gt; names = Arrays.asList("Alice", "Bob");
     * List&lt;Integer&gt; lengths = CollectionUtil.convert(names, String::length);
     * // lengths: [5, 3]
     * </pre>
     *
     * @param collection 集合
     * @param mapper     转换函数
     * @param <T>        源类型
     * @param <R>        目标类型
     * @return 转换后的列表
     */
    public static <T, R> List<R> convert(Collection<T> collection, Function<T, R> mapper) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 过滤集合中的元素
     *
     * @param collection 集合
     * @param predicate  过滤条件
     * @param <T>        元素类型
     * @return 过滤后的列表
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 按指定 key 去重
     * <p>
     * 保留每个 key 首次出现的元素。
     * <pre>
     * List&lt;User&gt; users = ...;
     * List&lt;User&gt; distinct = CollectionUtil.distinctByKey(users, User::getId);
     * </pre>
     *
     * @param collection   集合
     * @param keyExtractor key 提取函数
     * @param <T>          元素类型
     * @return 去重后的列表
     */
    public static <T> List<T> distinctByKey(Collection<T> collection, Function<T, ?> keyExtractor) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return collection.stream()
            .filter(item -> seen.add(keyExtractor.apply(item)))
            .collect(Collectors.toList());
    }

    /**
     * 按指定 classifier 对集合进行分组
     * <p>
     * <pre>
     * List&lt;String&gt; words = Arrays.asList("apple", "ant", "banana", "avocado");
     * Map&lt;Character, List&lt;String&gt;&gt; grouped = CollectionUtil.groupBy(words, s -&gt; s.charAt(0));
     * // grouped: {a=[apple, ant, avocado], b=[banana]}
     * </pre>
     *
     * @param collection 集合
     * @param classifier 分组函数
     * @param <T>        元素类型
     * @param <K>        key 类型
     * @return 分组后的 Map
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> collection, Function<T, K> classifier) {
        if (isEmpty(collection)) {
            return new HashMap<>();
        }
        return collection.stream().collect(Collectors.groupingBy(classifier));
    }

    /**
     * 将集合转换为 Map
     *
     * @param collection  集合
     * @param keyMapper   key 映射函数
     * @param valueMapper value 映射函数
     * @param <T>         元素类型
     * @param <K>         key 类型
     * @param <V>         value 类型
     * @return 转换后的 Map
     * @throws IllegalStateException 如果存在重复的 key
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> collection,
                                            Function<T, K> keyMapper,
                                            Function<T, V> valueMapper) {
        if (isEmpty(collection)) {
            return new HashMap<>();
        }
        return collection.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * 计算两个集合的并集
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @param <T>   元素类型
     * @return 并集列表
     */
    public static <T> List<T> union(Collection<T> coll1, Collection<T> coll2) {
        List<T> result = new ArrayList<>();
        if (isNotEmpty(coll1)) {
            result.addAll(coll1);
        }
        if (isNotEmpty(coll2)) {
            result.addAll(coll2);
        }
        return result;
    }

    /**
     * 计算两个集合的交集
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @param <T>   元素类型
     * @return 交集列表
     */
    public static <T> List<T> intersection(Collection<T> coll1, Collection<T> coll2) {
        if (isEmpty(coll1) || isEmpty(coll2)) {
            return new ArrayList<>();
        }
        Set<T> set2 = new HashSet<>(coll2);
        return coll1.stream().filter(set2::contains).collect(Collectors.toList());
    }

    /**
     * 计算两个集合的差集（在 coll1 中但不在 coll2 中的元素）
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @param <T>   元素类型
     * @return 差集列表
     */
    public static <T> List<T> difference(Collection<T> coll1, Collection<T> coll2) {
        if (isEmpty(coll1)) {
            return new ArrayList<>();
        }
        if (isEmpty(coll2)) {
            return new ArrayList<>(coll1);
        }
        Set<T> set2 = new HashSet<>(coll2);
        return coll1.stream().filter(item -> !set2.contains(item)).collect(Collectors.toList());
    }

    /**
     * 将 null 列表转为空列表
     *
     * @param list 列表
     * @param <T>  元素类型
     * @return 非 null 的列表
     */
    public static <T> List<T> nullToEmpty(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 将 null 集合转为空集合
     *
     * @param set 集合
     * @param <T> 元素类型
     * @return 非 null 的集合
     */
    public static <T> Set<T> nullToEmpty(Set<T> set) {
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * 将 null Map 转为空 Map
     *
     * @param map Map
     * @param <K> key 类型
     * @param <V> value 类型
     * @return 非 null 的 Map
     */
    public static <K, V> Map<K, V> nullToEmpty(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }
}

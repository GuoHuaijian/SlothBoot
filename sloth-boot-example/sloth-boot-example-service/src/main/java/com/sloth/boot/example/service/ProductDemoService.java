package com.sloth.boot.example.service;

import com.sloth.boot.common.security.xss.XssCleaner;
import com.sloth.boot.example.dto.ProductCreateRequest;
import com.sloth.boot.example.dto.ProductDTO;
import com.sloth.boot.starter.redis.bloom.RedisBloomFilter;
import com.sloth.boot.starter.redis.core.RedisCacheUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 商品演示服务 - 展示布隆过滤器、缓存策略、分布式ID、XSS防护等能力
 */
@Slf4j
@Service
public class ProductDemoService {

    private final RedisCacheUtil cacheUtil;

    /** 布隆过滤器（Redis 未启用时为 null） */
    private final RedisBloomFilter<String> bloomFilter;

    /** 内存商品存储 */
    private final ConcurrentHashMap<Long, ProductDTO> products = new ConcurrentHashMap<>();

    /** 自增ID生成器（基础值 + Redis 自增） */
    private final AtomicLong idGenerator = new AtomicLong(1000);

    public ProductDemoService(RedisCacheUtil cacheUtil,
                              @Autowired(required = false) RedisBloomFilter<String> bloomFilter) {
        this.cacheUtil = cacheUtil;
        this.bloomFilter = bloomFilter;
    }

    @PostConstruct
    public void init() {
        String[][] demoData = {
                {"1", "MacBook Pro", "Apple MacBook Pro 14英寸 M4芯片", "14999.00", "100", "电脑"},
                {"2", "iPhone 16", "Apple iPhone 16 Pro Max 256GB", "9999.00", "200", "手机"},
                {"3", "Java 编程思想", "Java经典著作 第4版", "108.00", "500", "图书"},
                {"4", "Spring实战", "Spring Boot 3权威指南", "89.00", "300", "图书"},
                {"5", "HUAWEI MateBook", "华为MateBook X Pro 2024", "11999.00", "80", "电脑"},
                {"6", "AirPods Pro", "Apple AirPods Pro 第2代", "1899.00", "150", "配件"},
                {"7", "机械键盘", "HHKB Professional Hybrid Type-S", "2199.00", "60", "配件"},
                {"8", "显示器", "Dell U2723QE 4K USB-C 显示器", "3999.00", "120", "电脑"},
                {"9", "Redis设计与实现", "黄健宏著 深入理解Redis", "79.00", "400", "图书"},
                {"10", "Samsung Galaxy S25", "三星Galaxy S25 Ultra 512GB", "10999.00", "150", "手机"},
        };

        for (String[] data : demoData) {
            Long id = Long.parseLong(data[0]);
            ProductDTO product = new ProductDTO();
            product.setId(id);
            product.setName(data[1]);
            product.setDescription(data[2]);
            product.setPrice(new BigDecimal(data[3]));
            product.setStock(Integer.parseInt(data[4]));
            product.setCategory(data[5]);
            product.setCreateTime(LocalDateTime.now());
            products.put(id, product);

            // 注册到布隆过滤器
            if (bloomFilter != null) {
                bloomFilter.add(String.valueOf(id));
            }
        }

        log.info("商品演示数据初始化完成, 共 {} 个商品", products.size());
    }

    /**
     * 查询商品 - 布隆过滤器穿透防护 + 逻辑过期缓存
     */
    public ProductDTO getProduct(Long id) {
        // 布隆过滤器拦截不存在的key，防止缓存穿透
        if (bloomFilter != null && !bloomFilter.mightContain(String.valueOf(id))) {
            log.debug("布隆过滤器拦截: id={}", id);
            return null;
        }

        return cacheUtil.getWithLogicalExpire("product:" + id, ProductDTO.class,
                () -> products.get(id), Duration.ofMinutes(30));
    }

    /**
     * 查询全部商品
     */
    public List<ProductDTO> listProducts() {
        return new ArrayList<>(products.values());
    }

    /**
     * 创建商品 - 生成分布式ID + XSS清洗
     */
    public ProductDTO createProduct(ProductCreateRequest request) {
        Long id = idGenerator.incrementAndGet() + cacheUtil.increment("demo:product:id", 1);

        ProductDTO product = new ProductDTO();
        product.setId(id);
        product.setName(request.getName());
        // 对描述内容做XSS清洗，防止存储型XSS
        product.setDescription(XssCleaner.cleanText(request.getDescription()));
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setCreateTime(LocalDateTime.now());

        products.put(id, product);

        // 注册到布隆过滤器
        if (bloomFilter != null) {
            bloomFilter.add(String.valueOf(id));
        }

        // 写入缓存
        cacheUtil.set("product:" + id, product, Duration.ofMinutes(30));

        log.info("创建商品成功: id={}, name={}", id, product.getName());
        return product;
    }

    /**
     * 更新商品
     */
    public ProductDTO updateProduct(Long id, ProductCreateRequest request) {
        ProductDTO product = products.get(id);
        if (product == null) {
            return null;
        }

        product.setName(request.getName());
        product.setDescription(XssCleaner.cleanText(request.getDescription()));
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());

        products.put(id, product);
        cacheUtil.set("product:" + id, product, Duration.ofMinutes(30));

        log.info("更新商品成功: id={}", id);
        return product;
    }

    /**
     * 删除商品
     */
    public boolean deleteProduct(Long id) {
        ProductDTO removed = products.remove(id);
        if (removed != null) {
            cacheUtil.delete("product:" + id);
            log.info("删除商品成功: id={}", id);
            return true;
        }
        return false;
    }

    /**
     * 获取商品排行榜（ZSet 排名 Top10）
     */
    public Set<Object> getRank() {
        return cacheUtil.zRange("product:rank", 0, 9);
    }

    /**
     * 为商品投票（写入 ZSet）
     */
    public void voteProduct(Long productId) {
        cacheUtil.zAdd("product:rank", String.valueOf(productId), Math.random() * 100);
        log.info("商品投票: productId={}", productId);
    }

    /**
     * 演示三种缓存策略的性能对比
     */
    public Map<String, Object> demoCacheStrategies() {
        String key = "demo:key";
        String dbValue = "Hello SlothBoot";

        // 模拟慢查询的供应商
        java.util.function.Supplier<String> slowSupplier = () -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return dbValue;
        };

        Map<String, Object> result = new LinkedHashMap<>();

        // 策略1: 基础缓存 get + set
        long start1 = System.currentTimeMillis();
        String val1 = cacheUtil.get(key, String.class);
        if (val1 == null) {
            val1 = slowSupplier.get();
            cacheUtil.set(key, val1, Duration.ofMinutes(10));
        }
        long time1 = System.currentTimeMillis() - start1;
        result.put("basicCache", Map.of("value", val1, "timeMs", time1));

        // 策略2: getOrLoad（带空值保护）
        long start2 = System.currentTimeMillis();
        String val2 = cacheUtil.getOrLoad(key, String.class, slowSupplier, Duration.ofMinutes(10));
        long time2 = System.currentTimeMillis() - start2;
        result.put("getOrLoad", Map.of("value", val2, "timeMs", time2));

        // 策略3: 逻辑过期（异步重建）
        long start3 = System.currentTimeMillis();
        String val3 = cacheUtil.getWithLogicalExpire(key, String.class, slowSupplier, Duration.ofMinutes(10));
        long time3 = System.currentTimeMillis() - start3;
        result.put("logicalExpire", Map.of("value", val3, "timeMs", time3));

        return result;
    }

    /**
     * 获取布隆过滤器统计信息
     */
    public Map<String, Object> getBloomStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        if (bloomFilter != null) {
            stats.put("count", bloomFilter.count());
            stats.put("expectedInsertions", bloomFilter.getExpectedInsertions());
            stats.put("falsePositiveProbability", bloomFilter.getFalsePositiveProbability());
        } else {
            stats.put("available", false);
        }
        return stats;
    }

    /**
     * 重置布隆过滤器
     */
    public void resetBloom() {
        if (bloomFilter != null) {
            bloomFilter.reset();
            // 重新注册现有商品
            products.keySet().forEach(id -> bloomFilter.add(String.valueOf(id)));
            log.info("布隆过滤器已重置, 重新注册 {} 个商品", products.size());
        }
    }
}

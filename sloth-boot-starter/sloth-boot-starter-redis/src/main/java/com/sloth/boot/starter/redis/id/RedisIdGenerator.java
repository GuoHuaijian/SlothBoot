package com.sloth.boot.starter.redis.id;

import com.sloth.boot.starter.redis.config.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的分布式 ID 生成器。
 * <p>
 * ID 格式：{prefix}_{yyyyMMdd}_{workerId}_{sequence}
 * 例如：sloth_20260508_0_0001
 * <p>
 * 使用 Redis INCR 原子操作保证序列号唯一性，
 * 按日期分键避免 key 膨胀。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class RedisIdGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisProperties redisProperties;

    /**
     * 生成下一个分布式 ID。
     *
     * @return 分布式唯一 ID
     */
    public String nextId() {
        RedisProperties.IdGenerator config = redisProperties.getIdGenerator();
        String date = LocalDate.now().format(DATE_FMT);
        String key = redisProperties.getKeyPrefix() + "id:" + config.getPrefix() + ":" + date;

        Long sequence = stringRedisTemplate.opsForValue().increment(key);
        if (sequence == null) {
            sequence = 1L;
        }

        // 首次设置 key 过期时间为 2 天，避免 key 堆积
        if (sequence == 1L) {
            stringRedisTemplate.expire(key, 2, TimeUnit.DAYS);
        }

        return config.getPrefix() + "_" + date + "_" + config.getWorkerId() + "_" + String.format("%04d", sequence);
    }

    /**
     * 生成纯数字分布式 ID（适合数据库主键）。
     * <p>
     * 格式：{yyyyMMdd}{workerId}{sequence}，例如 2026050800001
     *
     * @return 纯数字 ID
     */
    public long nextNumericId() {
        RedisProperties.IdGenerator config = redisProperties.getIdGenerator();
        String date = LocalDate.now().format(DATE_FMT);
        String key = redisProperties.getKeyPrefix() + "id:num:" + config.getPrefix() + ":" + date;

        Long sequence = stringRedisTemplate.opsForValue().increment(key);
        if (sequence == null) {
            sequence = 1L;
        }

        if (sequence == 1L) {
            stringRedisTemplate.expire(key, 2, TimeUnit.DAYS);
        }

        // 日期8位 + 机器号4位 + 序列号4位 = 16位数字
        String workerPart = String.format("%04d", config.getWorkerId());
        String seqPart = String.format("%04d", sequence % 10000);
        return Long.parseLong(date + workerPart + seqPart);
    }
}

package com.sloth.boot.starter.redis.id;

import com.sloth.boot.common.exception.SystemException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.starter.redis.config.RedisProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

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
@Slf4j
public class RedisIdGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int SEQ_MODULO = 10000;
    private static final long EXPIRE_SECONDS = 2 * 24 * 60 * 60;

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisProperties redisProperties;
    private final DefaultRedisScript<Long> incrScript;

    public RedisIdGenerator(StringRedisTemplate stringRedisTemplate, RedisProperties redisProperties,
                            ResourceScriptSource idGeneratorScriptSource) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisProperties = redisProperties;
        this.incrScript = buildRedisScript(idGeneratorScriptSource);
    }

    /**
     * 生成下一个分布式 ID。
     *
     * @return 分布式唯一 ID
     */
    public String nextId() {
        RedisProperties.IdGenerator config = redisProperties.getIdGenerator();
        String date = LocalDate.now().format(DATE_FMT);
        String key = redisProperties.getKeyPrefix() + "id:" + config.getPrefix() + ":" + date;

        Long sequence = executeIncrScript(key);

        return config.getPrefix() + "_" + date + "_" + config.getWorkerId() + "_"
            + String.format("%04d", wrapSequence(sequence));
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

        Long sequence = executeIncrScript(key);

        // 日期8位 + 机器号4位 + 序列号4位 = 16位数字
        String workerPart = String.format("%04d", config.getWorkerId());
        String seqPart = String.format("%04d", wrapSequence(sequence));
        return Long.parseLong(date + workerPart + seqPart);
    }

    /**
     * 通过 Lua 脚本原子执行 INCR + EXPIRE。
     *
     * @param key Redis key
     * @return 递增后的序列号
     */
    private Long executeIncrScript(String key) {
        Long sequence = stringRedisTemplate.execute(incrScript, Collections.singletonList(key),
            String.valueOf(EXPIRE_SECONDS));
        return sequence != null ? sequence : 1L;
    }

    /**
     * 将序列号包装到 1-9999 范围，避免出现 0000。
     *
     * @param sequence 原始序列号
     * @return 包装后的序列号（1~9999）
     */
    private long wrapSequence(long sequence) {
        return ((sequence - 1) % SEQ_MODULO) + 1;
    }

    private static DefaultRedisScript<Long> buildRedisScript(ResourceScriptSource scriptSource) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        try {
            redisScript.setScriptText(scriptSource.getScriptAsString());
        } catch (IOException ex) {
            throw new SystemException(GlobalErrorCode.INTERNAL_ERROR, ex);
        }
        return redisScript;
    }
}

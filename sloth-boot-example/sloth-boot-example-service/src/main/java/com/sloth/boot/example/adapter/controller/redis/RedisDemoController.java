package com.sloth.boot.example.adapter.controller.redis;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.redis.RedisDemoCommand;
import com.sloth.boot.example.application.model.event.order.OrderStatusEvent;
import com.sloth.boot.example.application.model.vo.product.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis 能力演示接口。
 * <p>
 * 演示缓存策略、布隆过滤器、ZSet 排行榜、Pub/Sub 事件等 Redis 高级能力。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "Redis 能力", description = "缓存策略、布隆过滤器、ZSet排行榜、Pub/Sub 事件")
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisDemoController {

    private final RedisDemoCommand redisDemoCommand;

    @Operation(summary = "查询商品（布隆过滤器 + 逻辑过期缓存）", description = "布隆过滤器拦截不存在的请求，逻辑过期缓存防击穿")
    @Parameter(name = "id", description = "商品ID", required = true, example = "1")
    @GetMapping("/product/{id}")
    public R<ProductVO> getProduct(@PathVariable Long id) {
        ProductVO product = redisDemoCommand.getProduct(id);
        if (product == null) {
            return R.fail("商品不存在（布隆过滤器拦截或缓存未命中）");
        }
        return R.ok(product);
    }

    @Operation(summary = "缓存策略演示", description = "对比演示三种缓存策略：基础缓存、getOrLoad、逻辑过期")
    @GetMapping("/cache/demo")
    public R<Map<String, Object>> demoCacheStrategies() {
        return R.ok(redisDemoCommand.demoCacheStrategies());
    }

    @Operation(summary = "布隆过滤器统计", description = "查看布隆过滤器的插入数量和误判率配置")
    @GetMapping("/bloom/stats")
    public R<Map<String, Object>> getBloomStats() {
        return R.ok(redisDemoCommand.getBloomStats());
    }

    @Operation(summary = "重置布隆过滤器", description = "清空并重建布隆过滤器，重新注册所有商品ID")
    @PostMapping("/bloom/reset")
    public R<String> resetBloom() {
        redisDemoCommand.resetBloom();
        return R.ok("布隆过滤器已重置");
    }

    @Operation(summary = "商品排行榜", description = "基于 Redis ZSet 的商品投票排行榜 Top10")
    @GetMapping("/rank")
    public R<Set<Object>> getRank() {
        return R.ok(redisDemoCommand.getRank());
    }

    @Operation(summary = "商品投票", description = "为指定商品投票，使用 Redis ZSet 实时更新排名")
    @Parameter(name = "productId", description = "商品ID", required = true)
    @PostMapping("/rank/vote")
    public R<String> voteProduct(@RequestParam Long productId) {
        redisDemoCommand.voteProduct(productId);
        return R.ok("投票成功");
    }

    @Operation(summary = "限流测试", description = "10秒内最多5次请求，超出返回限流提示")
    @GetMapping("/rate-limit-test")
    @com.sloth.boot.starter.redis.annotation.RateLimit(count = 5, period = 10, message = "10秒内最多5次请求")
    public R<String> rateLimitTest() {
        return R.ok("请求成功");
    }

    @Operation(summary = "获取订单事件", description = "获取通过 Redis Pub/Sub 接收到的最近订单状态变更事件")
    @Parameter(name = "count", description = "返回事件数量", example = "20")
    @GetMapping("/pubsub/events")
    public R<List<OrderStatusEvent>> getRecentEvents(@RequestParam(defaultValue = "20") int count) {
        return R.ok(redisDemoCommand.getRecentEvents(count));
    }
}

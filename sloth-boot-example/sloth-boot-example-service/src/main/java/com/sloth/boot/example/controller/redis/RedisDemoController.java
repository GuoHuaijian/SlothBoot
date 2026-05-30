package com.sloth.boot.example.controller.redis;

import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.model.order.dto.OrderDTO;
import com.sloth.boot.example.model.order.event.OrderStatusEvent;
import com.sloth.boot.example.model.order.request.OrderCreateRequest;
import com.sloth.boot.example.model.product.dto.ProductDTO;
import com.sloth.boot.example.model.product.request.ProductCreateRequest;
import com.sloth.boot.example.service.redis.RedisDemoService;
import com.sloth.boot.starter.idempotent.annotation.Idempotent;
import com.sloth.boot.starter.redis.annotation.DistributedLock;
import com.sloth.boot.starter.redis.annotation.RateLimit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis 能力演示接口
 * <p>
 * 合并商品管理、订单管理及全部 Redis 能力演示，包括：
 * 布隆过滤器、逻辑过期缓存、分布式锁、幂等、限流、ZSet 排行榜、Pub/Sub 事件
 */
@Tag(name = "Redis 能力", description = "演示 Redis 高级能力：布隆过滤器、缓存策略、分布式锁、幂等、限流、ZSet排行榜、Pub/Sub 事件")
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisDemoController {

    private final RedisDemoService redisDemoService;

    // ==================== 商品接口 ====================

    @Operation(summary = "查询商品", description = "布隆过滤器拦截不存在的请求，防止缓存穿透，逻辑过期缓存防击穿")
    @Parameter(name = "id", description = "商品ID", required = true, example = "1")
    @GetMapping("/product/{id}")
    public R<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO product = redisDemoService.getProduct(id);
        if (product == null) {
            return R.fail("商品不存在（布隆过滤器拦截或缓存未命中）");
        }
        return R.ok(product);
    }

    @Operation(summary = "查询商品列表", description = "查询全部商品列表")
    @OperateLog(module = "商品管理", description = "查询商品列表", type = OperateTypeEnum.QUERY)
    @GetMapping("/product/list")
    public R<List<ProductDTO>> listProducts() {
        return R.ok(redisDemoService.listProducts());
    }

    @Operation(summary = "创建商品", description = "创建商品，输入内容自动进行 XSS 清洗，ID 注册到布隆过滤器")
    @OperateLog(module = "商品管理", description = "创建商品", type = OperateTypeEnum.CREATE)
    @PostMapping("/product")
    public R<ProductDTO> createProduct(@RequestBody ProductCreateRequest request) {
        return R.ok(redisDemoService.createProduct(request));
    }

    @Operation(summary = "删除商品", description = "删除商品并清理缓存和布隆过滤器")
    @Parameter(name = "id", description = "商品ID", required = true)
    @OperateLog(module = "商品管理", description = "删除商品", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/product/{id}")
    public R<String> deleteProduct(@PathVariable Long id) {
        redisDemoService.deleteProduct(id);
        return R.ok("删除成功");
    }

    // ==================== 订单接口 ====================

    @Operation(summary = "创建订单", description = "下单时使用分布式锁防止并发超卖，幂等注解防止重复提交")
    @DistributedLock(key = "'order:create:' + #request.productId", waitTime = 5, leaseTime = 30, message = "下单处理中，请勿重复操作")
    @Idempotent(timeout = 30, message = "请勿重复提交订单")
    @PostMapping("/order/create")
    public R<OrderDTO> createOrder(@RequestBody OrderCreateRequest request) {
        long userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : 1L;
        return R.ok(redisDemoService.createOrder(request, userId));
    }

    @Operation(summary = "查询订单列表", description = "查询全部订单，按创建时间倒序")
    @OperateLog(module = "订单管理", description = "查询订单列表", type = OperateTypeEnum.QUERY)
    @GetMapping("/order/list")
    public R<List<OrderDTO>> listOrders() {
        return R.ok(redisDemoService.listOrders());
    }

    @Operation(summary = "支付订单", description = "使用分布式锁保证同一订单支付操作串行执行")
    @Parameter(name = "id", description = "订单ID", required = true)
    @DistributedLock(key = "'order:' + #id", waitTime = 5, leaseTime = 30, message = "支付处理中")
    @PutMapping("/order/{id}/pay")
    public R<OrderDTO> payOrder(@PathVariable Long id) {
        return R.ok(redisDemoService.payOrder(id));
    }

    // ==================== Redis 能力演示 ====================

    @Operation(summary = "缓存策略演示", description = "对比演示三种缓存策略：基础缓存、getOrLoad、逻辑过期")
    @GetMapping("/cache/demo")
    public R<Map<String, Object>> demoCacheStrategies() {
        return R.ok(redisDemoService.demoCacheStrategies());
    }

    @Operation(summary = "布隆过滤器统计", description = "查看布隆过滤器的插入数量和误判率配置")
    @GetMapping("/bloom/stats")
    public R<Map<String, Object>> getBloomStats() {
        return R.ok(redisDemoService.getBloomStats());
    }

    @Operation(summary = "重置布隆过滤器", description = "清空并重建布隆过滤器，重新注册所有商品ID")
    @PostMapping("/bloom/reset")
    public R<String> resetBloom() {
        redisDemoService.resetBloom();
        return R.ok("布隆过滤器已重置");
    }

    @Operation(summary = "商品排行榜", description = "基于 Redis ZSet 的商品投票排行榜 Top10")
    @GetMapping("/rank")
    public R<Set<Object>> getRank() {
        return R.ok(redisDemoService.getRank());
    }

    @Operation(summary = "商品投票", description = "为指定商品投票，使用 Redis ZSet 实时更新排名")
    @Parameter(name = "productId", description = "商品ID", required = true)
    @PostMapping("/rank/vote")
    public R<String> voteProduct(@RequestParam Long productId) {
        redisDemoService.voteProduct(productId);
        return R.ok("投票成功");
    }

    @Operation(summary = "限流测试", description = "10秒内最多5次请求，超出返回限流提示")
    @GetMapping("/rate-limit-test")
    @RateLimit(count = 5, period = 10, message = "10秒内最多5次请求")
    public R<String> rateLimitTest() {
        return R.ok("请求成功");
    }

    @Operation(summary = "获取订单事件", description = "获取通过 Redis Pub/Sub 接收到的最近订单状态变更事件")
    @Parameter(name = "count", description = "返回事件数量", example = "20")
    @GetMapping("/pubsub/events")
    public R<List<OrderStatusEvent>> getRecentEvents(@RequestParam(defaultValue = "20") int count) {
        return R.ok(redisDemoService.getRecentEvents(count));
    }
}

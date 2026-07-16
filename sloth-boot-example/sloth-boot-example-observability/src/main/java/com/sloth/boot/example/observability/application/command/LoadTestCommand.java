package com.sloth.boot.example.observability.application.command;

import com.sloth.boot.example.observability.application.model.vo.LoadTestResultVO;
import com.sloth.boot.starter.threadpool.core.ThreadPools;
import com.sloth.boot.starter.threadpool.core.VisibleThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 压测演示业务命令。
 * <p>
 * 并发调用各演示端点，用于在可观测性面板上批量产生指标、链路与日志数据。
 * 使用预置的 {@link ThreadPools#HTTP_CLIENT} 线程池。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
public class LoadTestCommand {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final VisibleThreadPoolExecutor executor;

    public LoadTestCommand(RestTemplate restTemplate,
                           @Value("${server.port:8080}") int serverPort) {
        this.restTemplate = restTemplate;
        this.baseUrl = "http://localhost:" + serverPort;
        this.executor = ThreadPools.httpClient();
    }

    /**
     * 执行压测。
     *
     * @param count 请求数量
     * @return 压测结果
     */
    public LoadTestResultVO run(int count) {
        log.info("Starting load test with {} requests", count);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        long startTime = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(count);

        String[] endpoints = {
                baseUrl + "/api/demo/orders",
                baseUrl + "/api/demo/orders/%d",
                baseUrl + "/api/demo/users",
                baseUrl + "/api/demo/users/%d",
                baseUrl + "/api/demo/products",
                baseUrl + "/api/demo/products/%d",
                baseUrl + "/api/demo/metrics",
                baseUrl + "/api/demo/trace",
                baseUrl + "/api/demo/orders/place",
                baseUrl + "/api/demo/slow"
        };

        for (int i = 0; i < count; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    String endpoint = String.format(endpoints[index % endpoints.length], index % 20 + 1001);
                    if (endpoint.equals(baseUrl + "/api/demo/orders/place")) {
                        restTemplate.postForEntity(endpoint,
                                Map.of("userId", 1, "productId", 3, "quantity", 2), Map.class);
                    } else if (endpoint.equals(baseUrl + "/api/demo/orders")) {
                        restTemplate.getForEntity(endpoint, Map.class);
                    } else if (endpoint.contains("/orders/%")) {
                        restTemplate.postForEntity(endpoint, null, Map.class);
                    } else {
                        restTemplate.getForEntity(endpoint, Map.class);
                    }
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    log.debug("Request {} failed: {}", index, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("Load test completed: total={}, success={}, error={}, elapsed={}ms",
                count, successCount.get(), errorCount.get(), elapsed);

        return new LoadTestResultVO(count, successCount.get(), errorCount.get(),
                elapsed, count > 0 ? elapsed / count : 0);
    }
}

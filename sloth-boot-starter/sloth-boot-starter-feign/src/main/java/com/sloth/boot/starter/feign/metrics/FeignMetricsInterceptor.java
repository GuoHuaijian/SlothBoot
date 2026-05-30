package com.sloth.boot.starter.feign.metrics;

import feign.Client;
import feign.Request;
import feign.Response;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Feign 调用指标拦截器。
 * <p>
 * 包装 Feign Client，记录每次远程调用的次数和耗时。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class FeignMetricsInterceptor implements Client {

    private final Client delegate;
    private final MeterRegistry meterRegistry;

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        String serviceName = extractServiceName(request.url());
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Response response = delegate.execute(request, options);
            sample.stop(Timer.builder("feign.call.duration")
                .tag("service", serviceName)
                .tag("status", String.valueOf(response.status()))
                .register(meterRegistry));
            meterRegistry.counter("feign.call.total", "service", serviceName,
                "status", String.valueOf(response.status())).increment();
            return response;
        } catch (IOException e) {
            sample.stop(Timer.builder("feign.call.duration")
                .tag("service", serviceName)
                .tag("status", "error")
                .register(meterRegistry));
            meterRegistry.counter("feign.call.total", "service", serviceName,
                "status", "error").increment();
            throw e;
        }
    }

    private String extractServiceName(String url) {
        try {
            java.net.URI uri = java.net.URI.create(url);
            return uri.getHost() != null ? uri.getHost() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }
}

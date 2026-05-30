# Example Demo Restructuring Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restructure the example demo module from 8 overlapping pages to 6 capability-focused pages, fix configuration bugs, and enhance the monitor starter so all monitoring logic lives in the starter rather than the example.

**Architecture:** Phase 1 adds missing capabilities to `sloth-boot-starter-monitor` (JVM endpoint, metrics summary endpoint, system resource monitoring, slow operation event listener, thread pool health indicator). Phase 2 adds default logback config to `sloth-boot-common-log` and fixes configuration bugs. Phase 3 restructures the example backend (merge 8 controllers into 6, remove redundant logic, product/order use DB). Phase 4 restructures the frontend (6 demo pages, updated routing/sidebar).

**Tech Stack:** Spring Boot 3.x, Spring Actuator, Micrometer, MyBatis-Plus, H2, Vue 3 + TypeScript + Element Plus

---

## Phase 1: Monitor Starter Enhancement

**Goal:** Move all monitoring logic from the example module into `sloth-boot-starter-monitor`. After this phase, the starter provides: JVM info endpoint, metrics summary endpoint, system resource monitoring with threshold alarms, slow operation event consumption, and thread pool health indicator.

**Package base:** `com.sloth.boot.starter.monitor`

### Task 1.1: Create JvmInfo model and JvmInfoService

**Files:**
- Create: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/model/JvmInfo.java`
- Create: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/service/JvmInfoService.java`

**Purpose:** The example's `MonitorDemoService.getJvmInfo()` (37 lines of MXBean code) moves into the starter as a reusable service. Any project using the monitor starter can inject `JvmInfoService` to get structured JVM info.

**Step 1:** Create `JvmInfo.java` model

```java
package com.sloth.boot.starter.monitor.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class JvmInfo {
    private String heapUsed;
    private String heapMax;
    private String heapCommitted;
    private double heapUsagePercent;
    private String nonHeapUsed;
    private String nonHeapCommitted;
    private int threadCount;
    private int peakThreadCount;
    private int daemonThreadCount;
    private List<GcInfo> gcInfos;
    private int cpuProcessors;
    private double systemLoadAverage;

    @Data
    @Builder
    public static class GcInfo {
        private String name;
        private long collectionCount;
        private String collectionTime;
    }
}
```

**Step 2:** Create `JvmInfoService.java`

Move the logic from `MonitorDemoService.getJvmInfo()` (lines 74-110) and the `bytesToMB()` helper (lines 266-268) into this service. The service reads from `ManagementFactory.getMemoryMXBean()`, `getThreadMXBean()`, `getGarbageCollectorMXBeans()`, and `getOperatingSystemMXBean()`.

```java
package com.sloth.boot.starter.monitor.service;

import com.sloth.boot.starter.monitor.model.JvmInfo;
import org.springframework.stereotype.Service;
import java.lang.management.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JvmInfoService {

    public JvmInfo getJvmInfo() {
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        var threadMXBean = ManagementFactory.getThreadMXBean();

        List<JvmInfo.GcInfo> gcInfos = ManagementFactory.getGarbageCollectorMXBeans().stream()
                .map(gc -> JvmInfo.GcInfo.builder()
                        .name(gc.getName())
                        .collectionCount(gc.getCollectionCount())
                        .collectionTime(gc.getCollectionTime() + "ms")
                        .build())
                .collect(Collectors.toList());

        double heapPercent = heapUsage.getMax() > 0
                ? (double) heapUsage.getUsed() / heapUsage.getMax() * 100 : 0;

        return JvmInfo.builder()
                .heapUsed(bytesToMB(heapUsage.getUsed()))
                .heapMax(bytesToMB(heapUsage.getMax()))
                .heapCommitted(bytesToMB(heapUsage.getCommitted()))
                .heapUsagePercent(Math.round(heapPercent * 100.0) / 100.0)
                .nonHeapUsed(bytesToMB(nonHeapUsage.getUsed()))
                .nonHeapCommitted(bytesToMB(nonHeapUsage.getCommitted()))
                .threadCount(threadMXBean.getThreadCount())
                .peakThreadCount(threadMXBean.getPeakThreadCount())
                .daemonThreadCount(threadMXBean.getDaemonThreadCount())
                .gcInfos(gcInfos)
                .cpuProcessors(Runtime.getRuntime().availableProcessors())
                .systemLoadAverage(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage())
                .build();
    }

    static String bytesToMB(long bytes) {
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
```

**Step 3:** Register in `MonitorAutoConfiguration`

Add to `MonitorAutoConfiguration.java`:

```java
@Bean
@ConditionalOnMissingBean
public JvmInfoService jvmInfoService() {
    return new JvmInfoService();
}
```

**Step 4:** Verify compilation

```bash
cd sloth-boot-starter/sloth-boot-starter-monitor && mvn compile -q
```

- [ ] **Step 1:** Create `JvmInfo.java` model
- [ ] **Step 2:** Create `JvmInfoService.java` with JVM info gathering logic
- [ ] **Step 3:** Register `JvmInfoService` bean in `MonitorAutoConfiguration`
- [ ] **Step 4:** Verify `mvn compile` passes
- [ ] **Step 5:** Commit

```bash
git add sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/model/ sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/service/JvmInfoService.java sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/config/MonitorAutoConfiguration.java
git commit -m "feat(monitor): add JvmInfoService for structured JVM information"
```

---

### Task 1.2: Create MetricSummary model and MetricsSummaryService

**Files:**
- Create: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/model/MetricSummary.java`
- Create: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/service/MetricsSummaryService.java`

**Purpose:** The example's `MonitorDemoService.getMetricsSummary()` (37 lines of MeterRegistry iteration) moves into the starter. Any project can inject this to get a structured summary of all custom counters and timers.

**Step 1:** Create `MetricSummary.java` model

```java
package com.sloth.boot.starter.monitor.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MetricSummary {
    private List<CounterInfo> counters;
    private List<TimerInfo> timers;

    @Data @Builder
    public static class CounterInfo {
        private String name;
        private Map<String, String> tags;
        private double count;
    }

    @Data @Builder
    public static class TimerInfo {
        private String name;
        private Map<String, String> tags;
        private long count;
        private String totalTime;
        private String mean;
        private String max;
    }
}
```

**Step 2:** Create `MetricsSummaryService.java`

Move the logic from `MonitorDemoService.getMetricsSummary()` (lines 200-237). The service takes `MeterRegistry` and iterates meters to build structured `MetricSummary`.

```java
package com.sloth.boot.starter.monitor.service;

import com.sloth.boot.starter.monitor.model.MetricSummary;
import io.micrometer.core.instrument.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricsSummaryService {

    private final MeterRegistry meterRegistry;

    public MetricSummary getSummary() {
        List<MetricSummary.CounterInfo> counters = new ArrayList<>();
        List<MetricSummary.TimerInfo> timers = new ArrayList<>();

        meterRegistry.getMeters().forEach(meter -> {
            if (meter instanceof Counter counter) {
                counters.add(MetricSummary.CounterInfo.builder()
                        .name(counter.getId().getName())
                        .tags(extractTags(counter.getId()))
                        .count(counter.count())
                        .build());
            } else if (meter instanceof Timer timer) {
                timers.add(MetricSummary.TimerInfo.builder()
                        .name(timer.getId().getName())
                        .tags(extractTags(timer.getId()))
                        .count(timer.count())
                        .totalTime(String.format("%.2fms", timer.totalTime(TimeUnit.MILLISECONDS)))
                        .mean(String.format("%.2fms", timer.mean(TimeUnit.MILLISECONDS)))
                        .max(String.format("%.2fms", timer.max(TimeUnit.MILLISECONDS)))
                        .build());
            }
        });

        return MetricSummary.builder().counters(counters).timers(timers).build();
    }

    private Map<String, String> extractTags(Meter.Id id) {
        return id.getTags().stream()
                .collect(Collectors.toMap(Tag::getKey, Tag::getValue, (a, b) -> b));
    }
}
```

**Step 3:** Register in `MonitorAutoConfiguration` and verify compilation.

- [ ] **Step 1:** Create `MetricSummary.java` model
- [ ] **Step 2:** Create `MetricsSummaryService.java`
- [ ] **Step 3:** Register bean in `MonitorAutoConfiguration`
- [ ] **Step 4:** Verify `mvn compile` passes
- [ ] **Step 5:** Commit

---

### Task 1.3: Create Actuator Endpoints (JvmInfoEndpoint, MetricsSummaryEndpoint)

**Files:**
- Create: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/endpoint/JvmInfoEndpoint.java`
- Create: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/endpoint/MetricsSummaryEndpoint.java`

**Purpose:** Expose JVM info and metrics summary as Actuator endpoints (`/actuator/jvmInfo`, `/actuator/metricsSummary`), making them available via the standard Actuator exposure mechanism.

**Step 1:** Create `JvmInfoEndpoint.java`

```java
package com.sloth.boot.starter.monitor.endpoint;

import com.sloth.boot.starter.monitor.model.JvmInfo;
import com.sloth.boot.starter.monitor.service.JvmInfoService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id = "jvmInfo")
public class JvmInfoEndpoint {
    private final JvmInfoService jvmInfoService;

    public JvmInfoEndpoint(JvmInfoService jvmInfoService) {
        this.jvmInfoService = jvmInfoService;
    }

    @ReadOperation
    public JvmInfo jvmInfo() {
        return jvmInfoService.getJvmInfo();
    }
}
```

**Step 2:** Create `MetricsSummaryEndpoint.java`

```java
package com.sloth.boot.starter.monitor.endpoint;

import com.sloth.boot.starter.monitor.model.MetricSummary;
import com.sloth.boot.starter.monitor.service.MetricsSummaryService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id = "metricsSummary")
public class MetricsSummaryEndpoint {
    private final MetricsSummaryService metricsSummaryService;

    public MetricsSummaryEndpoint(MetricsSummaryService metricsSummaryService) {
        this.metricsSummaryService = metricsSummaryService;
    }

    @ReadOperation
    public MetricSummary summary() {
        return metricsSummaryService.getSummary();
    }
}
```

**Step 3:** Register both endpoints in `MonitorAutoConfiguration`

```java
@Bean
@ConditionalOnClass(Endpoint.class)
@ConditionalOnMissingBean
public JvmInfoEndpoint jvmInfoEndpoint(JvmInfoService jvmInfoService) {
    return new JvmInfoEndpoint(jvmInfoService);
}

@Bean
@ConditionalOnClass(Endpoint.class)
@ConditionalOnMissingBean
public MetricsSummaryEndpoint metricsSummaryEndpoint(MetricsSummaryService metricsSummaryService) {
    return new MetricsSummaryEndpoint(metricsSummaryService);
}
```

**Step 4:** Verify compilation.

- [ ] **Step 1:** Create `JvmInfoEndpoint.java`
- [ ] **Step 2:** Create `MetricsSummaryEndpoint.java`
- [ ] **Step 3:** Register beans in `MonitorAutoConfiguration`
- [ ] **Step 4:** Verify `mvn compile` passes
- [ ] **Step 5:** Commit

---

### Task 1.4: Create SlowOperationEventListener

**Files:**
- Create: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/event/SlowOperationEventListener.java`

**Purpose:** Consume `SlowOperationEvent` (published by mybatis/redis starters when slow operations are detected), record Micrometer metrics, trigger alarms, and log details. Currently this event is published but nobody listens.

**Step 1:** Create `SlowOperationEventListener.java`

```java
package com.sloth.boot.starter.monitor.event;

import com.sloth.boot.common.log.event.SlowOperationEvent;
import com.sloth.boot.starter.monitor.alarm.AlarmMessage;
import com.sloth.boot.starter.monitor.alarm.AlarmService;
import com.sloth.boot.starter.monitor.metrics.BusinessMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.event.EventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ConditionalOnClass(name = "com.sloth.boot.common.log.event.SlowOperationEvent")
public class SlowOperationEventListener {

    private final MeterRegistry meterRegistry;
    private final ObjectProvider<AlarmService> alarmService;

    public SlowOperationEventListener(MeterRegistry meterRegistry,
                                      ObjectProvider<AlarmService> alarmService) {
        this.meterRegistry = meterRegistry;
        this.alarmService = alarmService;
    }

    @EventListener
    public void onSlowOperation(SlowOperationEvent event) {
        // Record as Micrometer timer metric
        Timer.builder("sloth.slow.operation")
                .tag("type", event.getOperationType())
                .register(meterRegistry)
                .record(event.getCostTimeMs(), TimeUnit.MILLISECONDS);

        // Trigger alarm
        alarmService.ifPresent(alarm -> {
            AlarmMessage message = new AlarmMessage();
            message.setTitle("慢操作告警");
            message.setContent("类型: " + event.getOperationType()
                    + "\n详情: " + truncate(event.getDetail(), 500)
                    + "\n耗时: " + event.getCostTimeMs() + "ms"
                    + "\n阈值: " + event.getThresholdMs() + "ms"
                    + (event.getContext() != null ? "\n上下文: " + event.getContext() : ""));
            alarm.send(message);
        });
    }

    private String truncate(String s, int maxLen) {
        return (s != null && s.length() > maxLen) ? s.substring(0, maxLen) + "..." : s;
    }
}
```

**Step 2:** Verify compilation. Note: the `sloth-boot-common-log` module is not a dependency of the monitor starter currently. You need to add it as an **optional** dependency in the monitor starter's `pom.xml`:

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-common-log</artifactId>
    <version>${revision}</version>
    <optional>true</optional>
</dependency>
```

**Step 3:** Verify compilation.

- [ ] **Step 1:** Add `sloth-boot-common-log` optional dependency to monitor starter `pom.xml`
- [ ] **Step 2:** Create `SlowOperationEventListener.java`
- [ ] **Step 3:** Verify `mvn compile` passes
- [ ] **Step 4:** Commit

---

### Task 1.5: Create SystemResourceCollector (scheduled CPU/Memory/Disk monitoring)

**Files:**
- Create: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/collector/SystemResourceCollector.java`

**Purpose:** Periodically collect CPU usage, JVM memory usage, and disk usage. When thresholds from `MonitorProperties.Alarm` are exceeded, trigger alarm via `AlarmService`. This fills the gap where `cpuThreshold`, `memoryThreshold`, `diskThreshold` config properties exist but nothing reads them.

**Step 1:** Create `SystemResourceCollector.java`

The collector runs on a fixed schedule (default 60s). It reads:
- CPU: `OperatingSystemMXBean.getSystemCpuLoad()` (returns 0.0-1.0, multiply by 100 for %)
- Memory: `MemoryMXBean.getHeapMemoryUsage()` for JVM heap %, `OperatingSystemMXBean` for system memory
- Disk: `File.listRoots()` to check total/usable space

When any metric exceeds its threshold, it sends an `AlarmMessage` via `AlarmService`.

```java
package com.sloth.boot.starter.monitor.collector;

import com.sloth.boot.starter.monitor.alarm.AlarmMessage;
import com.sloth.boot.starter.monitor.alarm.AlarmService;
import com.sloth.boot.starter.monitor.config.MonitorProperties;
import com.sloth.boot.starter.monitor.metrics.BusinessMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.MemoryUsage;

@Slf4j
public class SystemResourceCollector {

    private final MonitorProperties monitorProperties;
    private final ObjectProvider<AlarmService> alarmService;
    private final MeterRegistry meterRegistry;

    public SystemResourceCollector(MonitorProperties monitorProperties,
                                   ObjectProvider<AlarmService> alarmService,
                                   MeterRegistry meterRegistry) {
        this.monitorProperties = monitorProperties;
        this.alarmService = alarmService;
        this.meterRegistry = meterRegistry;
        registerGauges();
    }

    private void registerGauges() {
        Gauge.builder("system.cpu.usage", this, c -> getCpuUsage())
                .description("System CPU usage percentage").register(meterRegistry);
        Gauge.builder("system.memory.usage", this, c -> getMemoryUsage())
                .description("JVM heap memory usage percentage").register(meterRegistry);
        Gauge.builder("system.disk.usage", this, c -> getDiskUsage())
                .description("Disk usage percentage").register(meterRegistry);
    }

    @Scheduled(fixedDelayString = "${sloth.monitor.collect-interval:60000}")
    public void collect() {
        MonitorProperties.Alarm alarmConfig = monitorProperties.getAlarm();
        if (!alarmConfig.isEnabled()) {
            return;
        }

        double cpu = getCpuUsage();
        double memory = getMemoryUsage();
        double disk = getDiskUsage();

        // Record as Micrometer gauges (already done via registerGauges)

        // Check thresholds and alarm
        StringBuilder alarmContent = new StringBuilder();
        if (cpu > alarmConfig.getCpuThreshold()) {
            alarmContent.append(String.format("- CPU 使用率: %.1f%% (阈值: %.1f%%)\n", cpu, alarmConfig.getCpuThreshold()));
        }
        if (memory > alarmConfig.getMemoryThreshold()) {
            alarmContent.append(String.format("- 内存使用率: %.1f%% (阈值: %.1f%%)\n", memory, alarmConfig.getMemoryThreshold()));
        }
        if (disk > alarmConfig.getDiskThreshold()) {
            alarmContent.append(String.format("- 磁盘使用率: %.1f%% (阈值: %.1f%%)\n", disk, alarmConfig.getDiskThreshold()));
        }

        if (!alarmContent.isEmpty()) {
            alarmService.ifPresent(alarm -> {
                AlarmMessage message = new AlarmMessage();
                message.setTitle("系统资源告警");
                message.setContent(alarmContent.toString());
                message.setLevel("WARN");
                alarm.send(message);
            });
        }
    }

    public double getCpuUsage() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        double load = os.getSystemLoadAverage();
        int processors = os.getAvailableProcessors();
        if (load < 0 || processors == 0) return 0;
        return Math.min(100.0, load / processors * 100.0);
    }

    public double getMemoryUsage() {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return heap.getMax() > 0 ? (double) heap.getUsed() / heap.getMax() * 100.0 : 0;
    }

    public double getDiskUsage() {
        File[] roots = File.listRoots();
        if (roots == null || roots.length == 0) return 0;
        long total = 0, usable = 0;
        for (File root : roots) {
            total += root.getTotalSpace();
            usable += root.getUsableSpace();
        }
        return total > 0 ? (1.0 - (double) usable / total) * 100.0 : 0;
    }
}
```

**Step 2:** Add `@EnableScheduling` to `MonitorAutoConfiguration` (or create a separate scheduling config).

**Step 3:** Register bean in `MonitorAutoConfiguration`.

**Step 4:** Verify compilation.

- [ ] **Step 1:** Create `SystemResourceCollector.java`
- [ ] **Step 2:** Enable scheduling support in auto-configuration
- [ ] **Step 3:** Register bean and verify compilation
- [ ] **Step 4:** Commit

---

### Task 1.6: Enrich InfoEndpoint and add utility class

**Files:**
- Modify: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/endpoint/InfoEndpoint.java`
- Create: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/util/MonitorUtil.java`

**Purpose:** Add `availableProcessors` to `InfoEndpoint.info()` (currently missing, example had to re-implement). Create `MonitorUtil` with `bytesToMB()`, `formatTimestamp()`, `formatDuration()` helpers that the example currently has as private methods.

**Step 1:** Add `info.put("availableProcessors", Runtime.getRuntime().availableProcessors())` to `InfoEndpoint.info()`.

**Step 2:** Create `MonitorUtil.java` with the three formatting methods from `MonitorDemoService` (lines 266-291).

**Step 3:** Verify compilation.

- [ ] **Step 1:** Enrich `InfoEndpoint` with `availableProcessors`
- [ ] **Step 2:** Create `MonitorUtil.java`
- [ ] **Step 3:** Verify compilation
- [ ] **Step 4:** Commit

---

### Task 1.7: Update spring-configuration-metadata

**Files:**
- Modify: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/resources/META-INF/spring-configuration-metadata-additional.json`

**Purpose:** Add metadata entries for the new `sloth.monitor.collect-interval` property (default 60000ms) and document the new endpoints in the metadata.

- [ ] **Step 1:** Add `collect-interval` property metadata
- [ ] **Step 2:** Verify JSON is valid
- [ ] **Step 3:** Commit

---

## Phase 2: Common-Log Enhancement + Configuration Bug Fixes

**Goal:** Fix 3 configuration bugs, add default logback-spring.xml to common-log, clean up application.yml.

### Task 2.1: Add default logback-spring.xml to sloth-boot-common-log

**Files:**
- Create: `sloth-boot-common/sloth-boot-common-log/src/main/resources/sloth-logback-spring.xml`

**Purpose:** Provide a ready-to-use logback config with traceId in pattern, colored console output for dev, JSON output for prod. Users include it via `<include resource="sloth-logback-spring.xml"/>` in their own logback-spring.xml, or override it.

**Step 1:** Create `sloth-logback-spring.xml` with:
- Console appender with color + traceId (dev)
- RollingFile appender (prod)
- JSON file appender (prod)
- Pattern: `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{traceId}] %-5level %logger{36} - %msg%n`

**Step 2:** Verify the resource is included in the JAR (`mvn package` and check).

- [ ] **Step 1:** Create `sloth-logback-spring.xml`
- [ ] **Step 2:** Verify resource packaging
- [ ] **Step 3:** Commit

---

### Task 2.2: Fix configuration bugs in application.yml

**Files:**
- Modify: `sloth-boot-example/sloth-boot-example-service/src/main/resources/application.yml`

**Purpose:** Fix 3 bugs + remove redundant defaults + restructure for clarity.

**Step 1:** Fix Bug 1 - `print-request-log` -> `print-access-log`

```yaml
# Before (line 180):
print-request-log: true
# After:
print-access-log: true
```

**Step 2:** Fix Bug 2 - Remove dead `sloth.web.enabled` (line 82)

```yaml
# Before:
sloth:
  web:
    enabled: true       # DELETE THIS LINE
    response-wrapper: true
# After:
sloth:
  web:
    response-wrapper: true
```

**Step 3:** Fix Bug 3 - Remove dead `sloth.mybatis.encrypt-key` (line 134)

```yaml
# DELETE THIS LINE:
encrypt-key: sloth-boot-example-key-2024
```

**Step 4:** Remove all redundant default-value lines:
- All `enabled: true` lines (web, redis, mybatis, thread-pool, monitor, doc, log)
- `sloth.redis.lock-wait-time: 3`
- `sloth.redis.lock-lease-time: 30`
- `sloth.redis.bloom.false-positive-probability: 0.01`
- `sloth.monitor.slow-api-enabled: true`
- `sloth.monitor.slow-api-threshold: 1000` (keep this one - it's 1000, different from default 3000)

**Step 5:** Restructure with clear section headers and comments explaining WHY non-default values are set.

**Step 6:** Fix auth white-list to be realistic (keep only actuator/doc paths, remove business API paths so auth actually works):

```yaml
sloth:
  auth:
    token-name: Authorization
    token-timeout: 7200
    white-list:
      - /health
      - /api/system/login
      - /doc.html
      - /swagger-ui/**
      - /v3/api-docs/**
      - /actuator/**
```

- [ ] **Step 1:** Fix `print-request-log` -> `print-access-log`
- [ ] **Step 2:** Remove dead `sloth.web.enabled`
- [ ] **Step 3:** Remove dead `sloth.mybatis.encrypt-key`
- [ ] **Step 4:** Remove redundant default values
- [ ] **Step 5:** Restructure with section headers
- [ ] **Step 6:** Fix auth white-list
- [ ] **Step 7:** Commit

---

### Task 2.3: Simplify example logback-spring.xml

**Files:**
- Modify: `sloth-boot-example/sloth-boot-example-service/src/main/resources/logback-spring.xml`

**Purpose:** Replace the hand-written logback config with an include of the starter-provided default, adding only example-specific customizations (APP_NAME).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="sloth-logback-spring.xml"/>
    <property name="APP_NAME" value="sloth-example"/>
</configuration>
```

- [ ] **Step 1:** Replace logback-spring.xml with include-based version
- [ ] **Step 2:** Commit

---

## Phase 3: Example Backend Restructuring

**Goal:** Merge 8 controllers into 6 capability-focused controllers. Product/Order use H2 tables instead of ConcurrentHashMap. MonitorDemoService becomes a thin proxy to starter services.

### Task 3.1: Create product and order database tables

**Files:**
- Modify: `sloth-boot-example/sloth-boot-example-service/src/main/resources/schema.sql`
- Modify: `sloth-boot-example/sloth-boot-example-service/src/main/resources/data.sql`
- Create: `sloth-boot-example/.../domain/entity/Product.java`
- Create: `sloth-boot-example/.../domain/entity/Order.java` (or `DemoOrder.java` to avoid SQL keyword)
- Create: `sloth-boot-example/.../domain/mapper/ProductMapper.java`
- Create: `sloth-boot-example/.../domain/mapper/OrderMapper.java`

**Purpose:** Product and Order currently use `ConcurrentHashMap` in-memory storage. This means no ORM features are demonstrated (no @Version, @TableLogic, auto-fill) and data is lost on restart. Add H2 tables to match the framework's enterprise positioning.

**Step 1:** Add `product` table to `schema.sql`:

```sql
CREATE TABLE IF NOT EXISTS product (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    stock       INT NOT NULL DEFAULT 0,
    description TEXT,
    status      TINYINT DEFAULT 0,
    create_by   VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT DEFAULT 0,
    version     INT DEFAULT 0
);
```

**Step 2:** Add `demo_order` table to `schema.sql`:

```sql
CREATE TABLE IF NOT EXISTS demo_order (
    id          BIGINT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    product_name VARCHAR(128),
    quantity    INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status      VARCHAR(20) DEFAULT 'PENDING',
    create_by   VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT DEFAULT 0,
    version     INT DEFAULT 0
);
```

**Step 3:** Add seed data to `data.sql` for 10 products.

**Step 4:** Create entity classes extending `BaseEntityX` (with @Version, @TableLogic, auto-fill annotations).

**Step 5:** Create mapper interfaces extending `BaseMapperX`.

- [ ] **Step 1:** Add product table DDL
- [ ] **Step 2:** Add demo_order table DDL
- [ ] **Step 3:** Add seed data
- [ ] **Step 4:** Create entity classes
- [ ] **Step 5:** Create mapper interfaces
- [ ] **Step 6:** Commit

---

### Task 3.2: Restructure backend - merge System+Dept+User -> AuthDemo + EntityDemo

**Files:**
- Modify: `sloth-boot-example/.../controller/system/SystemController.java` -> Rename to `AuthController.java`
- Modify: `sloth-boot-example/.../controller/user/UserController.java` -> Rename to `EntityController.java`
- Modify: `sloth-boot-example/.../service/system/SystemDemoService.java` -> Merge into `AuthDemoService.java`
- Modify: `sloth-boot-example/.../service/user/UserService.java` -> Merge into `EntityDemoService.java`
- Modify: `sloth-boot-example/.../service/dept/DeptService.java` -> Merge into `EntityDemoService.java`
- Delete: `sloth-boot-example/.../controller/dept/DeptController.java`
- Delete: `sloth-boot-example/.../service/dept/DeptService.java`

**Mapping of endpoints:**

| Old Endpoint | New Home | Reason |
|-------------|----------|--------|
| `POST /api/system/login` | `POST /api/auth/login` | AuthDemo |
| `POST /api/system/logout` | `POST /api/auth/logout` | AuthDemo |
| `GET /api/system/current-user` | `GET /api/auth/current-user` | AuthDemo |
| `GET /api/system/permissions` | `GET /api/auth/permissions` | AuthDemo |
| `GET /api/system/data-scope` | `GET /api/auth/data-scope` | AuthDemo |
| `GET /api/system/users` | DELETE (redundant with /api/entity/user/page) | - |
| `POST /api/user` | `POST /api/entity/user` | EntityDemo |
| `GET /api/user/page` | `GET /api/entity/user/page` | EntityDemo |
| `GET /api/user/{id}` | `GET /api/entity/user/{id}` | EntityDemo |
| `GET /api/user/{id}/desensitize` | `GET /api/entity/user/{id}/desensitize` | EntityDemo |
| `PUT /api/user` | `PUT /api/entity/user` | EntityDemo |
| `DELETE /api/user/{id}` | `DELETE /api/entity/user/{id}` | EntityDemo |
| `POST /api/user/import` | `POST /api/entity/user/import` | EntityDemo |
| `GET /api/user/scope` | `GET /api/entity/user/scope` | EntityDemo |
| `POST /api/dept` | `POST /api/entity/dept` | EntityDemo |
| `GET /api/dept/tree` | `GET /api/entity/dept/tree` | EntityDemo |
| `GET /api/dept/{id}` | `GET /api/entity/dept/{id}` | EntityDemo |
| `PUT /api/dept` | `PUT /api/entity/dept` | EntityDemo |
| `DELETE /api/dept/{id}` | `DELETE /api/entity/dept/{id}` | EntityDemo |
| `POST /api/dept/import` | `POST /api/entity/dept/import` | EntityDemo |
| `GET /api/dept/scope` | DELETE (merged into entity/user/scope) | - |

**Step 1:** Create `AuthController.java` with login/logout/current-user/permissions/data-scope endpoints.

**Step 2:** Create `EntityController.java` combining user CRUD + dept CRUD + desensitization + data permission + batch import + Excel import/export.

**Step 3:** Create `AuthDemoService.java` and `EntityDemoService.java`.

**Step 4:** Delete old controllers and services.

**Step 5:** Verify compilation.

- [ ] **Step 1:** Create AuthController + AuthDemoService
- [ ] **Step 2:** Create EntityController + EntityDemoService
- [ ] **Step 3:** Delete old controllers/dept, controllers/system, controllers/user, services/dept, services/user
- [ ] **Step 4:** Verify compilation
- [ ] **Step 5:** Commit

---

### Task 3.3: Restructure backend - merge Product+Order -> RedisDemo

**Files:**
- Modify: `sloth-boot-example/.../controller/product/ProductController.java` -> Rename to `RedisDemoController.java`
- Modify: `sloth-boot-example/.../controller/order/OrderController.java` -> Merge into `RedisDemoController.java`
- Modify: `sloth-boot-example/.../service/product/ProductDemoService.java` -> Rename to `RedisDemoService.java`
- Modify: `sloth-boot-example/.../service/order/OrderDemoService.java` -> Merge into `RedisDemoService.java`

**Mapping of endpoints:**

| Old Endpoint | New Home | Feature |
|-------------|----------|---------|
| `GET /api/product/{id}` | `GET /api/redis/product/{id}` | Bloom filter + logical expire cache |
| `GET /api/product/list` | `GET /api/redis/product/list` | Product listing |
| `POST /api/product` | `POST /api/redis/product` | XSS clean + bloom filter |
| `DELETE /api/product/{id}` | `DELETE /api/redis/product/{id}` | Delete |
| `GET /api/product/rank` | `GET /api/redis/rank` | ZSet ranking |
| `POST /api/product/rank/vote` | `POST /api/redis/rank/vote` | ZSet vote |
| `GET /api/product/cache/demo` | `GET /api/redis/cache/demo` | Cache strategy comparison |
| `GET /api/product/bloom/stats` | `GET /api/redis/bloom/stats` | Bloom filter stats |
| `POST /api/product/bloom/reset` | `POST /api/redis/bloom/reset` | Bloom reset |
| `POST /api/order/create` | `POST /api/redis/order/create` | Distributed lock + idempotent |
| `GET /api/order/list` | `GET /api/redis/order/list` | Order listing |
| `PUT /api/order/{id}/pay` | `PUT /api/redis/order/{id}/pay` | Distributed lock |
| `GET /api/order/rate-limit-test` | `GET /api/redis/rate-limit-test` | Rate limit demo |
| `GET /api/order/events` | `GET /api/redis/pubsub/events` | Pub/Sub events |

**Step 1:** Create `RedisDemoController.java` combining all product + order + redis-specific endpoints.

**Step 2:** Create `RedisDemoService.java` combining `ProductDemoService` + `OrderDemoService`, using the new DB-backed entities instead of ConcurrentHashMap.

**Step 3:** Delete old controllers and services.

**Step 4:** Verify compilation.

- [ ] **Step 1:** Create RedisDemoController + RedisDemoService
- [ ] **Step 2:** Delete old product/order controllers and services
- [ ] **Step 3:** Verify compilation
- [ ] **Step 4:** Commit

---

### Task 3.4: Restructure MonitorDemoService as thin proxy

**Files:**
- Modify: `sloth-boot-example/.../controller/monitor/MonitorController.java`
- Modify: `sloth-boot-example/.../service/monitor/MonitorDemoService.java`
- Delete: `sloth-boot-example/.../model/monitor/vo/JvmInfo.java`
- Delete: `sloth-boot-example/.../model/monitor/vo/MetricSummary.java`

**Purpose:** Now that the monitor starter provides `JvmInfoService`, `MetricsSummaryService`, `JvmInfoEndpoint`, `MetricsSummaryEndpoint`, and `SystemResourceCollector`, the example should only inject these services and pass through.

**Step 1:** Rewrite `MonitorDemoService.java` to ~30 lines:

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorDemoService {

    private final ThreadPoolManager threadPoolManager;
    private final ThreadPoolRegistry threadPoolRegistry;
    private final BusinessMetrics businessMetrics;
    private final JvmInfoService jvmInfoService;
    private final MetricsSummaryService metricsSummaryService;

    // Thin proxies - just delegate to starter services
    public JvmInfo getJvmInfo() { return jvmInfoService.getJvmInfo(); }
    public MetricSummary getMetricsSummary() { return metricsSummaryService.getSummary(); }
    public Map<String, ThreadPoolSnapshot> getThreadPoolSnapshots() { return threadPoolRegistry.getAllSnapshots(); }
    public ThreadPoolSnapshot getThreadPoolSnapshot(String name) { return threadPoolManager.getSnapshot(name); }
    public void resizeThreadPool(String name, int coreSize, int maxSize) { threadPoolManager.updatePoolSize(name, coreSize, maxSize); }

    // Demo-specific: submit tasks to show thread pool behavior
    public Map<String, Object> submitTasks(String poolName, int count, long sleepMs) { /* same as current */ }

    // Demo-specific: trigger counter/timer for demonstration
    public void incrementCounter(String name) { businessMetrics.increment(name); }
    public void recordTimer(String name, long durationMs) { /* same as current */ }

    // Demo-specific: simulate slow API
    public String slowApi() { Thread.sleep(5000); return "done"; }
}
```

**Step 2:** Delete `JvmInfo.java` and `MetricSummary.java` from example's model package (now in starter).

**Step 3:** Update `MonitorController.java` imports to use starter's model classes.

**Step 4:** Verify compilation.

- [ ] **Step 1:** Rewrite MonitorDemoService as thin proxy
- [ ] **Step 2:** Delete example model classes
- [ ] **Step 3:** Update imports
- [ ] **Step 4:** Verify compilation
- [ ] **Step 5:** Commit

---

### Task 3.5: Keep Security and AI controllers as-is

**Files:** No changes needed.

**Purpose:** `SecurityController` and `AiController` are already clean, self-contained demos with no overlap. They stay as `/api/security/*` and `/api/ai/*`.

- [ ] No action needed - confirmed clean.

---

## Phase 4: Frontend Restructuring

**Goal:** Update from 8 demo pages to 6: AuthDemo, EntityDemo, RedisDemo, SecurityDemo, AiDemo, MonitorDemo.

### Task 4.1: Update routing and sidebar

**Files:**
- Modify: `sloth-boot-ui/src/router/index.ts`
- Modify: `sloth-boot-ui/src/layouts/DemoLayout.vue`

**Step 1:** Update `router/index.ts` - replace 8 demo routes with 6:

```typescript
children: [
  { path: 'auth', name: 'DemoAuth', component: () => import('@/views/demo/AuthDemo.vue'), meta: { title: '认证授权' } },
  { path: 'entity', name: 'DemoEntity', component: () => import('@/views/demo/EntityDemo.vue'), meta: { title: '数据实体' } },
  { path: 'redis', name: 'DemoRedis', component: () => import('@/views/demo/RedisDemo.vue'), meta: { title: 'Redis 能力' } },
  { path: 'ai', name: 'DemoAi', component: () => import('@/views/demo/AiDemo.vue'), meta: { title: 'AI 助手' } },
  { path: 'security', name: 'DemoSecurity', component: () => import('@/views/demo/SecurityDemo.vue'), meta: { title: '安全工具' } },
  { path: 'monitor', name: 'DemoMonitor', component: () => import('@/views/demo/MonitorDemo.vue'), meta: { title: '系统监控' } },
]
```

**Step 2:** Update `DemoLayout.vue` sidebar menuItems:

```typescript
const menuItems = [
  { index: '/demo/auth', icon: Setting, label: '认证授权' },
  { index: '/demo/entity', icon: UserFilled, label: '数据实体' },
  { index: '/demo/redis', icon: Goods, label: 'Redis 能力' },
  { index: '/demo/ai', icon: ChatDotRound, label: 'AI 助手' },
  { index: '/demo/security', icon: Lock, label: '安全工具' },
  { index: '/demo/monitor', icon: Monitor, label: '系统监控' },
]
```

- [ ] **Step 1:** Update router routes
- [ ] **Step 2:** Update sidebar menuItems
- [ ] **Step 3:** Commit

---

### Task 4.2: Create AuthDemo.vue (merge SystemDemo + auth parts of DeptDemo)

**Files:**
- Create: `sloth-boot-ui/src/views/demo/AuthDemo.vue`
- Create: `sloth-boot-ui/src/api/auth.ts` (rename from system.ts)
- Delete: `sloth-boot-ui/src/views/demo/SystemDemo.vue`
- Delete: `sloth-boot-ui/src/views/demo/DeptDemo.vue`

**Purpose:** Single page for authentication and authorization. Sections:
1. Login/Logout panel (with token display)
2. Current user info (with @Desensitize demo)
3. Permission check panel
4. Data scope demo (merged from DeptDemo's data scope section)

**Step 1:** Create `auth.ts` API client pointing to `/api/auth/*`.

**Step 2:** Create `AuthDemo.vue` with the 4 sections above, combining the best parts of `SystemDemo.vue` and the data scope section from `DeptDemo.vue`.

**Step 3:** Delete old `SystemDemo.vue` and `DeptDemo.vue`.

- [ ] **Step 1:** Create auth.ts API client
- [ ] **Step 2:** Create AuthDemo.vue
- [ ] **Step 3:** Delete old files
- [ ] **Step 4:** Commit

---

### Task 4.3: Create EntityDemo.vue (merge UserDemo + tree/batch parts of DeptDemo)

**Files:**
- Create: `sloth-boot-ui/src/views/demo/EntityDemo.vue`
- Create: `sloth-boot-ui/src/api/entity.ts` (consolidate from user.ts + dept.ts)
- Delete: `sloth-boot-ui/src/views/demo/UserDemo.vue`
- Delete: `sloth-boot-ui/src/api/user.ts`
- Delete: `sloth-boot-ui/src/api/dept.ts`

**Purpose:** Single page for all data entity operations. Tabs:
1. "User Management" - CRUD, pagination, search, desensitization, field encryption
2. "Department Management" - tree structure, CRUD, batch import
3. "Excel Import/Export" - demonstrate EasyExcel integration (currently missing)
4. "Operation Log" - show recent @OperateLog entries (currently missing)

**Step 1:** Create `entity.ts` API client with all user + dept endpoints under `/api/entity/*`.

**Step 2:** Create `EntityDemo.vue` with 4 tabs.

**Step 3:** Delete old UserDemo.vue, user.ts, dept.ts.

- [ ] **Step 1:** Create entity.ts API client
- [ ] **Step 2:** Create EntityDemo.vue with 4 tabs
- [ ] **Step 3:** Delete old files
- [ ] **Step 4:** Commit

---

### Task 4.4: Create RedisDemo.vue (merge ProductDemo + OrderDemo)

**Files:**
- Create: `sloth-boot-ui/src/views/demo/RedisDemo.vue`
- Create: `sloth-boot-ui/src/api/redis.ts` (consolidate from product.ts + order.ts)
- Delete: `sloth-boot-ui/src/views/demo/ProductDemo.vue`
- Delete: `sloth-boot-ui/src/views/demo/OrderDemo.vue`
- Delete: `sloth-boot-ui/src/api/product.ts`
- Delete: `sloth-boot-ui/src/api/order.ts`

**Purpose:** Single page for all Redis capabilities. Tabs:
1. "Cache Strategies" - cache strategy comparison, bloom filter demo
2. "Distributed Lock & Idempotent" - order creation with distributed lock
3. "Rate Limit" - rate limit test with real-time stats
4. "Pub/Sub Events" - Redis Pub/Sub event stream
5. "ZSet Ranking" - product ranking/vote

**Step 1:** Create `redis.ts` API client with endpoints under `/api/redis/*`.

**Step 2:** Create `RedisDemo.vue` with 5 tabs, combining the best visual elements from ProductDemo and OrderDemo.

**Step 3:** Delete old files.

- [ ] **Step 1:** Create redis.ts API client
- [ ] **Step 2:** Create RedisDemo.vue
- [ ] **Step 3:** Delete old files
- [ ] **Step 4:** Commit

---

### Task 4.5: Update MonitorDemo.vue to use new Actuator endpoints

**Files:**
- Modify: `sloth-boot-ui/src/views/demo/MonitorDemo.vue`
- Modify: `sloth-boot-ui/src/api/monitor.ts`

**Purpose:** Update the monitoring demo to:
1. Use starter's `/actuator/jvmInfo` and `/actuator/metricsSummary` endpoints
2. Add system resource monitoring section (CPU/Memory/Disk gauges)
3. Remove the `slowApi` wait-for-5-seconds UX anti-pattern
4. Show the thread pool health indicator integration

**Step 1:** Update `monitor.ts` to call Actuator endpoints for JVM info and metrics summary.

**Step 2:** Update MonitorDemo.vue:
- Add "System Resources" tab showing CPU/Memory/Disk gauges with threshold lines
- Update "Overview" tab to use new endpoint data structure
- Replace slow-api sleep demo with a quick-trigger version or remove it
- Show thread pool health status integrated with `/actuator/health`

- [ ] **Step 1:** Update monitor.ts API client
- [ ] **Step 2:** Update MonitorDemo.vue
- [ ] **Step 3:** Commit

---

### Task 4.6: Update modules.ts metadata and LandingPage

**Files:**
- Modify: `sloth-boot-ui/src/data/modules.ts`

**Purpose:** Update the 24 module definitions' `demoRoute` properties to point to the new 6 demo routes instead of the old 8.

- [ ] **Step 1:** Update demoRoute mappings
- [ ] **Step 2:** Commit

---

## Phase 5: Verification

### Task 5.1: Verify backend compiles and starts

```bash
cd sloth-boot-example/sloth-boot-example-service
mvn clean compile -q
mvn spring-boot:run
```

Expected: Application starts on port 8080, H2 tables created, all 6 controller groups respond.

### Task 5.2: Verify frontend builds

```bash
cd sloth-boot-ui
npm install
npm run build
```

Expected: Build succeeds, no TypeScript errors, 6 demo routes defined.

### Task 5.3: Verify Actuator endpoints

After starting the app:
```bash
curl http://localhost:8080/actuator/jvmInfo | jq
curl http://localhost:8080/actuator/metricsSummary | jq
curl http://localhost:8080/actuator/appInfo | jq
curl http://localhost:8080/actuator/health | jq
```

Expected: All return valid JSON with structured monitoring data.

### Task 5.4: Verify configuration fixes

```bash
# Verify print-access-log works (should see [Access] logs in console)
curl http://localhost:8080/api/auth/login -X POST -H 'Content-Type: application/json' -d '{"userId":1,"username":"admin"}'

# Verify auth is enforced (should return 401 without token)
curl http://localhost:8080/api/entity/user/page

# Verify auth works with token
curl http://localhost:8080/api/entity/user/page -H 'Authorization: <token>'
```

- [ ] **Step 1:** Backend compiles and starts
- [ ] **Step 2:** Frontend builds
- [ ] **Step 3:** Actuator endpoints respond
- [ ] **Step 4:** Configuration fixes verified
- [ ] **Step 5:** Final commit

---

## Summary: File Change Inventory

### New Files (monitor starter - 9 files)
| File | Purpose |
|------|---------|
| `.../monitor/model/JvmInfo.java` | JVM info data model |
| `.../monitor/model/MetricSummary.java` | Metrics summary data model |
| `.../monitor/service/JvmInfoService.java` | JVM info gathering service |
| `.../monitor/service/MetricsSummaryService.java` | Metrics summary service |
| `.../monitor/endpoint/JvmInfoEndpoint.java` | Actuator JVM info endpoint |
| `.../monitor/endpoint/MetricsSummaryEndpoint.java` | Actuator metrics summary endpoint |
| `.../monitor/event/SlowOperationEventListener.java` | Slow operation event consumer |
| `.../monitor/collector/SystemResourceCollector.java` | CPU/Memory/Disk monitoring |
| `.../monitor/util/MonitorUtil.java` | Formatting utilities |

### New Files (common-log - 1 file)
| File | Purpose |
|------|---------|
| `.../common-log/src/main/resources/sloth-logback-spring.xml` | Default logback config |

### New Files (example backend - ~8 files)
| File | Purpose |
|------|---------|
| `.../example/controller/auth/AuthController.java` | Auth demo endpoints |
| `.../example/controller/entity/EntityController.java` | Entity CRUD endpoints |
| `.../example/controller/redis/RedisDemoController.java` | Redis capability endpoints |
| `.../example/service/auth/AuthDemoService.java` | Auth demo logic |
| `.../example/service/entity/EntityDemoService.java` | Entity demo logic |
| `.../example/service/redis/RedisDemoService.java` | Redis demo logic |
| `.../example/domain/entity/Product.java` | Product entity |
| `.../example/domain/entity/DemoOrder.java` | Order entity |
| `.../example/domain/mapper/ProductMapper.java` | Product mapper |
| `.../example/domain/mapper/OrderMapper.java` | Order mapper |

### New Files (frontend - 6 files)
| File | Purpose |
|------|---------|
| `sloth-boot-ui/src/views/demo/AuthDemo.vue` | Auth demo page |
| `sloth-boot-ui/src/views/demo/EntityDemo.vue` | Entity demo page |
| `sloth-boot-ui/src/views/demo/RedisDemo.vue` | Redis demo page |
| `sloth-boot-ui/src/api/auth.ts` | Auth API client |
| `sloth-boot-ui/src/api/entity.ts` | Entity API client |
| `sloth-boot-ui/src/api/redis.ts` | Redis API client |

### Modified Files (~8 files)
| File | Change |
|------|--------|
| `MonitorAutoConfiguration.java` | Register 6 new beans |
| `InfoEndpoint.java` | Add availableProcessors |
| `monitor-starter pom.xml` | Add common-log optional dep |
| `spring-configuration-metadata-additional.json` | Add new property metadata |
| `application.yml` | Fix 3 bugs, remove redundancy |
| `logback-spring.xml` | Simplify to include |
| `schema.sql` / `data.sql` | Add product/order tables |
| `router/index.ts` / `DemoLayout.vue` | 6 routes instead of 8 |

### Deleted Files (~14 files)
| File | Reason |
|------|--------|
| `DeptController.java` | Merged into EntityController |
| `DeptService.java` | Merged into EntityDemoService |
| `SystemController.java` | Replaced by AuthController |
| `SystemDemoService.java` | Replaced by AuthDemoService |
| `ProductController.java` | Merged into RedisDemoController |
| `OrderController.java` | Merged into RedisDemoController |
| `ProductDemoService.java` | Merged into RedisDemoService |
| `OrderDemoService.java` | Merged into RedisDemoService |
| `JvmInfo.java` (example) | Moved to starter |
| `MetricSummary.java` (example) | Moved to starter |
| `SystemDemo.vue` | Replaced by AuthDemo.vue |
| `DeptDemo.vue` | Merged into EntityDemo.vue |
| `ProductDemo.vue` | Merged into RedisDemo.vue |
| `OrderDemo.vue` | Merged into RedisDemo.vue |

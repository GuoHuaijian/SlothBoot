# Module Standards & Boundary Refactoring Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enforce module boundaries, naming conventions, and dependency management across all 31 SlothBoot modules per the spec at `docs/superpowers/specs/2026-05-30-module-standards-design.md`.

**Architecture:** Move misplaced classes to their correct modules, rename modules/packages for consistency, and clean up dependency declarations. Each task is independently compilable and committable.

**Tech Stack:** Java 21, Maven, Spring Boot 4.0.6

---

### Task 1: Clean up sloth-boot-parent dependencies

**Goal:** Remove heavy dependencies from parent POM that should only be in common-core.

**Files:**
- Modify: `sloth-boot-parent/pom.xml`
- Modify: `sloth-boot-common/sloth-boot-common-core/pom.xml`

- [ ] **Step 1: Remove dependencies from sloth-boot-parent/pom.xml**

Remove these `<dependency>` entries from the `<dependencies>` section (keep `lombok`, `slf4j-api`, `jakarta.validation-api`, test deps, and `spring-boot-starter`):

```xml
<!-- REMOVE these -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>tools.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
<dependency>
    <groupId>tools.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
</dependency>
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-core</artifactId>
</dependency>
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>transmittable-thread-local</artifactId>
</dependency>
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <scope>provided</scope>
</dependency>
```

- [ ] **Step 2: Add missing dependencies to common-core/pom.xml**

The common-core pom.xml already has `hutool-core`, `guava`, `transmittable-thread-local`, `mapstruct`, `mapstruct-processor`, `jackson-databind`, `jakarta.servlet-api`. Add the missing ones that were only in parent:

```xml
<dependency>
    <groupId>tools.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
</dependency>
```

Place them after the existing `jackson-databind` entry.

- [ ] **Step 3: Remove redundant internal module version declarations**

In all leaf module pom.xml files (common-*, starter-*, generator, example-service), remove `<version>${revision}</version>` from internal dependency declarations. The BOM (`sloth-boot-dependencies`) already manages these versions.

For example, in each pom.xml change:
```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-common-core</artifactId>
    <version>${revision}</version>
</dependency>
```
to:
```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-common-core</artifactId>
</dependency>
```

Applies to all `sloth-boot-*` dependency references in: `sloth-boot-common-log`, `sloth-boot-common-security`, `sloth-boot-starter-web`, `sloth-boot-starter-redis`, `sloth-boot-starter-mq`, `sloth-boot-starter-monitor`, `sloth-boot-starter-mybatis`, `sloth-boot-starter-thread-pool`, `sloth-boot-starter-idempotent`, `sloth-boot-starter-feign`, `sloth-boot-starter-gateway`, `sloth-boot-starter-sentinel`, `sloth-boot-starter-es`, `sloth-boot-starter-oss`, `sloth-boot-starter-excel`, `sloth-boot-starter-job`, `sloth-boot-starter-seata`, `sloth-boot-starter-sms`, `sloth-boot-starter-auth`, `sloth-boot-starter-ai`, `sloth-boot-starter-monitor`, `sloth-boot-example-service`.

- [ ] **Step 4: Verify compilation**

Run: `mvn clean compile -pl sloth-boot-common/sloth-boot-common-core -am`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "refactor(deps): move heavy dependencies from parent to common-core, clean up internal versions"
```

---

### Task 2: Clean up CacheConstant business constants

**Goal:** Remove domain-specific cache prefixes and expiry times from common-core.

**Files:**
- Modify: `sloth-boot-common/sloth-boot-common-core/src/main/java/com/sloth/boot/common/constant/CacheConstant.java`

- [ ] **Step 1: Remove business-domain constants**

In `CacheConstant.java`, remove these constants:

**Key prefixes to REMOVE:**
- `DEFAULT_CACHE_PREFIX` ("sloth:") — managed by `RedisProperties.keyPrefix`
- `USER_CACHE_PREFIX` + its Javadoc
- `ROLE_CACHE_PREFIX` + its Javadoc
- `MENU_CACHE_PREFIX` + its Javadoc
- `DEPT_CACHE_PREFIX` + its Javadoc
- `DICT_CACHE_PREFIX` + its Javadoc
- `CONFIG_CACHE_PREFIX` + its Javadoc

**Expiry times to REMOVE:**
- `DEFAULT_EXPIRE_TIME`
- `USER_EXPIRE_TIME`
- `ROLE_EXPIRE_TIME`
- `MENU_EXPIRE_TIME`
- `DEPT_EXPIRE_TIME`
- `DICT_EXPIRE_TIME`
- `CONFIG_EXPIRE_TIME`

**KEEP these framework-level constants:**
- `CODE_CACHE_PREFIX`, `RATE_LIMIT_PREFIX`, `IDEMPOTENT_PREFIX`, `LOCK_PREFIX`, `OPERATE_LOG_PREFIX`
- `CODE_EXPIRE_TIME`, `NULL_VALUE_EXPIRE_TIME`
- `CACHE_WILDCARD`, `CACHE_SINGLE_WILDCARD`

- [ ] **Step 2: Commit**

```bash
git add sloth-boot-common/sloth-boot-common-core/src/main/java/com/sloth/boot/common/constant/CacheConstant.java
git commit -m "refactor(common-core): remove business-domain constants from CacheConstant"
```

---

### Task 3: Move DesensitizeUtil to common-security

**Goal:** Move security-related utility from common-core to common-security.

**Files:**
- Move: `sloth-boot-common/sloth-boot-common-core/src/main/java/com/sloth/boot/common/util/DesensitizeUtil.java`
  → `sloth-boot-common/sloth-boot-common-security/src/main/java/com/sloth/boot/common/security/desensitize/DesensitizeUtil.java`
- Modify: `sloth-boot-common/sloth-boot-common-security/src/main/java/com/sloth/boot/common/security/desensitize/DesensitizeSerializer.java`

- [ ] **Step 1: Create the new file**

Create `sloth-boot-common/sloth-boot-common-security/src/main/java/com/sloth/boot/common/security/desensitize/DesensitizeUtil.java` with the exact same content as the original, but change the package declaration from:

```java
package com.sloth.boot.common.util;
```

to:

```java
package com.sloth.boot.common.security.desensitize;
```

- [ ] **Step 2: Delete the old file**

```bash
rm sloth-boot-common/sloth-boot-common-core/src/main/java/com/sloth/boot/common/util/DesensitizeUtil.java
```

- [ ] **Step 3: Update DesensitizeSerializer import**

In `sloth-boot-common/sloth-boot-common-security/src/main/java/com/sloth/boot/common/security/desensitize/DesensitizeSerializer.java`, change:

```java
import com.sloth.boot.common.util.DesensitizeUtil;
```

to:

```java
import com.sloth.boot.common.security.desensitize.DesensitizeUtil;
```

- [ ] **Step 4: Commit**

```bash
git add -A sloth-boot-common/sloth-boot-common-core/ sloth-boot-common/sloth-boot-common-security/
git commit -m "refactor(security): move DesensitizeUtil from common-core to common-security"
```

---

### Task 4: Move servlet code to starter-web

**Goal:** Remove Servlet-dependent UI classes from common-core. Keep IpUtil in common-core (its `getClientIp()` uses servlet but is logically an IP utility and common-log needs it). Move RequestLogFilter and OperateLogAspect from common-log to starter-web (they are servlet/web-layer concerns; common-log keeps annotations and event model only).

**Dependency constraint:** starter-web → common-log, so common-log cannot depend on starter-web. Servlet classes in common-log must move to starter-web.

**Files:**
- Create: `sloth-boot-starter/sloth-boot-starter-web/src/main/java/com/sloth/boot/starter/web/util/ServletUtil.java`
- Move: `sloth-boot-common/sloth-boot-common-log/src/main/java/com/sloth/boot/common/log/filter/RequestLogFilter.java` → `sloth-boot-starter/sloth-boot-starter-web/src/main/java/com/sloth/boot/starter/web/log/RequestLogFilter.java`
- Move: `sloth-boot-common/sloth-boot-common-log/src/main/java/com/sloth/boot/common/log/aspect/OperateLogAspect.java` → `sloth-boot-starter/sloth-boot-starter-web/src/main/java/com/sloth/boot/starter/web/log/OperateLogAspect.java`
- Delete: `sloth-boot-common/sloth-boot-common-core/src/main/java/com/sloth/boot/common/util/ServletUtil.java`
- Delete: `sloth-boot-common/sloth-boot-common-core/src/main/java/com/sloth/boot/common/interceptor/AbstractHandlerInterceptor.java`
- Modify: `sloth-boot-starter/sloth-boot-starter-idempotent/src/main/java/com/sloth/boot/starter/idempotent/aspect/IdempotentAspect.java`
- Modify: `sloth-boot-starter/sloth-boot-starter-idempotent/src/main/java/com/sloth/boot/starter/idempotent/spi/DefaultIdempotentKeyStrategy.java`
- Modify: `sloth-boot-starter/sloth-boot-starter-redis/src/main/java/com/sloth/boot/starter/redis/limiter/RateLimiterAspect.java`

- [ ] **Step 1: Create ServletUtil in starter-web**

Create `sloth-boot-starter/sloth-boot-starter-web/src/main/java/com/sloth/boot/starter/web/util/ServletUtil.java` with the full content of the original from common-core, changing:
- Package: `package com.sloth.boot.starter.web.util;`
- Keep `import com.sloth.boot.common.util.IpUtil;` (IpUtil stays in common-core)

- [ ] **Step 2: Move RequestLogFilter to starter-web**

Create `sloth-boot-starter/sloth-boot-starter-web/src/main/java/com/sloth/boot/starter/web/log/RequestLogFilter.java` with the content from common-log, changing:
- Package: `package com.sloth.boot.starter.web.log;`
- Import: `import com.sloth.boot.common.util.ServletUtil;` → `import com.sloth.boot.starter.web.util.ServletUtil;`

Delete the original: `sloth-boot-common/sloth-boot-common-log/src/main/java/com/sloth/boot/common/log/filter/RequestLogFilter.java`

- [ ] **Step 3: Move OperateLogAspect to starter-web**

Create `sloth-boot-starter/sloth-boot-starter-web/src/main/java/com/sloth/boot/starter/web/log/OperateLogAspect.java` with the content from common-log, changing:
- Package: `package com.sloth.boot.starter.web.log;`
- Import: `import com.sloth.boot.common.util.ServletUtil;` → `import com.sloth.boot.starter.web.util.ServletUtil;`

Delete the original: `sloth-boot-common/sloth-boot-common-log/src/main/java/com/sloth/boot/common/log/aspect/OperateLogAspect.java`

- [ ] **Step 4: Check common-log AutoConfiguration for bean registrations**

Read `sloth-boot-common/sloth-boot-common-log/src/main/java/com/sloth/boot/common/log/config/` to see if `RequestLogFilter` or `OperateLogAspect` are registered as beans. If so, move those bean definitions to `sloth-boot-starter/sloth-boot-starter-web/.../config/WebAutoConfiguration.java` (or equivalent).

- [ ] **Step 5: Delete old Servlet files from common-core**

```bash
rm sloth-boot-common/sloth-boot-common-core/src/main/java/com/sloth/boot/common/util/ServletUtil.java
rm sloth-boot-common/sloth-boot-common-core/src/main/java/com/sloth/boot/common/interceptor/AbstractHandlerInterceptor.java
```

Delete the `interceptor` package-info.java if no other classes remain.

- [ ] **Step 6: Update imports in 3 starter files**

**starter-idempotent `IdempotentAspect.java`:**
```java
// OLD: import com.sloth.boot.common.util.ServletUtil;
// NEW: import com.sloth.boot.starter.web.util.ServletUtil;
```

**starter-idempotent `DefaultIdempotentKeyStrategy.java`:**
```java
// OLD: import com.sloth.boot.common.util.ServletUtil;
// NEW: import com.sloth.boot.starter.web.util.ServletUtil;
```
(Keep `import com.sloth.boot.common.util.IpUtil;` — IpUtil stays in common-core)

**starter-redis `RateLimiterAspect.java`:**
```java
// OLD: import com.sloth.boot.common.util.ServletUtil;
// NEW: import com.sloth.boot.starter.web.util.ServletUtil;
```

**POM changes:** Add `sloth-boot-starter-web` as optional dependency to starter-idempotent and starter-redis:
```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-web</artifactId>
    <optional>true</optional>
</dependency>
```

- [ ] **Step 7: Verify compilation**

Run: `mvn clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
git add -A
git commit -m "refactor(web): move Servlet code from common-core/common-log to starter-web"
```

---

### Task 5: Fix HealthIndicator placement — Redis to starter-redis

**Goal:** Move RedisHealthIndicator from starter-monitor to starter-redis.

**Files:**
- Create: `sloth-boot-starter/sloth-boot-starter-redis/src/main/java/com/sloth/boot/starter/redis/health/RedisHealthIndicator.java`
- Delete: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/health/RedisHealthIndicator.java`
- Modify: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/config/MonitorAutoConfiguration.java`

- [ ] **Step 1: Create RedisHealthIndicator in starter-redis**

Create `sloth-boot-starter/sloth-boot-starter-redis/src/main/java/com/sloth/boot/starter/redis/health/RedisHealthIndicator.java` with the exact content of the original file from starter-monitor, changing only:

```java
package com.sloth.boot.starter.redis.health;
```

- [ ] **Step 2: Delete from starter-monitor**

```bash
rm sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/health/RedisHealthIndicator.java
```

- [ ] **Step 3: Update MonitorAutoConfiguration**

In `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/config/MonitorAutoConfiguration.java`:

- Remove: `import com.sloth.boot.starter.monitor.health.RedisHealthIndicator;`
- Remove the `RedisHealthIndicator` bean definition method (or `@Bean` + `@ConditionalOnClass` for it)
- Remove the `spring-data-redis` dependency from `sloth-boot-starter-monitor/pom.xml` (the `spring-boot-starter-data-redis` optional dep can also be removed if only used by this indicator)

- [ ] **Step 4: Commit**

```bash
git add -A sloth-boot-starter/sloth-boot-starter-redis/ sloth-boot-starter/sloth-boot-starter-monitor/
git commit -m "refactor(monitor): move RedisHealthIndicator to starter-redis"
```

---

### Task 6: Delete duplicate RocketMQ HealthIndicator from starter-monitor

**Goal:** Remove duplicate; starter-mq has the stronger version (with ServiceState check).

**Files:**
- Delete: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/health/RocketMQHealthIndicator.java`
- Modify: `sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/config/MonitorAutoConfiguration.java`
- Modify: `sloth-boot-starter/sloth-boot-starter-monitor/pom.xml`

- [ ] **Step 1: Delete the file**

```bash
rm sloth-boot-starter/sloth-boot-starter-monitor/src/main/java/com/sloth/boot/starter/monitor/health/RocketMQHealthIndicator.java
```

- [ ] **Step 2: Update MonitorAutoConfiguration**

- Remove: `import com.sloth.boot.starter.monitor.health.RocketMQHealthIndicator;`
- Remove the `RocketMQHealthIndicator` bean definition

- [ ] **Step 3: Remove rocketmq dependency from monitor pom.xml**

In `sloth-boot-starter/sloth-boot-starter-monitor/pom.xml`, remove:
```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <optional>true</optional>
</dependency>
```

- [ ] **Step 4: Commit**

```bash
git add -A sloth-boot-starter/sloth-boot-starter-monitor/
git commit -m "refactor(monitor): remove duplicate RocketMQHealthIndicator (starter-mq has stronger version)"
```

---

### Task 7: Rename thread-pool package `thread` → `threadpool`

**Goal:** Package name must match artifact suffix (`thread-pool` → `threadpool`).

**Files:**
- Move 5 files from `.../thread/monitor/` → `.../threadpool/metrics/` (and rename `monitor` → `metrics`)
- Modify 1 file: `ThreadPoolAutoConfiguration.java` imports
- Modify META-INF imports file
- Move ALL other files in `.../thread/` → `.../threadpool/`

- [ ] **Step 1: List all files to move**

All Java files under `sloth-boot-starter/sloth-boot-starter-thread-pool/src/main/java/com/sloth/boot/starter/thread/` must move to `.../threadpool/`. The `monitor` sub-package becomes `metrics`.

Full mapping:
- `thread/config/*` → `threadpool/config/*`
- `thread/core/*` → `threadpool/core/*`
- `thread/monitor/*` → `threadpool/metrics/*`
- `thread/support/*` → `threadpool/support/*`
- (any other sub-packages follow the same pattern)

- [ ] **Step 2: Move all files**

```bash
cd sloth-boot-starter/sloth-boot-starter-thread-pool/src/main/java/com/sloth/boot/starter/
mkdir -p threadpool
# Copy the directory structure
cp -r thread/* threadpool/
# Rename monitor to metrics in the copy
mv threadpool/monitor threadpool/metrics
rm -rf thread
```

- [ ] **Step 3: Update package declarations in all moved files**

In every `.java` file under the new `threadpool/` directory:
- Replace `package com.sloth.boot.starter.thread.` → `package com.sloth.boot.starter.threadpool.`
- Replace `package com.sloth.boot.starter.thread;` → `package com.sloth.boot.starter.threadpool;` (if any top-level files)
- Replace `.thread.monitor.` → `.threadpool.metrics.` in package declarations and imports

- [ ] **Step 4: Update AutoConfiguration imports**

`sloth-boot-starter/sloth-boot-starter-thread-pool/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```
com.sloth.boot.starter.threadpool.config.ThreadPoolAutoConfiguration
```

- [ ] **Step 5: Update test files**

Apply the same package rename to all files under `src/test/java/`.

- [ ] **Step 6: Verify compilation**

Run: `mvn clean compile -pl sloth-boot-starter/sloth-boot-starter-thread-pool -am`
Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add -A sloth-boot-starter/sloth-boot-starter-thread-pool/
git commit -m "refactor(thread-pool): rename package thread→threadpool, monitor→metrics"
```

---

### Task 8: Rename mq module to rocketmq

**Goal:** Full rename: directory, artifactId, package, class names.

**Files:**
- Rename directory: `sloth-boot-starter-mq/` → `sloth-boot-starter-rocketmq/`
- Modify: `sloth-boot-starter/pom.xml` (module declaration)
- Modify: `sloth-boot-dependencies/pom.xml` (artifactId in dependencyManagement)
- Modify: All 18 Java files + 1 test file in the module
- Modify: META-INF imports file
- Modify: README.md

- [ ] **Step 1: Rename the directory**

```bash
cd sloth-boot-starter/
mv sloth-boot-starter-mq sloth-boot-starter-rocketmq
```

- [ ] **Step 2: Update POM files**

**`sloth-boot-starter-rocketmq/pom.xml`:**
- Change `<artifactId>sloth-boot-starter-mq</artifactId>` → `<artifactId>sloth-boot-starter-rocketmq</artifactId>`

**`sloth-boot-starter/pom.xml`:**
- Change `<module>sloth-boot-starter-mq</module>` → `<module>sloth-boot-starter-rocketmq</module>`

**`sloth-boot-dependencies/pom.xml`:**
- Change `<artifactId>sloth-boot-starter-mq</artifactId>` → `<artifactId>sloth-boot-starter-rocketmq</artifactId>` (in the internal module dependencyManagement section)

- [ ] **Step 3: Rename package in all Java files**

In every `.java` file under `sloth-boot-starter-rocketmq/src/`:
- Replace `package com.sloth.boot.starter.mq` → `package com.sloth.boot.starter.rocketmq`
- Replace `import com.sloth.boot.starter.mq.` → `import com.sloth.boot.starter.rocketmq.`

- [ ] **Step 4: Rename MQ-prefixed classes to RocketMQ-prefixed**

Rename files:
- `config/MQAutoConfiguration.java` → `config/RocketMQAutoConfiguration.java`
- `config/MQProperties.java` → `config/RocketMQProperties.java`
- `monitor/MQHealthIndicator.java` → `health/RocketMQHealthIndicator.java` (also move from `monitor` to `health`)

In each renamed file, update the class name. Also update all references to these class names in other files within the module.

- [ ] **Step 5: Move HealthIndicator from `monitor` to `health` sub-package**

The `monitor/MQHealthIndicator.java` moves to `health/RocketMQHealthIndicator.java`:
- New package: `com.sloth.boot.starter.rocketmq.health`
- New class name: `RocketMQHealthIndicator`

Delete the now-empty `monitor/` directory and its `package-info.java`.

- [ ] **Step 6: Update META-INF imports**

`sloth-boot-starter-rocketmq/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```
com.sloth.boot.starter.rocketmq.config.RocketMQAutoConfiguration
```

- [ ] **Step 7: Update README.md**

In `sloth-boot-starter-rocketmq/README.md`:
- Replace all `sloth-boot-starter-mq` → `sloth-boot-starter-rocketmq`
- Replace all `MQ*` class references → `RocketMQ*`
- Replace `sloth.mq.` config prefix references → `sloth.rocketmq.` (if renaming config prefix; otherwise keep `sloth.mq.` for backward compat — decision: keep `sloth.mq.` for now since config prefix rename is a separate concern)

- [ ] **Step 8: Verify compilation**

Run: `mvn clean compile -pl sloth-boot-starter/sloth-boot-starter-rocketmq -am`
Expected: BUILD SUCCESS

- [ ] **Step 9: Commit**

```bash
git add -A
git commit -m "refactor(rocketmq): rename mq module to rocketmq (directory, artifactId, package, classes)"
```

---

### Task 9: Rename MybatisPlusAutoConfiguration to MybatisAutoConfiguration

**Goal:** Remove vendor name from AutoConfiguration class.

**Files:**
- Rename: `sloth-boot-starter/sloth-boot-starter-mybatis/src/main/java/com/sloth/boot/starter/mybatis/config/MybatisPlusAutoConfiguration.java` → `MybatisAutoConfiguration.java`
- Modify: the renamed file (class name)
- Modify: `sloth-boot-starter/sloth-boot-starter-mybatis/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Modify: `sloth-boot-starter/sloth-boot-starter-mybatis/src/test/java/com/sloth/boot/starter/mybatis/config/MybatisPlusAutoConfigurationTest.java`

- [ ] **Step 1: Rename and update the class**

Rename file `MybatisPlusAutoConfiguration.java` → `MybatisAutoConfiguration.java`.

In the file, change:
```java
public class MybatisPlusAutoConfiguration {
```
to:
```java
public class MybatisAutoConfiguration {
```

Update the test class name from `MybatisPlusAutoConfigurationTest` → `MybatisAutoConfigurationTest` and its reference to `MybatisPlusAutoConfiguration` → `MybatisAutoConfiguration`.

- [ ] **Step 2: Update META-INF imports**

`sloth-boot-starter/sloth-boot-starter-mybatis/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```
com.sloth.boot.starter.mybatis.config.MybatisAutoConfiguration
```

- [ ] **Step 3: Commit**

```bash
git add -A sloth-boot-starter/sloth-boot-starter-mybatis/
git commit -m "refactor(mybatis): rename MybatisPlusAutoConfiguration to MybatisAutoConfiguration"
```

---

### Task 10: Unify ES HealthIndicator sub-package

**Goal:** Move EsHealthIndicator from `monitoring` to `health` sub-package.

**Files:**
- Create: `sloth-boot-starter/sloth-boot-starter-es/src/main/java/com/sloth/boot/starter/es/health/EsHealthIndicator.java`
- Delete: `sloth-boot-starter/sloth-boot-starter-es/src/main/java/com/sloth/boot/starter/es/monitoring/EsHealthIndicator.java`
- Modify: `sloth-boot-starter/sloth-boot-starter-es/src/main/java/com/sloth/boot/starter/es/monitoring/package-info.java`

- [ ] **Step 1: Create the new file**

Create `sloth-boot-starter/sloth-boot-starter-es/src/main/java/com/sloth/boot/starter/es/health/EsHealthIndicator.java` with the same content, changing:

```java
package com.sloth.boot.starter.es.health;
```

- [ ] **Step 2: Delete the old file**

```bash
rm sloth-boot-starter/sloth-boot-starter-es/src/main/java/com/sloth/boot/starter/es/monitoring/EsHealthIndicator.java
```

- [ ] **Step 3: Update package-info.java**

Update `sloth-boot-starter/sloth-boot-starter-es/src/main/java/com/sloth/boot/starter/es/monitoring/package-info.java` to reflect that health checks are no longer in this package (remove mention of "health check" from the description).

- [ ] **Step 4: Commit**

```bash
git add -A sloth-boot-starter/sloth-boot-starter-es/
git commit -m "refactor(es): move EsHealthIndicator from monitoring to health sub-package"
```

---

### Task 11: Final compilation and verification

**Goal:** Ensure the entire project compiles and tests pass.

- [ ] **Step 1: Full compilation**

Run: `mvn clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 2: Full test suite**

Run: `mvn test`
Expected: All tests pass

- [ ] **Step 3: Fix any compilation errors**

If compilation fails, fix import paths, missing dependencies, or broken references.

- [ ] **Step 4: Final commit (if fixes needed)**

```bash
git add -A
git commit -m "fix: resolve compilation issues from module refactoring"
```

---

## Summary

| Task | Description | Impact |
|------|-------------|--------|
| 1 | Parent POM dependency cleanup | All modules |
| 2 | CacheConstant cleanup | common-core |
| 3 | Move DesensitizeUtil | common-core → common-security |
| 4 | Move servlet code | common-core/common-log → starter-web |
| 5 | Move RedisHealthIndicator | starter-monitor → starter-redis |
| 6 | Delete duplicate RocketMQ indicator | starter-monitor |
| 7 | Rename thread-pool package | starter-thread-pool |
| 8 | Rename mq → rocketmq | starter-mq (full rename) |
| 9 | Rename MybatisPlus → Mybatis | starter-mybatis |
| 10 | Unify ES HealthIndicator package | starter-es |
| 11 | Final verification | Whole project |

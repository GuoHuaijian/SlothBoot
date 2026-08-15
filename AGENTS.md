# AGENTS.md

This file applies to the whole repository.

## Project Context

Sloth Boot is a Java 21, Spring Boot 4, Spring Cloud Alibaba multi-module Maven project. It is a reusable foundation library and starter set, not a business application.

Main module roles:

- `sloth-boot-dependencies`: dependency BOM and version alignment.
- `sloth-boot-parent`: shared parent POM, compiler, plugin, and baseline dependencies.
- `sloth-boot-common-core`: framework-neutral common capabilities. Must not depend on Spring.
- `sloth-boot-common-log` / `sloth-boot-common-security` / `sloth-boot-common-doc` / `sloth-boot-common-test`: Spring-aware shared modules (auto-configurations, servlet filters, SpEL helpers).
- `sloth-boot-starter`: optional Spring Boot starter capabilities.
- `sloth-boot-example`: runnable examples only.

## Framework Neutrality Rules

- `sloth-boot-common-core` must compile and run without Spring on the classpath. No `org.springframework.*` imports, no auto-configuration classes, no `@Component`/`@ConfigurationProperties`.
- Spring-related shared helpers (e.g. SpEL parsing) live in `sloth-boot-common-log`.
- `sloth-boot-common-log` / `sloth-boot-common-security` are the sanctioned Spring-aware common modules; servlet filters and auto-configurations belong there or in a starter, never in `sloth-boot-common-core`.
- Do not add static service locators that reach into the Spring container (`ApplicationContextAware`, `RequestContextHolder`, static `MessageSource` holders). Use constructor injection.
- Spring events are plain POJO/record payloads published through `ApplicationEventPublisher`; never extend `ApplicationEvent`.
- Internal i18n messages are read through the neutral `I18nMessages` helper (JDK `ResourceBundle`, `Locale.getDefault()`); do not reintroduce Spring `MessageSource` holders in common modules.
- A type handler instantiated by a third-party framework (e.g. MyBatis) that needs configuration must expose a static setter invoked by auto-configuration instead of reaching into the container.

## Engineering Rules

- Keep changes scoped to the module and behavior being modified.
- Prefer existing local patterns before adding new abstractions.
- Do not introduce platform code into `example`; examples consume framework modules.
- Do not put business-specific behavior into `common` or generic starters.
- Public starter behavior must be override-friendly through `@ConditionalOnMissingBean`.
- All custom configuration properties must use the `sloth.*` namespace.
- Every Spring Boot 4 auto-configuration class must be registered through `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
- Add or update `spring-configuration-metadata-additional.json` when adding public configuration keys.
- Avoid hard-coded secrets, tokens, credentials, URLs, tenant IDs, or environment-specific values in source code.

## Java Style

Follow the Alibaba Java Development Manual as the baseline:

- Use clear, intention-revealing names. Avoid vague names such as `data`, `info`, `temp`, or `manager` unless the domain meaning is obvious.
- Class names use `UpperCamelCase`; methods, fields, and local variables use `lowerCamelCase`; constants use `UPPER_SNAKE_CASE`.
- Do not use magic values. Extract constants when a value has business or protocol meaning.
- Never compare boxed values with `==`; use value equality.
- Avoid `NullPointerException` by validating external inputs and using explicit defaults where needed.
- Do not swallow exceptions. Preserve the cause when wrapping exceptions.
- Log with parameterized messages. Do not concatenate strings in log calls.
- Do not log secrets, tokens, passwords, API keys, signatures, or full sensitive payloads.
- Use `BigDecimal` for money and precision-sensitive decimal calculations.
- Do not use floating point values for exact comparisons.
- Do not return internal mutable collections directly from public APIs.
- Specify collection initial capacity when the expected size is known.
- Prefer `isEmpty()` over `size() == 0`.
- Use `ThreadPoolExecutor` with explicit bounds instead of unbounded ad hoc executors.
- Always restore interrupt state after catching `InterruptedException`.

## Design Principles

- Apply SOLID pragmatically: keep classes cohesive, depend on abstractions where it removes real coupling, and keep extension points narrow.
- Use KISS and YAGNI. Do not add generic frameworks, factories, or layers before the need exists.
- Use DRY for meaningful duplication, not for coincidental similarity.
- Prefer composition over inheritance.
- Follow the Law of Demeter: keep cross-module knowledge shallow.
- Fail fast for invalid configuration and unsafe runtime states.
- Keep APIs small, stable, and explicit. A starter should expose configuration and replaceable beans, not hidden global behavior.

## Design Patterns

Use patterns only when they simplify the code:

- Strategy: pluggable provider implementations, such as SMS or OSS clients.
- Template Method: shared workflow with controlled extension points.
- Adapter: wrapping third-party clients behind Sloth Boot interfaces.
- Factory: selecting implementations from validated configuration.
- Decorator/Interceptor/Aspect: cross-cutting behavior such as logging, locks, rate limiting, and response wrapping.

Avoid pattern-driven overengineering. If a direct class or method is clearer, use it.

## Maven And Dependency Rules

- Keep versions centralized in `sloth-boot-dependencies` or root properties.
- Child modules should not declare dependency versions unless there is a clear isolation reason.
- Use correct Maven coordinates. Do not invent group IDs.
- Starter dependencies should be minimal and optional where consumers should choose the implementation.
- Example modules may include runtime drivers and demo-only dependencies, but these must not leak into common modules.

## Spring Boot Starter Rules

- Auto-configurations must be guarded with suitable conditions such as `@ConditionalOnClass`, `@ConditionalOnProperty`, and `@ConditionalOnMissingBean`.
- Default-enabled starters must not fail application startup when their backing service is absent unless the starter cannot function without it.
- Prefer typed `@ConfigurationProperties` over reading raw environment keys.
- Keep bean names stable when consumers may override them.
- Do not use component scanning inside starters as the primary registration mechanism.

## Testing And Verification

- For compile or dependency changes, run `mvn -B clean verify -DskipTests` at minimum.
- For behavior changes, add focused unit or slice tests in the changed module.
- For auto-configuration changes, prefer `ApplicationContextRunner` tests.
- Do not rely on the example service as the only verification path.
- If Maven or required services are unavailable locally, state that explicitly in the final report.

## Review Priorities

When reviewing or changing this repository, prioritize:

1. Compile correctness and dependency resolution.
2. Runtime startup safety.
3. Public API and configuration compatibility.
4. Security and sensitive data exposure.
5. Test coverage for changed behavior.
6. Documentation and example accuracy.

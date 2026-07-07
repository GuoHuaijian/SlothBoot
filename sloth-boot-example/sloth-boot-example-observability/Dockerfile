FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="sloth-boot <564559079@qq.com>"
LABEL description="Sloth Boot 可观测性演示服务"

# 安装 OTel Java Agent 2.10.0
ENV OTEL_AGENT_VERSION=2.10.0
RUN apk add --no-cache wget \
    && wget -q "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" -O /opt/opentelemetry-javaagent.jar \
    && apk del wget

WORKDIR /app

# 复制预构建的 jar
COPY target/*-exec.jar app.jar

# 创建日志目录
RUN mkdir -p /var/log/sloth-observability

# 暴露端口
EXPOSE 8080

# JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -javaagent:/opt/opentelemetry-javaagent.jar -jar app.jar"]

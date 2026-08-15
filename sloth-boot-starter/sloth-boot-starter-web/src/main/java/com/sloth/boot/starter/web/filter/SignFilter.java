package com.sloth.boot.starter.web.filter;

import com.sloth.boot.common.security.sign.SignProperties;
import com.sloth.boot.common.security.sign.SignUtil;
import com.sloth.boot.common.util.JsonUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求签名验证过滤器。
 * <p>
 * 在 {@code sloth.sign.enabled=true} 时生效。客户端需通过请求头携带
 * {@code X-Sign}、{@code X-Timestamp}（毫秒时间戳）与 {@code X-Nonce}（随机串），
 * 签名对象为请求全部查询参数与表单参数（合并后的 {@code getParameterMap}）。
 * <p>
 * 校验规则：时间戳在有效窗口内、签名匹配、nonce 在窗口内未重复使用
 * （窗口内防重放）。排除路径通过 {@code sloth.sign.exclude-paths} 配置（Ant 风格）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class SignFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SignFilter.class);

    /**
     * 签名请求头
     */
    public static final String SIGN_HEADER = "X-Sign";

    /**
     * 时间戳请求头（毫秒）
     */
    public static final String TIMESTAMP_HEADER = "X-Timestamp";

    /**
     * 随机数请求头
     */
    public static final String NONCE_HEADER = "X-Nonce";

    /**
     * 防重放 nonce 存储上限，超过后清理过期项
     */
    private static final int MAX_NONCE_ENTRIES = 100_000;

    private static final int REJECTED_STATUS = HttpServletResponse.SC_UNAUTHORIZED;

    private final SignProperties signProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ConcurrentHashMap<String, Long> nonces = new ConcurrentHashMap<>();

    /**
     * 构造签名过滤器。
     *
     * @param signProperties 签名配置
     */
    public SignFilter(SignProperties signProperties) {
        this.signProperties = signProperties;
        if (signProperties.isEnabled() && signProperties.getSecretKey() == null) {
            throw new IllegalStateException("sloth.sign.enabled=true 时必须配置 sloth.sign.secret-key");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        for (String excludePath : signProperties.getExcludePaths()) {
            if (pathMatcher.match(excludePath, requestUri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String sign = request.getHeader(SIGN_HEADER);
        String timestampHeader = request.getHeader(TIMESTAMP_HEADER);
        String nonce = request.getHeader(NONCE_HEADER);
        if (sign == null || timestampHeader == null || nonce == null) {
            writeRejected(response, "missing sign, timestamp or nonce header");
            return;
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampHeader);
        } catch (NumberFormatException e) {
            writeRejected(response, "invalid timestamp header");
            return;
        }

        Long previous = nonces.putIfAbsent(nonce, timestamp);
        if (previous != null) {
            writeRejected(response, "nonce already used");
            return;
        }

        try {
            Map<String, String[]> rawParams = request.getParameterMap();
            Map<String, Object> params = new HashMap<>(rawParams.size());
            rawParams.forEach((name, values) -> {
                if (values != null && values.length > 0) {
                    params.put(name, values.length == 1 ? values[0] : values);
                }
            });

            boolean verified = SignUtil.verifySign(params, sign, signProperties.getSecretKey(),
                timestamp, nonce, signProperties.getValidTime());
            if (!verified) {
                log.warn("[Sign] signature verification failed, uri: {}, clientIp: {}, timestamp: {}",
                    request.getRequestURI(), request.getRemoteAddr(), timestamp);
                writeRejected(response, "invalid signature");
                return;
            }
            filterChain.doFilter(request, response);
        } finally {
            if (nonces.size() > MAX_NONCE_ENTRIES) {
                pruneExpiredNonces();
            }
        }
    }

    private void pruneExpiredNonces() {
        long cutoff = System.currentTimeMillis() - signProperties.getValidTime() * 1000L;
        nonces.entrySet().removeIf(entry -> entry.getValue() < cutoff);
    }

    private void writeRejected(HttpServletResponse response, String message) throws IOException {
        response.setStatus(REJECTED_STATUS);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(JsonUtil.toJson(Map.of("code", REJECTED_STATUS, "message", message)));
    }
}

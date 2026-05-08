package com.sloth.boot.common.test;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring Boot 集成测试基类。
 * <p>
 * 提供统一的测试上下文配置，自动激活 test profile。 子类直接继承即可获得 Spring Boot 测试能力，无需重复配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseSpringBootTest {

    /**
     * 测试前的初始化钩子，子类可覆盖以添加自定义初始化逻辑。
     */
    @BeforeEach
    public void setUp() {
        // 测试前的初始化逻辑
    }
}

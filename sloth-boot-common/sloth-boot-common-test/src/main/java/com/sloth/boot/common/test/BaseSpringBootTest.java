package com.sloth.boot.common.test;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring Boot 测试基类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseSpringBootTest {

    @BeforeEach
    public void setUp() {
        // 测试前的初始化逻辑
    }
}
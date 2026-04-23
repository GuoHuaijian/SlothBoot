package com.sloth.boot.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 示例服务启动类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = Application.BASE_PACKAGE)
public class Application {

    private static final String BASE_PACKAGE = "com.sloth.boot";

    /**
     * 应用入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

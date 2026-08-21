package com.sloth.boot.generator.render;

/**
 * 模板渲染引擎抽象，隔离具体模板技术。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface TemplateEngine {

    /**
     * 渲染模板。
     *
     * @param templateLocation 模板类路径（如 templates/po.java.vm）
     * @param model            视图模型
     * @return 渲染结果
     */
    String render(String templateLocation, Object model);
}

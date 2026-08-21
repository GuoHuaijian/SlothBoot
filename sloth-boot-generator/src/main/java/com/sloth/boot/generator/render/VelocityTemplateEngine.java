package com.sloth.boot.generator.render;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.sloth.boot.common.util.AssertUtil;

/**
 * 基于 Velocity 的模板渲染引擎。
 * <p>
 * 从类路径加载模板，开启严格引用模式——模板中引用不存在的属性将直接抛错，
 * 避免静默输出错误代码。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class VelocityTemplateEngine implements TemplateEngine {

    private static final String RESOURCE_LOADERS = "resource.loaders";
    private static final String RESOURCE_LOADER_CLASSPATH = "resource.loader.classpath";
    private static final String CLASSPATH_LOADER_IMPL =
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";

    private final VelocityEngine engine;

    public VelocityTemplateEngine() {
        this.engine = createEngine();
    }

    @Override
    public String render(String templateLocation, Object model) {
        AssertUtil.notBlank(templateLocation, "模板路径不能为空");
        Template template = engine.getTemplate(templateLocation, StandardCharsets.UTF_8.name());
        // 必须使用可变 Map：Velocity 的 Foreach 指令会向上下文写入 $foreach 作用域变量
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("model", model);
        VelocityContext context = new VelocityContext(contextMap);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private VelocityEngine createEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RESOURCE_LOADERS, "classpath");
        velocityEngine.setProperty(RESOURCE_LOADER_CLASSPATH + ".class", CLASSPATH_LOADER_IMPL);
        velocityEngine.setProperty(RuntimeConstants.VM_PERM_INLINE_LOCAL, true);
        // 关闭空白吞噬：模板通过显式 "##" 控制换行，保证生成代码缩进与空行完全可控
        velocityEngine.setProperty(RuntimeConstants.SPACE_GOBBLING, "none");
        // 严格引用：引用不存在的属性时抛出异常，尽早暴露模板缺陷
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_REFERENCES_STRICT, true);
        velocityEngine.init();
        return velocityEngine;
    }
}

package com.sloth.boot.common.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.GenericTypeResolver;

/**
 * 泛型事件监听器基类
 * <p>
 * 自动解析泛型参数，只处理匹配类型的事件。子类只需实现 {@link #onEvent} 方法。
 * <p>
 * 使用示例：
 * <pre>
 * public class OrderEventListener extends AbstractEventListener&lt;OrderEvent&gt; {
 *
 *     protected void onEvent(OrderEvent event) {
 *         // 处理订单事件
 *     }
 * }
 * </pre>
 *
 * @param <T> 事件类型
 * @author sloth-boot
 * @since 1.0.0
 */
public abstract class AbstractEventListener<T extends ApplicationEvent> implements ApplicationListener<ApplicationEvent> {

    private final Class<?> eventType;

    @SuppressWarnings("unchecked")
    protected AbstractEventListener() {
        Class<?> typeArg = GenericTypeResolver.resolveTypeArgument(getClass(), AbstractEventListener.class);
        this.eventType = typeArg != null ? typeArg : ApplicationEvent.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(ApplicationEvent event) {
        if (eventType.isInstance(event)) {
            onEvent((T) event);
        }
    }

    /**
     * 处理事件
     *
     * @param event 事件对象
     */
    protected abstract void onEvent(T event);
}

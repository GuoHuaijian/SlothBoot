package com.sloth.boot.starter.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.sloth.boot.common.result.R;

/**
 * Sentinel 全局拦截处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class GlobalBlockHandler {

    /**
     * 统一处理 Sentinel Block 异常。
     *
     * @param exception Block 异常
     * @return 统一响应
     */
    public R<Void> handle(BlockException exception) {
        if (exception instanceof FlowException) {
            return R.fail(429, "请求过于频繁，请稍后再试");
        }
        if (exception instanceof DegradeException) {
            return R.fail(503, "服务暂时不可用，请稍后再试");
        }
        if (exception instanceof ParamFlowException) {
            return R.fail(429, "热点参数限流");
        }
        if (exception instanceof SystemBlockException) {
            return R.fail(503, "系统保护触发");
        }
        if (exception instanceof AuthorityException) {
            return R.fail(403, "授权规则不通过");
        }
        return R.fail(429, "请求被限流，请稍后再试");
    }
}

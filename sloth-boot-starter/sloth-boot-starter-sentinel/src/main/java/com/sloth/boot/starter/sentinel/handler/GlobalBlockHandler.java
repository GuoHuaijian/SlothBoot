package com.sloth.boot.starter.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.sloth.boot.common.result.R;
import com.sloth.boot.common.util.I18nUtil;

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
            return R.fail(429, I18nUtil.getMessage("sloth.error.sentinel_flow"));
        }
        if (exception instanceof DegradeException) {
            return R.fail(503, I18nUtil.getMessage("sloth.error.sentinel_degrade"));
        }
        if (exception instanceof ParamFlowException) {
            return R.fail(429, I18nUtil.getMessage("sloth.error.sentinel_flow"));
        }
        if (exception instanceof SystemBlockException) {
            return R.fail(503, I18nUtil.getMessage("sloth.error.sentinel_system"));
        }
        if (exception instanceof AuthorityException) {
            return R.fail(403, I18nUtil.getMessage("sloth.error.sentinel_authority"));
        }
        return R.fail(429, I18nUtil.getMessage("sloth.error.sentinel_blocked"));
    }
}

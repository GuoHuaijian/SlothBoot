package com.sloth.boot.example.adapter.scheduler.user;

import com.sloth.boot.starter.job.core.AbstractJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户统计作业处理器。
 * <p>
 * 继承 AbstractJobHandler，实现 XXL-Job 作业。
 * <p>
 * 命名规范：采用 模块+动作+Job 的命名方式
 * <p>
 * XXL-Job 配置：
 * - 作业名称：userStatsJob
 * - Cron 表达式：0 0 2 * * ? (每天凌晨2点)
 * - 路由策略：轮询/故障转移
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
public class UserStatsJob extends AbstractJobHandler {

    /**
     * 实现具体的作业逻辑。
     * <p>
     * 作业会在每天凌晨 2 点自动执行，统计用户数量。
     */
    @Override
    protected void doExecute() throws Exception {
        log.info("[UserStatsJob] 用户统计作业执行完成");
    }
}

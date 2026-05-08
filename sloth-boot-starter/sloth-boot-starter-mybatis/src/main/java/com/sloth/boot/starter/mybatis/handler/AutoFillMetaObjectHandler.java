package com.sloth.boot.starter.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.starter.mybatis.config.MybatisPlusProperties;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * MyBatis 自动填充处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    private static final Logger log = LoggerFactory.getLogger(AutoFillMetaObjectHandler.class);

    private final MybatisPlusProperties properties;

    public AutoFillMetaObjectHandler(MybatisPlusProperties properties) {
        this.properties = properties;
    }

    /**
     * 插入时自动填充字段。
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        String username = UserContext.getUsername();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createBy", String.class, username);
        this.strictInsertFill(metaObject, "updateBy", String.class, username);
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
        this.strictInsertFill(metaObject, "version", Integer.class, 1);

        // 租户 ID 自动填充（INSERT 时从 UserContext 获取，受 tenantAutoFill 配置控制）
        if (properties.isTenantAutoFill()) {
            try {
                String tenantId = UserContext.getTenantId();
                if (tenantId != null) {
                    this.strictInsertFill(metaObject, "tenantId", String.class, tenantId);
                }
            } catch (Exception e) {
                log.trace("自动填充 tenantId 失败, 跳过", e);
            }
        }
    }

    /**
     * 更新时自动填充字段。
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, UserContext.getUsername());
    }
}

package com.sloth.boot.common.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类（纯 POJO，不含持久化框架注解）
 * <p>
 * MyBatis-Plus 用户请使用 starter-mybatis 中的 {@code com.sloth.boot.starter.mybatis.core.BaseEntity}
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 逻辑删除标记 (0-正常, 1-已删除)
     */
    private Integer deleted;

    /**
     * 版本号
     */
    private Integer version;
}

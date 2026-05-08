package com.sloth.boot.common.base;

import lombok.Data;

import java.io.Serializable;

/**
 * VO 基类
 * <p>
 * 所有视图对象应继承此类。VO 用于前端展示层数据传输。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class BaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private java.time.LocalDateTime updateTime;
}

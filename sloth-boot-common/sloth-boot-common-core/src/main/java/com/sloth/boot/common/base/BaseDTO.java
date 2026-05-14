package com.sloth.boot.common.base;

import java.io.Serializable;

import lombok.Data;

/**
 * DTO 基类
 * <p>
 * 所有数据传输对象应继承此类。DTO 用于服务间、层间数据传输。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class BaseDTO implements Serializable {

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

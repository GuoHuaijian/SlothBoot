package com.sloth.boot.example.application.model.query.user;

import com.sloth.boot.common.base.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询条件。
 * <p>
 * 用于接收前端分页查询用户的请求参数。
 * 支持按用户名、手机号、部门ID、状态进行筛选。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户分页查询条件")
public class UserPageQry extends BaseQuery {

    /** 用户名（模糊匹配） */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 手机号（模糊匹配） */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /** 部门ID（精确匹配） */
    @Schema(description = "部门ID", example = "100")
    private Long deptId;

    /** 状态（精确匹配，0-正常, 1-停用） */
    @Schema(description = "状态", example = "1")
    private Integer status;
}

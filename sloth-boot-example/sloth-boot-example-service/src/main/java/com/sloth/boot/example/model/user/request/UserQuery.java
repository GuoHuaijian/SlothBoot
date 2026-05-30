package com.sloth.boot.example.model.user.request;

import com.sloth.boot.common.base.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询条件
 * <p>
 * 继承 {@link BaseQuery}，自动携带 pageNum/pageSize。
 * 配合 {@link com.sloth.boot.starter.mybatis.core.LambdaQueryWrapperX} 的 likeIfPresent/eqIfPresent 实现动态过滤。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户分页查询条件")
public class UserQuery extends BaseQuery {

    /** 用户名（模糊匹配） */
    @Schema(description = "用户名（模糊匹配）", example = "dev")
    private String username;

    /** 手机号（精确匹配） */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /** 所属部门ID */
    @Schema(description = "所属部门ID", example = "2")
    private Long deptId;

    /** 状态 */
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;
}

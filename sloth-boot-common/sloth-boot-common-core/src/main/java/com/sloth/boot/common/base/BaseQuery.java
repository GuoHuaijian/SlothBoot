package com.sloth.boot.common.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 查询基类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class BaseQuery {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方向 (asc/desc)
     */
    private String orderDirection = "desc";

    /**
     * 计算偏移量
     *
     * @return 偏移量
     */
    @JsonIgnore
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}

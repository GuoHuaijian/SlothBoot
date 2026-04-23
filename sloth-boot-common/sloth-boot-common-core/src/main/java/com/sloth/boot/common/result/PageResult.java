package com.sloth.boot.common.result;

import com.sloth.boot.common.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int totalPages;

    /**
     * 私有构造函数
     */
    private PageResult() {
    }

    /**
     * 创建分页结果
     *
     * @param list      数据列表
     * @param total     总记录数
     * @param pageNum   当前页码
     * @param pageSize  每页大小
     * @param <T>       数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> list, long total, int pageNum, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int) Math.ceil((double) total / pageSize));
        return result;
    }

    /**
     * 从 MyBatis-Plus IPage 转换
     *
     * @param page MyBatis-Plus 分页对象
     * @param <T>  数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        return of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return CommonConstant.SUCCESS == 0;
    }
}

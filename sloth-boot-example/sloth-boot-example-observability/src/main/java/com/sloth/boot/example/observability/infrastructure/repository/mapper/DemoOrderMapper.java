package com.sloth.boot.example.observability.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sloth.boot.example.observability.infrastructure.model.po.order.DemoOrder;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单数据访问层。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface DemoOrderMapper extends BaseMapperX<DemoOrder> {

    /**
     * 分页查询订单，按 create_time 倒序，可选按 status 过滤。
     *
     * @param page   分页对象
     * @param status 订单状态（可为 null）
     * @return 分页结果
     */
    IPage<DemoOrder> selectPageOrder(IPage<DemoOrder> page, @Param("status") String status);

    /**
     * 演示级联关联查询：订单 + 用户 + 商品名称（XML 多表 join）。
     *
     * @param userId    用户 ID（可为 null）
     * @param productId 商品 ID（可为 null）
     * @return 订单列表
     */
    List<DemoOrder> selectByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}

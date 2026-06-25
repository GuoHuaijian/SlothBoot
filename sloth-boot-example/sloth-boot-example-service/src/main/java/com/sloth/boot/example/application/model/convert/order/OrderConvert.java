package com.sloth.boot.example.application.model.convert.order;

import com.sloth.boot.example.application.model.dto.order.OrderDTO;
import com.sloth.boot.example.application.model.form.order.OrderCreateForm;
import com.sloth.boot.example.application.model.vo.order.OrderVO;
import com.sloth.boot.example.infrastructure.model.po.order.DemoOrder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 订单对象转换器（MapStruct）。
 * <p>
 * 负责订单相关对象之间的转换，包括：
 * - 表单对象 → 实体对象
 * - 实体对象 → 视图对象
 * - 实体对象 → 数据传输对象
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderConvert {

    /**
     * 将订单创建表单转换为实体对象。
     *
     * @param form 订单创建表单
     * @return 订单实体
     */
    DemoOrder toEntity(OrderCreateForm form);

    /**
     * 将订单实体转换为视图对象。
     *
     * @param entity 订单实体
     * @return 订单视图对象
     */
    OrderVO toVO(DemoOrder entity);

    /**
     * 将订单实体列表转换为视图对象列表。
     *
     * @param entities 订单实体列表
     * @return 订单视图对象列表
     */
    List<OrderVO> toVOList(List<DemoOrder> entities);

    /**
     * 将订单实体转换为数据传输对象。
     *
     * @param entity 订单实体
     * @return 订单数据传输对象
     */
    OrderDTO toDTO(DemoOrder entity);
}

package com.sloth.boot.example.observability.application.model.convert;

import com.sloth.boot.example.observability.application.model.enums.order.OrderStatus;
import com.sloth.boot.example.observability.application.model.form.order.PlaceOrderForm;
import com.sloth.boot.example.observability.application.model.vo.OrderVO;
import com.sloth.boot.example.observability.application.model.vo.PlaceOrderResultVO;
import com.sloth.boot.example.observability.application.model.vo.ProductVO;
import com.sloth.boot.example.observability.application.model.vo.UserVO;
import com.sloth.boot.example.observability.infrastructure.model.po.order.DemoOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单对象转换器（MapStruct）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderConvert {

    /**
     * 表单 -> 实体。仅映射 productId/quantity；userId/productName/amount/status/orderNo
     * 由命令层显式设置，避免 MapStruct 误填或漏填。
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNo", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    DemoOrder toEntity(PlaceOrderForm form);

    /**
     * 实体 -> VO。status 枚举按 code 输出。
     */
    @Mapping(target = "status", expression = "java(entity.getStatus() == null ? null : entity.getStatus().getCode())")
    OrderVO toVO(DemoOrder entity);

    List<OrderVO> toVOList(List<DemoOrder> entities);

    /**
     * 组装级联下单结果。
     */
    default PlaceOrderResultVO composePlaceOrder(Long orderId, UserVO user, ProductVO product,
                                                  Integer quantity, BigDecimal amount, OrderStatus status) {
        PlaceOrderResultVO vo = new PlaceOrderResultVO();
        vo.setOrderId(orderId);
        vo.setUser(user);
        vo.setProduct(product);
        vo.setQuantity(quantity);
        vo.setAmount(amount);
        vo.setStatus(status == null ? null : status.getCode());
        return vo;
    }
}

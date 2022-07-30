package com.shalhlad.product_delivery_service.mapper;

import com.shalhlad.product_delivery_service.dto.response.OrderDetailsDto;
import com.shalhlad.product_delivery_service.entity.order.Order;
import com.shalhlad.product_delivery_service.entity.order.OrderedProductDetails;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {UserMapper.class, DepartmentMapper.class},
    imports = {BigDecimal.class, OrderedProductDetails.class})
public interface OrderMapper {

  @Mapping(target = "price",
      expression = "java(order.getOrderedProducts().values().stream().map(OrderedProductDetails::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add))")
  OrderDetailsDto toDetailsDto(Order order);

  Iterable<OrderDetailsDto> toDetailsDto(Iterable<Order> orders);
}

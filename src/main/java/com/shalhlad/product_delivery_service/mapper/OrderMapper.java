package com.shalhlad.product_delivery_service.mapper;

import com.shalhlad.product_delivery_service.dto.response.OrderDetailsDto;
import com.shalhlad.product_delivery_service.dto.response.ProductWithQuantityDto;
import com.shalhlad.product_delivery_service.entity.order.Order;
import com.shalhlad.product_delivery_service.entity.order.OrderedProductDetails;
import com.shalhlad.product_delivery_service.entity.product.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(uses = {UserMapper.class, DepartmentMapper.class})
public interface OrderMapper {

  @Mappings({
      @Mapping(target = "orderPrice", expression = "java(calculateOrderPrice(order))"),
      @Mapping(target = "orderedProducts", qualifiedByName = "orderedProductsMapToList")
  })
  OrderDetailsDto toDetailsDto(Order order);

  Iterable<OrderDetailsDto> toDetailsDto(Iterable<Order> orders);

  default BigDecimal calculateOrderPrice(Order order) {
    return order.getOrderedProducts().values().stream()
        .map(OrderedProductDetails::getPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Named("orderedProductsMapToList")
  default List<ProductWithQuantityDto> orderedProductsMapToList(
      Map<Product, OrderedProductDetails> orderedProducts) {
    return orderedProducts.entrySet().stream()
        .map(e -> {
          Product product = e.getKey();
          OrderedProductDetails productDetails = e.getValue();
          return new ProductWithQuantityDto(
              product.getId(),
              product.getName(),
              product.getCategory(),
              productDetails.getPrice(),
              productDetails.getQuantity()
          );
        })
        .toList();
  }
}

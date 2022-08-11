package com.shalhlad.product_delivery_service.mapper;

import com.shalhlad.product_delivery_service.dto.response.OrderDetailsDto;
import com.shalhlad.product_delivery_service.dto.response.ProductWithQuantityAndDiscountDto;
import com.shalhlad.product_delivery_service.entity.order.Order;
import com.shalhlad.product_delivery_service.entity.order.OrderedProductDetails;
import com.shalhlad.product_delivery_service.entity.product.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(uses = {UserMapper.class, DepartmentMapper.class})
public interface OrderMapper {

  @Mappings({
      @Mapping(target = "orderPrice", expression = "java(calculateOrderPrice(order))"),
      @Mapping(target = "orderedProducts", qualifiedByName = "orderedProductsMapToList")
  })
  OrderDetailsDto toDetailsDto(Order order);

  List<OrderDetailsDto> toDetailsDto(Iterable<Order> orders);

  default Page<OrderDetailsDto> toDetailsDto(Page<Order> orders) {
    List<OrderDetailsDto> mappedContent = toDetailsDto(orders.getContent());
    return new PageImpl<>(mappedContent, orders.getPageable(), orders.getTotalElements());
  }

  default BigDecimal calculateOrderPrice(Order order) {
    return order.getOrderedProducts().values().stream()
        .map(od -> {
          BigDecimal priceOfOne = od.getPriceOfOne();
          Integer quantity = od.getQuantity();

          BigDecimal discountMultiplier = BigDecimal.valueOf(100)
              .subtract(od.getDiscountInPercents())
              .divide(BigDecimal.valueOf(100), 3, RoundingMode.FLOOR);

          return priceOfOne
              .multiply(BigDecimal.valueOf(quantity))
              .multiply(discountMultiplier)
              .setScale(2, RoundingMode.FLOOR);
        })
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Named("orderedProductsMapToList")
  default List<ProductWithQuantityAndDiscountDto> orderedProductsMapToList(
      Map<Product, OrderedProductDetails> orderedProducts) {
    return orderedProducts.entrySet().stream()
        .map(e -> {
          Product product = e.getKey();
          OrderedProductDetails productDetails = e.getValue();
          return new ProductWithQuantityAndDiscountDto(
              product.getId(),
              product.getName(),
              product.getCategory(),
              productDetails.getPriceOfOne(),
              productDetails.getQuantity(),
              productDetails.getDiscountInPercents()
          );
        })
        .toList();
  }
}

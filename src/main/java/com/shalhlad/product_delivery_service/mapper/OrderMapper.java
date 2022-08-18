package com.shalhlad.product_delivery_service.mapper;

import com.shalhlad.product_delivery_service.dto.response.OrderDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.ProductWithQuantityAndDiscountResponse;
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
public abstract class OrderMapper {

  @Mappings({
      @Mapping(target = "orderPrice", expression = "java(calculateOrderPrice(order))"),
      @Mapping(target = "orderedProducts", qualifiedByName = "orderedProductsMapToList"),
      @Mapping(target = "nextStage", expression = "java(calculateNextStage(order))")
  })
  public abstract OrderDetailsResponse toDetailsResponse(Order order);

  public abstract List<OrderDetailsResponse> toDetailsResponse(Iterable<Order> orders);

  public Page<OrderDetailsResponse> toDetailsResponse(Page<Order> orders) {
    List<OrderDetailsResponse> mappedContent = toDetailsResponse(orders.getContent());
    return new PageImpl<>(mappedContent, orders.getPageable(), orders.getTotalElements());
  }


  protected String calculateNextStage(Order order) {
    try {
      return order.getStage().next().toString();
    } catch (UnsupportedOperationException e) {
      return "-";
    }
  }

  protected BigDecimal calculateOrderPrice(Order order) {
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
  protected List<ProductWithQuantityAndDiscountResponse> orderedProductsMapToList(
      Map<Product, OrderedProductDetails> orderedProducts) {
    return orderedProducts.entrySet().stream()
        .map(e -> {
          Product product = e.getKey();
          OrderedProductDetails productDetails = e.getValue();
          return new ProductWithQuantityAndDiscountResponse(
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

package com.shalhlad.productdeliveryservice.mapper;

import com.shalhlad.productdeliveryservice.dto.response.OrderDetailsResponse;
import com.shalhlad.productdeliveryservice.dto.response.ProductWithQuantityAndDiscountResponse;
import com.shalhlad.productdeliveryservice.entity.order.Order;
import com.shalhlad.productdeliveryservice.entity.order.OrderedProductDetails;
import com.shalhlad.productdeliveryservice.entity.product.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

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
    return orders.map(this::toDetailsResponse);
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
              product.getWeight(),
              productDetails.getDiscountInPercents()
          );
        })
        .toList();
  }
}

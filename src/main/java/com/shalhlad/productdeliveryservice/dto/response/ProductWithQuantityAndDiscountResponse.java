package com.shalhlad.productdeliveryservice.dto.response;

import com.shalhlad.productdeliveryservice.entity.product.Category;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductWithQuantityAndDiscountResponse extends ProductWithQuantityResponse {

  private BigDecimal discountInPercents;

  public ProductWithQuantityAndDiscountResponse(Long id, String name,
      Category category, BigDecimal price,
      Integer weight, Integer quantity, BigDecimal discountInPercents) {
    super(id, name, category, price, weight, quantity);
    this.discountInPercents = discountInPercents;
  }
}

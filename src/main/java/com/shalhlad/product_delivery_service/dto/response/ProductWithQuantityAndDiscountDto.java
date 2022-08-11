package com.shalhlad.product_delivery_service.dto.response;

import com.shalhlad.product_delivery_service.entity.product.Category;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductWithQuantityAndDiscountDto extends ProductWithQuantityDto {

  private BigDecimal discountInPercents;

  public ProductWithQuantityAndDiscountDto(Long id, String name,
      Category category, BigDecimal price,
      Integer quantity, BigDecimal discountInPercents) {
    super(id, name, category, price, quantity);
    this.discountInPercents = discountInPercents;
  }
}

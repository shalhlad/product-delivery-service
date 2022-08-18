package com.shalhlad.product_delivery_service.dto.response;

import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.entity.product.Product;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductWithQuantityResponse {

  private Long id;
  private String name;
  private Category category;
  private BigDecimal price;
  private Integer quantity;

  public ProductWithQuantityResponse(Product product, Integer quantity) {
    this(product.getId(), product.getName(), product.getCategory(), product.getPrice(), quantity);
  }
}

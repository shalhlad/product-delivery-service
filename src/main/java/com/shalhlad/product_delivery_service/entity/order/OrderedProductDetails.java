package com.shalhlad.product_delivery_service.entity.order;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Embeddable
public class OrderedProductDetails {

  @Column(precision = 5, scale = 2, nullable = false)
  private BigDecimal price;

  @Column(nullable = false)
  private Integer quantity;

}

package com.shalhlad.productdeliveryservice.entity.order;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Embeddable
public class OrderedProductDetails {

  @Column(name = "price_of_one", precision = 5, scale = 2, nullable = false)
  private BigDecimal priceOfOne;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "discount_in_percents", precision = 2, scale = 1, nullable = false)
  private BigDecimal discountInPercents;

}

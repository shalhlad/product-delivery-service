package com.shalhlad.product_delivery_service.dto.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CardResponse {

  private Integer numberOfOrders;
  private BigDecimal discountInPercents;
}

package com.shalhlad.productdeliveryservice.dto.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CardResponse {

  private Integer numberOfOrders;
  private BigDecimal discountInPercents;
}

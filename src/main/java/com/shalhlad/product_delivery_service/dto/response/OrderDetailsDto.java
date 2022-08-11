package com.shalhlad.product_delivery_service.dto.response;

import com.shalhlad.product_delivery_service.entity.order.Stage;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class OrderDetailsDto {

  private Long id;
  private DepartmentDetailsDto department;
  private String deliveryAddress;
  private List<ProductWithQuantityAndDiscountDto> orderedProducts;
  private BigDecimal orderPrice;
  private UserDetailsDto user;
  private String stage;
  private Map<Stage, Date> stageHistory;
}

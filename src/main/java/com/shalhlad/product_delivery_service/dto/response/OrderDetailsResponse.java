package com.shalhlad.product_delivery_service.dto.response;

import com.shalhlad.product_delivery_service.entity.order.Stage;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class OrderDetailsResponse {

  private Long id;
  private DepartmentDetailsResponse department;
  private String deliveryAddress;
  private List<ProductWithQuantityAndDiscountResponse> orderedProducts;
  private BigDecimal orderPrice;
  private UserDetailsResponse user;
  private String stage;
  private String nextStage;
  private Map<Stage, Date> stageHistory;
}

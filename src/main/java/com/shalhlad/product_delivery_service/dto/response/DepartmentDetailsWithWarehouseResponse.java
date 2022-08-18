package com.shalhlad.product_delivery_service.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class DepartmentDetailsWithWarehouseResponse {

  private Long id;
  private String address;
  private List<ProductWithQuantityResponse> productWarehouse;
}

package com.shalhlad.productdeliveryservice.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class DepartmentDetailsWithWarehouseResponse {

  private Long id;
  private String address;
  private List<ProductWithQuantityResponse> productWarehouse;
}

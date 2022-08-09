package com.shalhlad.product_delivery_service.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class DepartmentDetailsWithWarehouseDto {

  private Long id;
  private String address;
  private List<ProductWithQuantityDto> productWarehouse;
}

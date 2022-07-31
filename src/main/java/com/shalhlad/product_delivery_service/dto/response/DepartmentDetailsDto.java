package com.shalhlad.product_delivery_service.dto.response;

import com.shalhlad.product_delivery_service.entity.product.Product;
import java.util.Map;
import lombok.Data;

@Data
public class DepartmentDetailsDto {

  private Long id;
  private String address;
  private Map<Product, Integer> productWarehouse;
}

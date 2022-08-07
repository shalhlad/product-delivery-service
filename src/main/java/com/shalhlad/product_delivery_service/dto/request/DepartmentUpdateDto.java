package com.shalhlad.product_delivery_service.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class DepartmentUpdateDto {

  @Length(min = 2, max = 50, message = "Address should be between 2 and 50 characters")
  private String address;
}

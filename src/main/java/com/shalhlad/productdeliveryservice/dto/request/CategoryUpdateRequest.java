package com.shalhlad.productdeliveryservice.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CategoryUpdateRequest {

  @Length(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
  private String name;
}

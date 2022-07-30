package com.shalhlad.product_delivery_service.dto.request;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CategoryCreationDto {

  @NotNull(message = "Name should not be null")
  @Length(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
  private String name;
}

package com.shalhlad.product_delivery_service.dto.request;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ProductCreateRequest {

  @NotNull(message = "Name should not be null")
  @Length(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
  private String name;

  @NotNull(message = "Price should not be null")
  @DecimalMin(value = "0.01", message = "Price should be minimum 0.01")
  @Digits(integer = 5, fraction = 2, message = "Price should contain max 5 number at left of point and 2 at right")
  private BigDecimal price;

  @NotNull(message = "CategoryId should not be null")
  @Min(value = 0, message = "CategoryId should be minimum 0")
  private Long categoryId;
}

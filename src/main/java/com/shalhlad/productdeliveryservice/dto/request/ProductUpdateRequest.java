package com.shalhlad.productdeliveryservice.dto.request;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ProductUpdateRequest {

  @Length(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
  private String name;

  @DecimalMin(value = "0.01", message = "Price should be minimum 0.01")
  @Digits(integer = 5, fraction = 2, message = "Price should contain max 5 number at left of point and 2 at right")
  private BigDecimal price;

  @Min(value = 0, message = "CategoryId should be minimum 0")
  private Long categoryId;

  @Min(value = 1, message = "Weight should be minimum 1 gram")
  private Integer weight;
}

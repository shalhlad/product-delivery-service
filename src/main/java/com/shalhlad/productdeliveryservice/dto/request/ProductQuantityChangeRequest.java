package com.shalhlad.productdeliveryservice.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProductQuantityChangeRequest {

  @NotNull(message = "Action should not be null")
  @Pattern(regexp = "(reduce)|(increase)", message = "Available actions are ['increase', 'reduce']")
  private String action;

  @NotNull(message = "ProductId should not be null")
  @Min(value = 0, message = "ProductId should not be minimum 0")
  private Long productId;

  @NotNull(message = "NumberOfProducts should not be null")
  @Min(value = 1, message = "NumberOfProducts should be minimum 1")
  private Integer numberOfProducts;
}

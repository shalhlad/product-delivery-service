package com.shalhlad.product_delivery_service.dto.request;

import java.util.Map;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class OrderCreationDto {

  @NotNull(message = "DepartmentId should not be null")
  @Min(value = 0, message = "DepartmentId should be minimum 0")
  private Long departmentId;

  @NotNull(message = "OrderedProducts should not be null")
  @NotEmpty(message = "OrderedProducts should not be empty")
  private Map<Long, Integer> orderedProducts;

  @NotNull(message = "DeliveryAddress should not be null")
  @Length(min = 2, max = 50, message = "DeliveryAddress should be between 2 and 50 characters")
  private String deliveryAddress;

}

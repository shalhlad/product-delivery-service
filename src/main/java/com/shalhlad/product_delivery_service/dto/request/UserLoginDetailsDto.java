package com.shalhlad.product_delivery_service.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginDetailsDto {

  @NotNull(message = "Email should not be null")
  @Email
  private String email;

  @NotNull(message = "Password should not be null")
  private String password;
}

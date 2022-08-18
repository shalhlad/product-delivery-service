package com.shalhlad.product_delivery_service.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class SignUpRequest {

  @NotNull(message = "Email should not be null")
  @Email
  private String email;

  @NotNull(message = "FirstName should not be null")
  @Length(min = 2, max = 50, message = "FirstName should be between 2 and 50 characters long")
  private String firstName;

  @NotNull(message = "Password should not be null")
  @Length(min = 5, max = 50, message = "Password should be between 5 and 50 characters")
  private String password;
}

package com.shalhlad.product_delivery_service.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserDetailsUpdateDto {

  @Length(min = 2, max = 50, message = "FirstName should be between 2 and 50 characters long")
  private String firstName;

  @Length(min = 2, max = 50, message = "LastName should be between 2 and 50 characters long")
  private String lastName;

  @Length(min = 2, max = 50, message = "PatronymicName should be between 2 and 50 characters long")
  private String patronymic;

}

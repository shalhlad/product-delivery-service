package com.shalhlad.product_delivery_service.dto.response;

import com.shalhlad.product_delivery_service.entity.user.Role;
import java.util.Date;
import lombok.Data;

@Data
public class UserDetailsDto {

  private String userId;
  private String firstName;
  private String lastName;
  private String patronymic;
  private String email;
  private Date registrationDate;
  private Role role;
}

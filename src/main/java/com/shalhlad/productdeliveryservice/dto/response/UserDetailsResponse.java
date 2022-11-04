package com.shalhlad.productdeliveryservice.dto.response;

import com.shalhlad.productdeliveryservice.entity.user.Role;
import java.util.Date;
import lombok.Data;

@Data
public class UserDetailsResponse {

  private String userId;
  private String firstName;
  private String lastName;
  private String patronymic;
  private String email;
  private Date registrationDate;
  private CardResponse card;
  private Role role;
}

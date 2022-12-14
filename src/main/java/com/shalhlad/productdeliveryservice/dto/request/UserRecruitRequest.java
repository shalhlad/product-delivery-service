package com.shalhlad.productdeliveryservice.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRecruitRequest {

  @NotNull(message = "UserId should not be null")
  @NotEmpty(message = "UserId should not be empty")
  private String userId;

  @NotNull(message = "Role should not be null")
  private EmployeeRoles role;

  @NotNull(message = "DepartmentId should not be null")
  @Min(value = 0, message = "DepartmentId should not be negative")
  private Long departmentId;
}

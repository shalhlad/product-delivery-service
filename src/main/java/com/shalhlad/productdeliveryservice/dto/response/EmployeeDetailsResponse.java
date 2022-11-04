package com.shalhlad.productdeliveryservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDetailsResponse extends UserDetailsResponse {

  private DepartmentDetailsResponse department;
}

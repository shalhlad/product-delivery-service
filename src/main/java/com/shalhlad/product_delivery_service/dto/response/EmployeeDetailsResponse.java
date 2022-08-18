package com.shalhlad.product_delivery_service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDetailsResponse extends UserDetailsResponse {

  private DepartmentDetailsResponse department;
}

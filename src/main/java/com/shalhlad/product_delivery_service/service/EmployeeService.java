package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.EmployeeRoles;
import com.shalhlad.product_delivery_service.dto.request.UserRecruitRequest;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import java.security.Principal;

public interface EmployeeService {

  EmployeeDetailsResponse findEmployeeByUserId(Principal principal, String userId);

  EmployeeDetailsResponse recruitUser(Principal principal, UserRecruitRequest userRecruitRequest);

  UserDetailsResponse fireEmployeeByUserId(Principal principal, String userId);

  EmployeeDetailsResponse changeRoleOfEmployee(Principal principal, String userId,
      EmployeeRoles newRole);

}

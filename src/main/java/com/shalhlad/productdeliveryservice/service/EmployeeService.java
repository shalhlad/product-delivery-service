package com.shalhlad.productdeliveryservice.service;

import com.shalhlad.productdeliveryservice.dto.request.EmployeeRoles;
import com.shalhlad.productdeliveryservice.dto.request.UserRecruitRequest;
import com.shalhlad.productdeliveryservice.dto.response.EmployeeDetailsResponse;
import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;
import java.security.Principal;

public interface EmployeeService {

  EmployeeDetailsResponse findEmployeeByUserId(Principal principal, String userId);

  EmployeeDetailsResponse recruitUser(Principal principal, UserRecruitRequest userRecruitRequest);

  UserDetailsResponse fireEmployeeByUserId(Principal principal, String userId);

  EmployeeDetailsResponse changeRoleOfEmployee(Principal principal, String userId,
      EmployeeRoles newRole);

}

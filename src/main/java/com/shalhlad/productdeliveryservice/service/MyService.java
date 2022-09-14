package com.shalhlad.productdeliveryservice.service;

import com.shalhlad.productdeliveryservice.dto.request.EmployeeRoles;
import com.shalhlad.productdeliveryservice.dto.request.ProcessingOrderCharacteristics;
import com.shalhlad.productdeliveryservice.dto.request.UserDetailsUpdateRequest;
import com.shalhlad.productdeliveryservice.dto.response.DepartmentDetailsResponse;
import com.shalhlad.productdeliveryservice.dto.response.EmployeeDetailsResponse;
import com.shalhlad.productdeliveryservice.dto.response.OrderDetailsResponse;
import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MyService {

  UserDetailsResponse getDetailsOfAuthorizedUser(Principal principal);

  UserDetailsResponse updateDetailsOfAuthorizedUser(Principal principal,
      UserDetailsUpdateRequest userDetailsUpdateRequest);

  DepartmentDetailsResponse getDepartmentOfAuthorizedUser(Principal principal);

  Page<OrderDetailsResponse> getOrdersOfAuthorizedUserDepartment(Principal principal,
      ProcessingOrderCharacteristics processingOrderCharacteristics, Pageable pageable);

  Page<OrderDetailsResponse> getOrdersOfAuthorizedUser(Principal principal, boolean active,
      Pageable pageable);

  OrderDetailsResponse getOrderOfAuthorizedUserByOrderId(Principal principal, Long orderId);

  Iterable<EmployeeDetailsResponse> getEmployeesOfAuthorizedUserDepartment(Principal principal,
      EmployeeRoles employeeRole);
}

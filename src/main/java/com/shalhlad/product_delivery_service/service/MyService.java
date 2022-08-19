package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.EmployeeRoles;
import com.shalhlad.product_delivery_service.dto.request.ProcessingOrderCharacteristics;
import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateRequest;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.OrderDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
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

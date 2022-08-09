package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.UserRecruitRequestDto;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import java.security.Principal;

public interface EmployeeService {

  Iterable<Employee> findAllEmployeesOfAuthorizationDepartment(Principal principal);

  Employee findEmployeeOfCurrentDepartmentByUserId(Principal principal, String userId);

  Employee recruit(Principal principal, UserRecruitRequestDto userRecruitRequestDto);

  User fire(Principal principal, String userId);

  Employee changePosition(Principal principal, String userId, Role role);

  Iterable<Employee> findAllEmployeesByDepartmentId(Principal principal, Long departmentId);
}

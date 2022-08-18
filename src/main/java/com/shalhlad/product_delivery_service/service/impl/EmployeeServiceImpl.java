package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.EmployeeRoles;
import com.shalhlad.product_delivery_service.dto.request.UserRecruitRequest;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.exception.NoRightsException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.EmployeeMapper;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.repository.DepartmentRepository;
import com.shalhlad.product_delivery_service.repository.EmployeeRepository;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.EmployeeService;
import java.security.Principal;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;
  private final UserMapper userMapper;

  @Override
  public EmployeeDetailsResponse findEmployeeByUserId(Principal principal, String userId) {
    return employeeRepository.findByUserId(userId)
        .map(employeeMapper::toDetailsResponse)
        .orElseThrow(() -> new NotFoundException("Employee not found by userId: " + userId));
  }

  @Override
  public EmployeeDetailsResponse recruitUser(Principal principal,
      UserRecruitRequest userRecruitRequest) {
    Department department = departmentRepository.findById(userRecruitRequest.getDepartmentId())
        .orElseThrow(() -> new NotFoundException(
            "Department not found with id: " + userRecruitRequest.getDepartmentId()));

    User principalUser = userRepository.findByEmail(principal.getName()).orElseThrow();
    switch (principalUser.getRole()) {
      case ROLE_ADMIN -> {
        if (userRecruitRequest.getRole() != EmployeeRoles.DEPARTMENT_HEAD) {
          throw new NoRightsException("Admin can only recruit department head");
        }
      }
      case ROLE_DEPARTMENT_HEAD -> {
        if (userRecruitRequest.getRole() != EmployeeRoles.WAREHOUSEMAN &&
            userRecruitRequest.getRole() != EmployeeRoles.COLLECTOR &&
            userRecruitRequest.getRole() != EmployeeRoles.COURIER) {
          throw new NoRightsException(
              "Department head can only recruit warehouseman, collector or courier");
        }
        Employee departmentHead = employeeRepository.findById(principalUser.getId())
            .orElseThrow();
        if (!departmentHead.getDepartment().equals(department)) {
          throw new NoRightsException(
              "The head of the department is not the head of the department in which the employee need to recruit");
        }
      }
      default -> throw new NoRightsException("Only admin or department head can recruit employees");
    }

    User targetUser = userRepository.findByUserId(userRecruitRequest.getUserId())
        .orElseThrow(() -> new NotFoundException(
            "User not found with userId: " + userRecruitRequest.getUserId()));
    if (targetUser.getRole() != Role.ROLE_CUSTOMER) {
      throw new NoRightsException("Only customer can become employee");
    }

    Employee recruited = userMapper.toEmployee(targetUser);
    recruited.setDepartment(department);
    recruited.setRole(userRecruitRequest.getRole().toEntityRole());

    userRepository.delete(targetUser);

    Employee saved = employeeRepository.save(recruited);
    return employeeMapper.toDetailsResponse(saved);
  }

  @Override
  public UserDetailsResponse fireEmployeeByUserId(Principal principal, String userId) {
    User principalUser = userRepository.findByEmail(principal.getName()).orElseThrow();

    Employee target = employeeRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("Employee not found with userId: " + userId));

    switch (principalUser.getRole()) {
      case ROLE_ADMIN -> {
        if (target.getRole() != Role.ROLE_DEPARTMENT_HEAD) {
          throw new NoRightsException("Admin can only fire department head");
        }
      }
      case ROLE_DEPARTMENT_HEAD -> {
        if (target.getRole() != Role.ROLE_WAREHOUSEMAN &&
            target.getRole() != Role.ROLE_COLLECTOR &&
            target.getRole() != Role.ROLE_COURIER) {
          throw new NoRightsException(
              "Department head can only fire warehouseman, collector or courier");
        }
        Employee departmentHead = employeeRepository.findById(principalUser.getId())
            .orElseThrow();
        if (!departmentHead.getDepartment().equals(target.getDepartment())) {
          throw new NoRightsException(
              "Target employee doest not work in department where the current department head works");
        }
      }
      default -> throw new NoRightsException("Only admin or department head can fire employees");
    }

    User user = employeeMapper.toUser(target);
    user.setRole(Role.ROLE_CUSTOMER);
    employeeRepository.delete(target);

    User saved = userRepository.save(user);
    return userMapper.toDetailsResponse(saved);
  }

  @Override
  public EmployeeDetailsResponse changeRoleOfEmployee(Principal principal, String userId,
      EmployeeRoles role) {
    if (role != EmployeeRoles.COURIER &&
        role != EmployeeRoles.COLLECTOR &&
        role != EmployeeRoles.WAREHOUSEMAN) {
      throw new InvalidValueException(
          "Roles to change are ['COURIER', 'COLLECTOR', 'WAREHOUSEMAN']");
    }

    Employee departmentHead = employeeRepository.findByEmail(principal.getName()).orElseThrow();
    Employee target = employeeRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("Employee not found with userId: " + userId));
    if (!departmentHead.getDepartment().equals(target.getDepartment())) {
      throw new NoRightsException(
          "Target employee doest not work in department where the current manager works");
    }
    if (target.getRole() == Role.ROLE_DEPARTMENT_HEAD) {
      throw new NoRightsException("Department head cannot change role of another department head");
    }

    target.setRole(role.toEntityRole());

    Employee saved = employeeRepository.save(target);
    return employeeMapper.toDetailsResponse(saved);
  }


}

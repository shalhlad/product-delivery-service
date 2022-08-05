package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.UserRecruitRequestDto;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;
  private final UserMapper userMapper;

  @Autowired
  public EmployeeServiceImpl(UserRepository userRepository,
      DepartmentRepository departmentRepository, EmployeeRepository employeeRepository,
      EmployeeMapper employeeMapper, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.departmentRepository = departmentRepository;
    this.employeeRepository = employeeRepository;
    this.employeeMapper = employeeMapper;
    this.userMapper = userMapper;
  }

  @Override
  public Iterable<Employee> findAllEmployeesOfDepartment(Principal principal) {
    Employee manager = employeeRepository.findByEmail(principal.getName()).orElseThrow();
    return employeeRepository.findAllByDepartment(manager.getDepartment());
  }

  @Override
  public Employee findEmployeeOfDepartmentByUserId(Principal principal, String userId) {
    Employee manager = employeeRepository.findByEmail(principal.getName()).orElseThrow();
    return employeeRepository.findByDepartmentAndUserId(manager.getDepartment(), userId)
        .orElseThrow(() -> new NotFoundException(
            "Employee not found with current department and userId: " + userId));
  }

  @Override
  public Employee recruit(Principal principal, UserRecruitRequestDto userRecruitRequestDto) {
    Department department = departmentRepository.findById(userRecruitRequestDto.getDepartmentId())
        .orElseThrow(() -> new NotFoundException(
            "Department not found with id: " + userRecruitRequestDto.getDepartmentId()));

    User principalUser = userRepository.findByEmail(principal.getName()).orElseThrow();
    switch (principalUser.getRole()) {
      case ROLE_ADMIN -> {
        if (userRecruitRequestDto.getRole() != Role.ROLE_DEPARTMENT_HEAD) {
          throw new NoRightsException("Admin can only recruit department head");
        }
      }
      case ROLE_DEPARTMENT_HEAD -> {
        if (principalUser.getRole() != Role.ROLE_WAREHOUSEMAN &&
            principalUser.getRole() != Role.ROLE_COLLECTOR &&
            principalUser.getRole() != Role.ROLE_COURIER) {
          throw new NoRightsException(
              "Department head can only recruit warehouseman, collector or courier");
        }
        if (!employeeRepository.findByUserId(principalUser.getUserId()).orElseThrow()
            .getDepartment()
            .equals(department)) {
          throw new NoRightsException(
              "The head of the department is not the head of the department in which the employee need to recruit");
        }
      }
      default -> throw new NoRightsException("Only admin or department head can recruit employees");
    }

    User targetUser = userRepository.findByUserId(userRecruitRequestDto.getUserId())
        .orElseThrow(() -> new NotFoundException(
            "User not found with userId: " + userRecruitRequestDto.getUserId()));

    Employee recruited = userMapper.toEmployee(targetUser);
    recruited.setDepartment(department);
    recruited.setRole(userRecruitRequestDto.getRole());

    userRepository.delete(targetUser);
    return employeeRepository.save(recruited);
  }

  @Override
  public User fire(Principal principal, String userId) {
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
        if (!employeeRepository.findByUserId(principalUser.getUserId()).orElseThrow()
            .getDepartment().equals(target.getDepartment())) {
          throw new NoRightsException(
              "Target employee doest not work in department where the current department head works");
        }
      }
      default -> throw new NoRightsException("Only admin or department head can fire employees");
    }

    User user = employeeMapper.toUser(target);
    employeeRepository.delete(target);
    return userRepository.save(user);
  }

  @Override
  public Employee changePosition(Principal principal, String userId, Role role) {
    Employee manager = employeeRepository.findByEmail(principal.getName()).orElseThrow();
    Employee target = employeeRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("Employee not found with userId: " + userId));

    if (!manager.getDepartment().equals(target.getDepartment())) {
      throw new NoRightsException(
          "Target employee doest not work in department where the current manager works");
    }
    target.setRole(role);
    return employeeRepository.save(target);
  }
}

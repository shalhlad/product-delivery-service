package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.UserRecruitRequestDto;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsDto;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsDto;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.mapper.EmployeeMapper;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.service.EmployeeService;
import com.shalhlad.product_delivery_service.util.Utils;
import java.security.Principal;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

  private final EmployeeService service;
  private final EmployeeMapper employeeMapper;
  private final UserMapper userMapper;

  @Autowired
  public EmployeeController(EmployeeService service, EmployeeMapper employeeMapper,
      UserMapper userMapper) {
    this.service = service;
    this.employeeMapper = employeeMapper;
    this.userMapper = userMapper;
  }

  @GetMapping
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  public Iterable<EmployeeDetailsDto> getEmployeesOfCurrentDepartment(Principal principal) {
    return employeeMapper.toDetailsDto(service.findAllEmployeesOfDepartment(principal));
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  public EmployeeDetailsDto getEmployeeOfCurrentDepartmentByUserId(
      Principal principal,
      @PathVariable String userId) {
    return employeeMapper.toDetailsDto(service.findEmployeeOfDepartmentByUserId(principal, userId));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  public EmployeeDetailsDto recruitUser(
      Principal principal,
      @RequestBody @Valid UserRecruitRequestDto userRecruitRequestDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return employeeMapper.toDetailsDto(service.recruit(principal, userRecruitRequestDto));
  }

  @PatchMapping("/{userId}")
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  public EmployeeDetailsDto changeEmployeePosition(
      Principal principal,
      @PathVariable String userId,
      @RequestParam Role position) {
    return employeeMapper.toDetailsDto(service.changePosition(principal, userId, position));
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasAnyRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  public UserDetailsDto fireEmployee(Principal principal, @PathVariable String userId) {
    return userMapper.toUserDetailsDto(service.fire(principal, userId));
  }


}

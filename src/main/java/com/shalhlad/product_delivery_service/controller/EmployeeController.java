package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.UserRecruitRequestDto;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsDto;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsDto;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.mapper.EmployeeMapper;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.service.EmployeeService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
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

  @GetMapping("/departments/me/employees")
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  @ApiOperation(value = "getEmployeesOfAuthorizedEmployeeDepartment",
      notes = "Returns all employees of authorized employee department")
  public Iterable<EmployeeDetailsDto> getEmployeesOfCurrentDepartment(
      @ApiIgnore Principal principal) {
    return employeeMapper.toDetailsDto(
        service.findAllEmployeesOfAuthorizationDepartment(principal));
  }

  @GetMapping("/departments/{departmentId}/employees")
  @PreAuthorize("hasAnyRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  @ApiOperation(value = "getEmployeesOfDepartmentByDepartmentId",
      notes = "Returns all employees of department by department id")
  public Iterable<EmployeeDetailsDto> getEmployeesOfDepartmentByDepartmentId(
      @ApiIgnore Principal principal,
      @PathVariable Long departmentId) {
    return employeeMapper.toDetailsDto(
        service.findAllEmployeesByDepartmentId(principal, departmentId));
  }

  @GetMapping("/employees//{userId}")
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  @ApiOperation(value = "getEmployeeOfAuthorizedEmployeeDepartmentByEmployeeId",
      notes = "Returns employee of authorized employee department by employee id")
  public EmployeeDetailsDto getEmployeeOfCurrentDepartmentByUserId(
      @ApiIgnore Principal principal,
      @PathVariable String userId) {
    return employeeMapper.toDetailsDto(
        service.findEmployeeOfCurrentDepartmentByUserId(principal, userId));
  }

  @PostMapping("/employees")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  @ApiOperation(value = "recruitUser",
      notes = "Makes customer the employee of authorized employee department")
  public EmployeeDetailsDto recruitUser(
      @ApiIgnore Principal principal,
      @RequestBody @Valid UserRecruitRequestDto userRecruitRequestDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return employeeMapper.toDetailsDto(service.recruit(principal, userRecruitRequestDto));
  }

  @PatchMapping("/employees/{userId}")
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  @ApiOperation(value = "changeEmployeePosition",
      notes = "Change employee position of authorized employee department")
  public EmployeeDetailsDto changeEmployeePosition(
      @ApiIgnore Principal principal,
      @PathVariable String userId,
      @RequestParam Role position) {
    return employeeMapper.toDetailsDto(service.changePosition(principal, userId, position));
  }

  @DeleteMapping("/employees/{userId}")
  @PreAuthorize("hasAnyRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  @ApiOperation(value = "fireEmployeeByUserId",
      notes = "Fire employee of authorized employee department by userId")
  public UserDetailsDto fireEmployee(@ApiIgnore Principal principal, @PathVariable String userId) {
    return userMapper.toDetailsDto(service.fire(principal, userId));
  }


}

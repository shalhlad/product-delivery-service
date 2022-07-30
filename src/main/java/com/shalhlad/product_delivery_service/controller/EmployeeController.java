package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.UserRecruitRequestDto;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
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

  @Autowired
  public EmployeeController(EmployeeService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  public Iterable<Employee> getEmployeesOfCurrentDepartment(Principal principal) {
    return service.getEmployeesOfDepartment(principal);
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  public Employee getEmployeeOfCurrentDepartmentByUserId(
      Principal principal,
      @PathVariable String userId) {
    return service.getEmployeeOfDepartmentByUserId(principal, userId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  public Employee recruitUser(
      Principal principal,
      @RequestBody @Valid UserRecruitRequestDto userRecruitRequestDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.recruit(principal, userRecruitRequestDto);
  }

  @PatchMapping("/{userId}")
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  public Employee changeEmployeePosition(
      Principal principal,
      @PathVariable String userId,
      @RequestParam Role position) {
    return service.changePosition(principal, userId, position);
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  public User fireEmployee(Principal principal, @PathVariable String userId) {
    return service.fire(principal, userId);
  }


}

package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.EmployeeRoles;
import com.shalhlad.product_delivery_service.dto.request.UserRecruitRequest;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.service.EmployeeService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Api(tags = "employees")
public class EmployeeController {

  private final EmployeeService service;

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  @ApiOperation(value = "recruitUser",
      notes = "Makes customer the employee of authorized employee department")
  public EmployeeDetailsResponse recruitUser(
      @ApiIgnore Principal principal,
      @RequestBody @Valid UserRecruitRequest userRecruitRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.recruitUser(principal, userRecruitRequest);
  }

  @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  @ApiOperation(value = "getEmployeeByUserId", notes = "Returns employee by userId")
  public EmployeeDetailsResponse getEmployeeByUserId(
      @ApiIgnore Principal principal,
      @PathVariable String userId) {
    return service.findEmployeeByUserId(principal, userId);
  }

  @PatchMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  @ApiOperation(value = "changeEmployeePositionByUserId",
      notes = "Change employee position of authorized employee department")
  public EmployeeDetailsResponse changeEmployeeRole(
      @ApiIgnore Principal principal,
      @PathVariable String userId,
      @RequestParam EmployeeRoles role) {
    return service.changeRoleOfEmployee(principal, userId, role);
  }

  @DeleteMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  @ApiOperation(value = "fireEmployeeByUserId",
      notes = "Fire employee of authorized employee department by userId")
  public UserDetailsResponse fireEmployee(@ApiIgnore Principal principal,
      @PathVariable String userId) {
    return service.fireEmployeeByUserId(principal, userId);
  }


}

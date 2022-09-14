package com.shalhlad.productdeliveryservice.controller;

import com.shalhlad.productdeliveryservice.dto.request.EmployeeRoles;
import com.shalhlad.productdeliveryservice.dto.request.UserRecruitRequest;
import com.shalhlad.productdeliveryservice.dto.response.EmployeeDetailsResponse;
import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;
import com.shalhlad.productdeliveryservice.service.EmployeeService;
import com.shalhlad.productdeliveryservice.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("${apiPrefix}/employees")
@RequiredArgsConstructor
@Tag(name = "employees")
@SecurityRequirement(name = "Bearer Authentication")
public class EmployeeController {

  private final EmployeeService service;

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  @Operation(summary = "recruitUser",
      description = "Makes customer the employee of authorized employee department")
  public EmployeeDetailsResponse recruitUser(
      @Parameter(hidden = true) Principal principal,
      @RequestBody @Valid UserRecruitRequest userRecruitRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.recruitUser(principal, userRecruitRequest);
  }

  @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  @Operation(summary = "getEmployeeByUserId", description = "Returns employee by userId")
  public EmployeeDetailsResponse getEmployeeByUserId(
      @Parameter(hidden = true) Principal principal,
      @PathVariable String userId) {
    return service.findEmployeeByUserId(principal, userId);
  }

  @PatchMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  @Operation(summary = "changeEmployeePositionByUserId",
      description = "Change employee position of authorized employee department")
  public EmployeeDetailsResponse changeEmployeeRole(
      @Parameter(hidden = true) Principal principal,
      @PathVariable String userId,
      @RequestParam EmployeeRoles role) {
    return service.changeRoleOfEmployee(principal, userId, role);
  }

  @DeleteMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyRole({'ADMIN', 'DEPARTMENT_HEAD'})")
  @Operation(summary = "fireEmployeeByUserId",
      description = "Fire employee of authorized employee department by userId")
  public UserDetailsResponse fireEmployee(
      @Parameter(hidden = true) Principal principal,
      @PathVariable String userId) {
    return service.fireEmployeeByUserId(principal, userId);
  }


}

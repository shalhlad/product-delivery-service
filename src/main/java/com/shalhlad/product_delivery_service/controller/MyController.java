package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.EmployeeRoles;
import com.shalhlad.product_delivery_service.dto.request.ProcessingOrderCharacteristics;
import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateRequest;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.OrderDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.service.MyService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPrefix}/my")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@Tag(name = "my")
@SecurityRequirement(name = "Bearer Authentication")
public class MyController {

  private final MyService service;

  @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getProfileDetailsOfAuthorizedUser",
      description = "Returns profile information of authorized user")
  public UserDetailsResponse getProfileDetailsOfAuthorizedUser(
      @Parameter(hidden = true) Principal principal) {
    return service.getDetailsOfAuthorizedUser(principal);
  }

  @PatchMapping(value = "/user",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "updateProfileDetailsOfAuthorizedUser",
      description = "Update profile fields")
  public UserDetailsResponse updateProfileDetailsOfAuthorizedUser(
      @Parameter(hidden = true) Principal principal,
      @RequestBody @Valid UserDetailsUpdateRequest userDetailsUpdateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.updateDetailsOfAuthorizedUser(principal, userDetailsUpdateRequest);
  }

  @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getOrdersOfAuthorizedUser",
      description = "Returns orders created by authorized user")
  public Page<OrderDetailsResponse> getOrdersOfAuthorizedUser(
      @Parameter(hidden = true) Principal principal,
      @RequestParam(required = false, defaultValue = "false") boolean active,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "15") int size) {
    return service.getOrdersOfAuthorizedUser(principal, active, PageRequest.of(page, size));
  }

  @GetMapping(value = "/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getOrderOfAuthorizedUserByOrderId",
      description = "Returns order created by authorized user by orderId")
  public OrderDetailsResponse getOrderOfAuthorizedUserByOrderId(
      @Parameter(hidden = true) Principal principal,
      @PathVariable Long orderId) {
    return service.getOrderOfAuthorizedUserByOrderId(principal, orderId);
  }

  @GetMapping(value = "/department", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getDepartmentOfAuthorizedEmployee",
      description = "Returns department of authorized employee")
  @PreAuthorize("hasAnyRole({'DEPARTMENT_HEAD', 'COURIER', 'COLLECTOR', 'WAREHOUSEMAN'})")
  public DepartmentDetailsResponse getDepartmentOfAuthorizedEmployee(
      @Parameter(hidden = true) Principal principal) {
    return service.getDepartmentOfAuthorizedUser(principal);
  }

  @GetMapping(value = "/department/orders", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getOrdersOfAuthorizedEmployeeDepartment",
      description = "Returns orders that can be processed or already processing by authorized employee")
  @PreAuthorize("hasAnyRole({'DEPARTMENT_HEAD', 'COURIER', 'COLLECTOR', 'WAREHOUSEMAN'})")
  public Iterable<OrderDetailsResponse> getOrdersOfAuthorizedEmployeeDepartment(
      @Parameter(hidden = true) Principal principal,
      @RequestParam ProcessingOrderCharacteristics orderCharacteristic,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "15") int size
  ) {
    return service.getOrdersOfAuthorizedUserDepartment(principal, orderCharacteristic,
        PageRequest.of(page, size));
  }

  @GetMapping(value = "/department/employees", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyRole({'DEPARTMENT_HEAD', 'COURIER', 'COLLECTOR', 'WAREHOUSEMAN'})")
  @Operation(summary = "getEmployeesOfAuthorizedEmployeeDepartment",
      description = "Returns employees of authorized employee department")
  public Iterable<EmployeeDetailsResponse> getEmployeesOfAuthorizedEmployeeDepartment(
      @Parameter(hidden = true) Principal principal,
      @RequestParam(required = false) EmployeeRoles employeeRole) {
    return service.getEmployeesOfAuthorizedUserDepartment(principal, employeeRole);
  }

}

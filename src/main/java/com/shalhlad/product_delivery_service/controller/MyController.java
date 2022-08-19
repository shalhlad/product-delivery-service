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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/my")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@Api(tags = "my")
public class MyController {

  private final MyService service;

  @GetMapping
  @ApiOperation(value = "getProfileDetailsOfAuthorizedUser",
      notes = "Returns profile information of authorized user")
  public UserDetailsResponse getProfileDetailsOfAuthorizedUser(@ApiIgnore Principal principal) {
    return service.getDetailsOfAuthorizedUser(principal);
  }

  @PatchMapping
  @ApiOperation(value = "updateProfileDetailsOfAuthorizedUser",
      notes = "Update profile fields")
  public UserDetailsResponse updateProfileDetailsOfAuthorizedUser(
      @ApiIgnore Principal principal,
      @RequestBody @Valid UserDetailsUpdateRequest userDetailsUpdateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.updateDetailsOfAuthorizedUser(principal, userDetailsUpdateRequest);
  }

  @GetMapping("/orders")
  @ApiOperation(value = "getOrdersOfAuthorizedUser", notes = "Returns orders created by authorized user")
  public Page<OrderDetailsResponse> getOrdersOfAuthorizedUser(
      @ApiIgnore Principal principal,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "15") int size) {
    return service.getOrdersOfAuthorizedUser(principal, PageRequest.of(page, size));
  }

  @GetMapping("/orders/{orderId}")
  @ApiOperation(value = "getOrderOfAuthorizedUserByOrderId", notes = "Returns order created by authorized user by orderId")
  public OrderDetailsResponse getOrderOfAuthorizedUserByOrderId(@ApiIgnore Principal principal,
      @PathVariable Long orderId) {
    return service.getOrderOfAuthorizedUserByOrderId(principal, orderId);
  }

  @GetMapping("/department")
  @ApiOperation(value = "getDepartmentOfAuthorizedEmployee", notes = "Returns department of authorized employee")
  @PreAuthorize("hasAnyRole({'DEPARTMENT_HEAD', 'COURIER', 'COLLECTOR', 'WAREHOUSEMAN'})")
  public DepartmentDetailsResponse getDepartmentOfAuthorizedEmployee(
      @ApiIgnore Principal principal) {
    return service.getDepartmentOfAuthorizedUser(principal);
  }

  @GetMapping("/department/orders")
  @ApiOperation(value = "getOrdersOfAuthorizedEmployeeDepartment", notes = "Returns orders that can be processed or already processing by authorized employee")
  @PreAuthorize("hasAnyRole({'DEPARTMENT_HEAD', 'COURIER', 'COLLECTOR', 'WAREHOUSEMAN'})")
  public Iterable<OrderDetailsResponse> getOrdersOfAuthorizedEmployeeDepartment(
      @ApiIgnore Principal principal,
      @RequestParam ProcessingOrderCharacteristics orderCharacteristic,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "15") int size
  ) {
    return service.getOrdersOfAuthorizedUserDepartment(principal, orderCharacteristic,
        PageRequest.of(page, size));
  }

  @GetMapping("/department/employees")
  @PreAuthorize("hasAnyRole({'DEPARTMENT_HEAD', 'COURIER', 'COLLECTOR', 'WAREHOUSEMAN'})")
  @ApiOperation(value = "getEmployeesOfAuthorizedEmployeeDepartment", notes = "Returns employees of authorized employee department")
  public Iterable<EmployeeDetailsResponse> getEmployeesOfAuthorizedEmployeeDepartment(
      @ApiIgnore Principal principal,
      @RequestParam(required = false) EmployeeRoles employeeRole) {
    return service.getEmployeesOfAuthorizedUserDepartment(principal, employeeRole);
  }

}

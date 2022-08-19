package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.DepartmentCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.DepartmentUpdateRequest;
import com.shalhlad.product_delivery_service.dto.request.EmployeeRoles;
import com.shalhlad.product_delivery_service.dto.request.ProductQuantityChangeRequest;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsWithWarehouseResponse;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsResponse;
import com.shalhlad.product_delivery_service.service.DepartmentService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
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
@RequestMapping("${apiPrefix}/departments")
@RequiredArgsConstructor
@Tag(name = "departments")
public class DepartmentController {

  private final DepartmentService service;

  @GetMapping(value = "/{departmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getDepartmentById", description = "Returns department by id")
  public DepartmentDetailsResponse getById(@PathVariable Long departmentId) {
    return service.findDepartmentById(departmentId);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getAllDepartments", description = "Returns all departments")
  public Iterable<DepartmentDetailsResponse> getAll() {
    return service.findAllDepartments();
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @Operation(summary = "createDepartment", description = "Creates department")
  @SecurityRequirement(name = "Bearer Authentication")
  public DepartmentDetailsResponse create(
      @RequestBody @Valid DepartmentCreateRequest departmentCreateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.createDepartment(departmentCreateRequest);
  }

  @PatchMapping(value = "/{departmentId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @Operation(summary = "updateDepartmentById", description = "Update department fields by departmentId")
  @SecurityRequirement(name = "Bearer Authentication")
  public DepartmentDetailsResponse update(
      @PathVariable Long departmentId,
      @RequestBody @Valid DepartmentUpdateRequest departmentUpdateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.updateDepartmentDetails(departmentId, departmentUpdateRequest);
  }

  @GetMapping(value = "/{departmentId}/warehouse", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getDepartmentWithWarehouseByDepartmentId", description = "Returns department with warehouse by departmentId")
  public DepartmentDetailsWithWarehouseResponse getWithWarehouseById(
      @PathVariable Long departmentId) {
    return service.getDepartmentWithWarehouse(departmentId);
  }

  @PatchMapping(value = "/{departmentId}/warehouse",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('WAREHOUSEMAN')")
  @Operation(summary = "updateProductQuantityInDepartmentWarehouseByDepartmentId",
      description = "Updates quantity of products in department's warehouse by department id")
  @SecurityRequirement(name = "Bearer Authentication")
  public DepartmentDetailsWithWarehouseResponse changeProductQuantity(
      @PathVariable Long departmentId,
      @RequestBody @Valid ProductQuantityChangeRequest productQuantityChangeRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.changeProductQuantityInDepartment(departmentId, productQuantityChangeRequest);
  }

  @GetMapping(value = "/{departmentId}/employees", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  @Operation(summary = "getEmployeesOfDepartmentByDepartmentId", description = "Returns employees of department by departmentId")
  @SecurityRequirement(name = "Bearer Authentication")
  public Iterable<EmployeeDetailsResponse> getEmployeesOfDepartmentById(
      @PathVariable Long departmentId,
      @RequestParam(required = false) EmployeeRoles employeeRole) {
    return service.findEmployeesOfDepartment(departmentId, employeeRole);
  }


}

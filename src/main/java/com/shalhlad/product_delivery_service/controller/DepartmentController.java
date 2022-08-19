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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/departments")
@RequiredArgsConstructor
@Api(tags = "departments")
public class DepartmentController {

  private final DepartmentService service;

  @GetMapping(value = "/{departmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "getDepartmentById", notes = "Returns department by id")
  public DepartmentDetailsResponse getById(@PathVariable Long departmentId) {
    return service.findDepartmentById(departmentId);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "getAllDepartments", notes = "Returns all departments")
  public Iterable<DepartmentDetailsResponse> getAll() {
    return service.findAllDepartments();
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @ApiOperation(value = "createDepartment", notes = "Creates department")
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
  @ApiOperation(value = "updateDepartmentById", notes = "Update department fields by departmentId")
  public DepartmentDetailsResponse update(
      @PathVariable Long departmentId,
      @RequestBody @Valid DepartmentUpdateRequest departmentUpdateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.updateDepartmentDetails(departmentId, departmentUpdateRequest);
  }

  @GetMapping(value = "/{departmentId}/warehouse", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "getDepartmentWithWarehouseByDepartmentId", notes = "Returns department with warehouse by departmentId")
  public DepartmentDetailsWithWarehouseResponse getWithWarehouseById(
      @PathVariable Long departmentId) {
    return service.getDepartmentWithWarehouse(departmentId);
  }

  @PatchMapping(value = "/{departmentId}/warehouse",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('WAREHOUSEMAN')")
  @ApiOperation(value = "updateProductQuantityInDepartmentWarehouseByDepartmentId",
      notes = "Updates quantity of products in department's warehouse by department id")
  public DepartmentDetailsWithWarehouseResponse changeProductQuantity(
      @PathVariable Long departmentId,
      @RequestBody @Valid ProductQuantityChangeRequest productQuantityChangeRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.changeProductQuantityInDepartment(departmentId, productQuantityChangeRequest);
  }

  @GetMapping(value = "/{departmentId}/employees", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('DEPARTMENT_HEAD')")
  @ApiOperation(value = "getEmployeesOfDepartmentByDepartmentId", notes = "Returns employees of department by departmentId")
  public Iterable<EmployeeDetailsResponse> getEmployeesOfDepartmentById(
      @PathVariable Long departmentId,
      @RequestParam(required = false) EmployeeRoles employeeRole) {
    return service.findEmployeesOfDepartment(departmentId, employeeRole);
  }


}

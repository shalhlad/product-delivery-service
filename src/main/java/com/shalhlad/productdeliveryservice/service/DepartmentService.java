package com.shalhlad.productdeliveryservice.service;

import com.shalhlad.productdeliveryservice.dto.request.DepartmentCreateRequest;
import com.shalhlad.productdeliveryservice.dto.request.DepartmentUpdateRequest;
import com.shalhlad.productdeliveryservice.dto.request.EmployeeRoles;
import com.shalhlad.productdeliveryservice.dto.request.ProductQuantityChangeRequest;
import com.shalhlad.productdeliveryservice.dto.response.DepartmentDetailsResponse;
import com.shalhlad.productdeliveryservice.dto.response.DepartmentDetailsWithWarehouseResponse;
import com.shalhlad.productdeliveryservice.dto.response.EmployeeDetailsResponse;

public interface DepartmentService {

  DepartmentDetailsResponse createDepartment(DepartmentCreateRequest departmentCreateRequest);

  DepartmentDetailsResponse findDepartmentById(Long id);

  Iterable<DepartmentDetailsResponse> findAllDepartments();

  DepartmentDetailsWithWarehouseResponse changeProductQuantityInDepartment(Long departmentId,
      ProductQuantityChangeRequest productQuantityChangeRequest);

  DepartmentDetailsWithWarehouseResponse getDepartmentWithWarehouse(Long id);

  DepartmentDetailsResponse updateDepartmentDetails(Long id,
      DepartmentUpdateRequest departmentUpdateRequest);

  Iterable<EmployeeDetailsResponse> findEmployeesOfDepartment(Long id, EmployeeRoles employeeRole);
}

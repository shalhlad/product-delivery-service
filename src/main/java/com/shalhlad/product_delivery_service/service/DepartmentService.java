package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.DepartmentCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.DepartmentUpdateRequest;
import com.shalhlad.product_delivery_service.dto.request.EmployeeRoles;
import com.shalhlad.product_delivery_service.dto.request.ProductQuantityChangeRequest;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsWithWarehouseResponse;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsResponse;

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

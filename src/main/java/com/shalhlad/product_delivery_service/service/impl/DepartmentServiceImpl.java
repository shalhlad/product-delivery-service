package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.DepartmentCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.DepartmentUpdateRequest;
import com.shalhlad.product_delivery_service.dto.request.EmployeeRoles;
import com.shalhlad.product_delivery_service.dto.request.ProductQuantityChangeRequest;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsWithWarehouseResponse;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsResponse;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.product.Product;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.DepartmentMapper;
import com.shalhlad.product_delivery_service.mapper.EmployeeMapper;
import com.shalhlad.product_delivery_service.repository.DepartmentRepository;
import com.shalhlad.product_delivery_service.repository.EmployeeRepository;
import com.shalhlad.product_delivery_service.repository.ProductRepository;
import com.shalhlad.product_delivery_service.service.DepartmentService;
import java.util.Map;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final ProductRepository productRepository;
  private final EmployeeRepository employeeRepository;
  private final DepartmentMapper departmentMapper;
  private final EmployeeMapper employeeMapper;

  @Override
  public DepartmentDetailsResponse createDepartment(
      DepartmentCreateRequest departmentCreateRequest) {
    Department department = new Department();
    department.setAddress(departmentCreateRequest.getAddress());

    Department saved = departmentRepository.save(department);
    return departmentMapper.toDetailsResponse(saved);
  }

  @Override
  public DepartmentDetailsResponse findDepartmentById(Long id) {
    return departmentRepository.findById(id)
        .map(departmentMapper::toDetailsResponse)
        .orElseThrow(() -> new NotFoundException("Department not found with id: " + id));
  }

  @Override
  public Iterable<DepartmentDetailsResponse> findAllDepartments() {
    Iterable<Department> departments = departmentRepository.findAll();
    return departmentMapper.toDetailsResponse(departments);
  }

  @Override
  public DepartmentDetailsWithWarehouseResponse changeProductQuantityInDepartment(Long departmentId,
      ProductQuantityChangeRequest productQuantityChangeRequest) {
    Department department = departmentRepository.findById(departmentId)
        .orElseThrow(() -> new NotFoundException("Department not found with id: " + departmentId));

    Product product = productRepository.findById(productQuantityChangeRequest.getProductId())
        .orElseThrow(() -> new NotFoundException(
            "Product not found with id: " + productQuantityChangeRequest.getProductId()));

    Map<Product, Integer> warehouse = department.getProductWarehouse();

    switch (productQuantityChangeRequest.getAction()) {
      case "increase" -> warehouse.put(product,
          warehouse.getOrDefault(product, 0) + productQuantityChangeRequest.getNumberOfProducts());
      case "reduce" -> {
        if (warehouse.containsKey(product)
            && warehouse.get(product) >= productQuantityChangeRequest.getNumberOfProducts()) {
          warehouse.put(product,
              warehouse.get(product) - productQuantityChangeRequest.getNumberOfProducts());
        } else {
          throw new InvalidValueException(
              "Cannot reduce the quantity of products because the quantity to reduce more products in warehouse");
        }
      }
    }

    Department saved = departmentRepository.save(department);
    return departmentMapper.toDetailsWithWarehouseResponse(saved);
  }

  @Override
  public DepartmentDetailsWithWarehouseResponse getDepartmentWithWarehouse(Long id) {
    return departmentRepository.findById(id)
        .map(departmentMapper::toDetailsWithWarehouseResponse)
        .orElseThrow(() -> new NotFoundException("Department not found with id: " + id));
  }

  @Override
  public DepartmentDetailsResponse updateDepartmentDetails(Long id,
      DepartmentUpdateRequest departmentUpdateRequest) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Department not found with id: " + id));
    departmentMapper.update(department, departmentUpdateRequest);

    Department saved = departmentRepository.save(department);
    return departmentMapper.toDetailsResponse(saved);
  }

  @Override
  public Iterable<EmployeeDetailsResponse> findEmployeesOfDepartment(Long id,
      EmployeeRoles employeeRole) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Department not found with id: " + id));
    Iterable<Employee> employees = employeeRole == null ?
        employeeRepository.findAllByDepartment(department) :
        employeeRepository.findAllByDepartmentAndRole(department, employeeRole.toEntityRole());
    return employeeMapper.toDetailsResponse(employees);
  }
}

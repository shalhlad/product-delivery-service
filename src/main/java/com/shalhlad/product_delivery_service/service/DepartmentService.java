package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.DepartmentCreationDto;
import com.shalhlad.product_delivery_service.dto.request.ProductQuantityToChangeDto;
import com.shalhlad.product_delivery_service.entity.department.Department;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

  Department create(DepartmentCreationDto departmentCreationDto);

  Department findById(Long id);

  Iterable<Department> findAll(Pageable pageable);

  Department changeProductQuantity(Long departmentId,
      ProductQuantityToChangeDto productQuantityToChangeDto);
}

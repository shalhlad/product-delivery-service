package com.shalhlad.product_delivery_service.repository;

import com.shalhlad.product_delivery_service.entity.department.Department;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends CrudRepository<Department, Long> {

}

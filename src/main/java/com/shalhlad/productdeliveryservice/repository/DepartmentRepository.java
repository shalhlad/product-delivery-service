package com.shalhlad.productdeliveryservice.repository;

import com.shalhlad.productdeliveryservice.entity.department.Department;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends CrudRepository<Department, Long> {

}

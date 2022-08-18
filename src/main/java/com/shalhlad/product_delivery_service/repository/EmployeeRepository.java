package com.shalhlad.product_delivery_service.repository;

import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.Role;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

  Optional<Employee> findByEmail(String email);

  Optional<Employee> findByUserId(String userId);

  Iterable<Employee> findAllByDepartment(Department department);

  Iterable<Employee> findAllByDepartmentAndRole(Department department, Role role);
}

package com.shalhlad.product_delivery_service.repository;

import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  Optional<Employee> findByEmail(String email);

  Optional<Employee> findByUserId(String userId);

  Iterable<Employee> findAllByDepartment(Department department);

  Optional<Employee> findByDepartmentAndUserId(Department department, String userId);
}

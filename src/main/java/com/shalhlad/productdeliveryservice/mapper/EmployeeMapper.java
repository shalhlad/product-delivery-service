package com.shalhlad.productdeliveryservice.mapper;

import com.shalhlad.productdeliveryservice.dto.response.EmployeeDetailsResponse;
import com.shalhlad.productdeliveryservice.entity.user.Employee;
import com.shalhlad.productdeliveryservice.entity.user.User;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(uses = DepartmentMapper.class)
public interface EmployeeMapper {

  User toUser(Employee employee);

  EmployeeDetailsResponse toDetailsResponse(Employee employee);

  List<EmployeeDetailsResponse> toDetailsResponse(Iterable<Employee> employees);
}

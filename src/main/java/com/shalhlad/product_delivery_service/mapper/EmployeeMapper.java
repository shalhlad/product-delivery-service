package com.shalhlad.product_delivery_service.mapper;

import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsDto;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.User;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(uses = DepartmentMapper.class)
public interface EmployeeMapper {

  User toUser(Employee employee);

  EmployeeDetailsDto toDetailsDto(Employee employee);

  List<EmployeeDetailsDto> toDetailsDto(Iterable<Employee> employees);
}

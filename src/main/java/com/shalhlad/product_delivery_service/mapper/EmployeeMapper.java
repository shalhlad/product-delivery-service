package com.shalhlad.product_delivery_service.mapper;

import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.User;
import org.mapstruct.Mapper;

@Mapper
public interface EmployeeMapper {

  User toUser(Employee employee);
}

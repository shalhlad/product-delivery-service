package com.shalhlad.product_delivery_service.mapper;

import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsDto;
import com.shalhlad.product_delivery_service.entity.department.Department;
import org.mapstruct.Mapper;

@Mapper
public interface DepartmentMapper {

  DepartmentDetailsDto toDetailsDto(Department department);

  Iterable<DepartmentDetailsDto> toDetailsDto(Iterable<Department> departments);
}

package com.shalhlad.product_delivery_service.mapper;

import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsDto;
import com.shalhlad.product_delivery_service.dto.response.OrderDepartmentDetailsDto;
import com.shalhlad.product_delivery_service.dto.response.ProductWithQuantityDto;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.product.Product;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface DepartmentMapper {

  @Mapping(target = "productWarehouse", qualifiedByName = "productQuantityMapToList")
  DepartmentDetailsDto toDetailsDto(Department department);

  Iterable<DepartmentDetailsDto> toDetailsDto(Iterable<Department> departments);

  OrderDepartmentDetailsDto toOrderDepartmentDetailsDto(Department department);

  @Named("productQuantityMapToList")
  default List<ProductWithQuantityDto> productQuantityMapToList(
      Map<Product, Integer> productQuantityMap) {
    return productQuantityMap.entrySet().stream()
        .map(e -> new ProductWithQuantityDto(e.getKey(), e.getValue()))
        .toList();
  }
}

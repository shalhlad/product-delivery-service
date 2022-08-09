package com.shalhlad.product_delivery_service.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.shalhlad.product_delivery_service.dto.request.DepartmentUpdateDto;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsDto;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsWithWarehouseDto;
import com.shalhlad.product_delivery_service.dto.response.ProductWithQuantityDto;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.product.Product;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper
public interface DepartmentMapper {

  @Mapping(target = "productWarehouse", qualifiedByName = "productQuantityMapToList")
  DepartmentDetailsWithWarehouseDto toDetailsWithWarehouseDto(Department department);

  List<DepartmentDetailsWithWarehouseDto> toDetailsWithWarehouseDto(
      Iterable<Department> departments);

  DepartmentDetailsDto toDetailsDto(Department department);

  @Mappings({
      @Mapping(target = "productWarehouse", ignore = true),
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "address", nullValuePropertyMappingStrategy = IGNORE)
  })
  void update(@MappingTarget Department department, DepartmentUpdateDto departmentUpdateDto);

  @Named("productQuantityMapToList")
  default List<ProductWithQuantityDto> productQuantityMapToList(
      Map<Product, Integer> productQuantityMap) {
    return productQuantityMap.entrySet().stream()
        .map(e -> new ProductWithQuantityDto(e.getKey(), e.getValue()))
        .toList();
  }
}

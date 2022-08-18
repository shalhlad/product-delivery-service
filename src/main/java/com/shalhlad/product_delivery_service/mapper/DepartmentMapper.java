package com.shalhlad.product_delivery_service.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.shalhlad.product_delivery_service.dto.request.DepartmentUpdateRequest;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsWithWarehouseResponse;
import com.shalhlad.product_delivery_service.dto.response.ProductWithQuantityResponse;
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
public abstract class DepartmentMapper {

  @Mapping(target = "productWarehouse", qualifiedByName = "productQuantityMapToList")
  public abstract DepartmentDetailsWithWarehouseResponse toDetailsWithWarehouseResponse(
      Department department);

  public abstract DepartmentDetailsResponse toDetailsResponse(Department department);

  public abstract List<DepartmentDetailsResponse> toDetailsResponse(
      Iterable<Department> departments);

  @Mappings({
      @Mapping(target = "productWarehouse", ignore = true),
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "address", nullValuePropertyMappingStrategy = IGNORE)
  })
  public abstract void update(@MappingTarget Department department,
      DepartmentUpdateRequest departmentUpdateRequest);


  @Named("productQuantityMapToList")
  protected List<ProductWithQuantityResponse> productQuantityMapToList(
      Map<Product, Integer> productQuantityMap) {
    return productQuantityMap.entrySet().stream()
        .map(e -> new ProductWithQuantityResponse(e.getKey(), e.getValue()))
        .toList();
  }
}

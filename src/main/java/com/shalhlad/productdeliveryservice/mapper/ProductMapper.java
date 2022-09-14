package com.shalhlad.productdeliveryservice.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.shalhlad.productdeliveryservice.dto.request.ProductCreateRequest;
import com.shalhlad.productdeliveryservice.dto.request.ProductUpdateRequest;
import com.shalhlad.productdeliveryservice.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface ProductMapper {

  @Mappings({
      @Mapping(target = "category", ignore = true),
      @Mapping(target = "id", ignore = true)
  })
  Product toEntity(ProductCreateRequest productCreateRequest);


  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "category", ignore = true),
      @Mapping(target = "name", nullValuePropertyMappingStrategy = IGNORE),
      @Mapping(target = "price", nullValuePropertyMappingStrategy = IGNORE)
  })
  void update(@MappingTarget Product product, ProductUpdateRequest productUpdateRequest);
}

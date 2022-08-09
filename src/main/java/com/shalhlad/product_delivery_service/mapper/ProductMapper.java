package com.shalhlad.product_delivery_service.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.shalhlad.product_delivery_service.dto.request.ProductCreationDto;
import com.shalhlad.product_delivery_service.dto.request.ProductUpdateDto;
import com.shalhlad.product_delivery_service.entity.product.Product;
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
  Product toEntity(ProductCreationDto productCreationDto);


  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "category", ignore = true),
      @Mapping(target = "name", nullValuePropertyMappingStrategy = IGNORE),
      @Mapping(target = "price", nullValuePropertyMappingStrategy = IGNORE)
  })
  void update(@MappingTarget Product product, ProductUpdateDto productUpdateDto);
}

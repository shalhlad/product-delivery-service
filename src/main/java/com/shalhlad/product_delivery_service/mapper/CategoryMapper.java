package com.shalhlad.product_delivery_service.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.shalhlad.product_delivery_service.dto.request.CategoryUpdateRequest;
import com.shalhlad.product_delivery_service.entity.product.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface CategoryMapper {

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "name", nullValuePropertyMappingStrategy = IGNORE)
  })
  void update(@MappingTarget Category category, CategoryUpdateRequest categoryUpdateRequest);
}

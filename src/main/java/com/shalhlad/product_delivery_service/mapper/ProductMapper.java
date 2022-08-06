package com.shalhlad.product_delivery_service.mapper;

import com.shalhlad.product_delivery_service.dto.request.ProductCreationDto;
import com.shalhlad.product_delivery_service.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface ProductMapper {

  @Mappings({
      @Mapping(target = "category", ignore = true),
      @Mapping(target = "id", ignore = true)
  })
  Product toEntity(ProductCreationDto productCreationDto);

}

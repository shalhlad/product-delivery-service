package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.CategoryCreationDto;
import com.shalhlad.product_delivery_service.entity.product.Category;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

  Category create(CategoryCreationDto categoryCreationDto);

  Iterable<Category> findAll(Pageable pageable);

  Category findById(Long id);

  void deleteById(Long id);
}

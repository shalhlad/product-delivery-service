package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.CategoryCreationDto;
import com.shalhlad.product_delivery_service.dto.request.CategoryUpdateDto;
import com.shalhlad.product_delivery_service.entity.product.Category;

public interface CategoryService {

  Category create(CategoryCreationDto categoryCreationDto);

  Iterable<Category> findAll();

  Category findById(Long id);

  void deleteById(Long id);

  Category update(Long id, CategoryUpdateDto categoryUpdateDto);
}

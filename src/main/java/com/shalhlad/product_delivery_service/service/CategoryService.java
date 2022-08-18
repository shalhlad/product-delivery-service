package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.CategoryCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.CategoryUpdateRequest;
import com.shalhlad.product_delivery_service.entity.product.Category;

public interface CategoryService {

  Category createCategory(CategoryCreateRequest categoryCreateRequest);

  Iterable<Category> findAllCategories();

  Category findCategoryById(Long id);

  void deleteCategoryById(Long id);

  Category updateCategoryById(Long id, CategoryUpdateRequest categoryUpdateRequest);
}

package com.shalhlad.productdeliveryservice.service;

import com.shalhlad.productdeliveryservice.dto.request.CategoryCreateRequest;
import com.shalhlad.productdeliveryservice.dto.request.CategoryUpdateRequest;
import com.shalhlad.productdeliveryservice.entity.product.Category;

public interface CategoryService {

  Category createCategory(CategoryCreateRequest categoryCreateRequest);

  Iterable<Category> findAllCategories();

  Category findCategoryById(Long id);

  void deleteCategoryById(Long id);

  Category updateCategoryById(Long id, CategoryUpdateRequest categoryUpdateRequest);
}

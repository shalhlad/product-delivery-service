package com.shalhlad.productdeliveryservice.service.impl;

import com.shalhlad.productdeliveryservice.dto.request.CategoryCreateRequest;
import com.shalhlad.productdeliveryservice.dto.request.CategoryUpdateRequest;
import com.shalhlad.productdeliveryservice.entity.product.Category;
import com.shalhlad.productdeliveryservice.exception.NotFoundException;
import com.shalhlad.productdeliveryservice.mapper.CategoryMapper;
import com.shalhlad.productdeliveryservice.repository.CategoryRepository;
import com.shalhlad.productdeliveryservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Autowired
  public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  @Override
  public Category createCategory(CategoryCreateRequest categoryCreateRequest) {
    Category category = new Category();
    category.setName(categoryCreateRequest.getName());
    return categoryRepository.save(category);
  }

  @Override
  public Iterable<Category> findAllCategories() {
    return categoryRepository.findAll();
  }

  @Override
  public Category findCategoryById(Long categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));
  }

  @Override
  public void deleteCategoryById(Long categoryId) {
    if (categoryRepository.existsById(categoryId)) {
      categoryRepository.deleteById(categoryId);
    } else {
      throw new NotFoundException("Category not found with id " + categoryId);
    }
  }

  @Override
  public Category updateCategoryById(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
    Category category = findCategoryById(categoryId);
    categoryMapper.update(category, categoryUpdateRequest);
    return categoryRepository.save(category);
  }
}

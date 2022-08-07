package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.CategoryCreationDto;
import com.shalhlad.product_delivery_service.dto.request.CategoryUpdateDto;
import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.CategoryMapper;
import com.shalhlad.product_delivery_service.repository.CategoryRepository;
import com.shalhlad.product_delivery_service.service.CategoryService;
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
  public Category create(CategoryCreationDto categoryCreationDto) {
    Category category = new Category();
    category.setName(categoryCreationDto.getName());
    return categoryRepository.save(category);
  }

  @Override
  public Iterable<Category> findAll() {
    return categoryRepository.findAll();
  }

  @Override
  public Category findById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
  }

  @Override
  public void deleteById(Long id) {
    if (categoryRepository.existsById(id)) {
      categoryRepository.deleteById(id);
    } else {
      throw new NotFoundException("Category not found with id " + id);
    }
  }

  @Override
  public Category update(Long id, CategoryUpdateDto categoryUpdateDto) {
    Category category = findById(id);
    categoryMapper.update(category, categoryUpdateDto);
    return categoryRepository.save(category);
  }
}

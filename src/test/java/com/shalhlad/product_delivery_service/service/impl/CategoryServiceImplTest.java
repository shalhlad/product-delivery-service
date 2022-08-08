package com.shalhlad.product_delivery_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.shalhlad.product_delivery_service.dto.request.CategoryCreationDto;
import com.shalhlad.product_delivery_service.dto.request.CategoryUpdateDto;
import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.CategoryMapper;
import com.shalhlad.product_delivery_service.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CategoryServiceImplTest {

  @InjectMocks
  private CategoryServiceImpl service;

  @Mock
  private CategoryRepository categoryRepository;
  @Mock
  private CategoryMapper categoryMapper;

  private Category category;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this).close();

    category = new Category();
    category.setId(1L);
    category.setName("test");
  }

  @Test
  public void create() {
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    Category actual = service.create(new CategoryCreationDto());
    assertThat(actual).isEqualTo(category);
  }

  @Test
  public void findById() {
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

    Category actual = service.findById(1L);
    assertThat(actual).isEqualTo(category);
  }

  @Test
  public void findById_NotFoundException() {
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> service.findById(1L));
  }

  @Test
  public void findAll() {
    when(categoryRepository.findAll()).thenReturn(List.of(category));

    assertThat(service.findAll()).isEqualTo(List.of(category));
  }

  @Test
  public void deleteById() {
    when(categoryRepository.existsById(anyLong())).thenReturn(true);

    assertThatNoException().isThrownBy(() -> service.deleteById(1L));
  }

  @Test
  public void deleteById_NotFoundException() {
    when(categoryRepository.existsById(anyLong())).thenReturn(false);

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> service.deleteById(1L));
  }

  @Test
  public void update() {
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
    doNothing().when(categoryMapper).update(any(Category.class), any(CategoryUpdateDto.class));
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    Category actual = service.update(1L, new CategoryUpdateDto());
    assertThat(actual).isEqualTo(category);
  }
}

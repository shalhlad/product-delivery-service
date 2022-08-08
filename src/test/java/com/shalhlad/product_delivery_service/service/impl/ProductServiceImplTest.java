package com.shalhlad.product_delivery_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.shalhlad.product_delivery_service.dto.request.ProductCreationDto;
import com.shalhlad.product_delivery_service.dto.request.ProductUpdateDto;
import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.entity.product.Product;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.ProductMapper;
import com.shalhlad.product_delivery_service.repository.CategoryRepository;
import com.shalhlad.product_delivery_service.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class ProductServiceImplTest {

  @InjectMocks
  private ProductServiceImpl service;

  @Mock
  private ProductRepository productRepository;
  @Mock
  private CategoryRepository categoryRepository;
  @Mock
  private ProductMapper productMapper;

  private Product product;
  private Category category;
  private Pageable pageable;
  private Page<Product> productPage;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this).close();

    category = new Category();
    category.setId(1L);
    category.setName("test");

    product = new Product();
    product.setId(1L);
    product.setPrice(BigDecimal.valueOf(52, 2));
    product.setName("test1");
    product.setCategory(category);

    Product secondProduct = new Product();
    secondProduct.setId(2L);
    secondProduct.setPrice(BigDecimal.valueOf(51, 2));
    secondProduct.setName("test2");
    secondProduct.setCategory(category);

    pageable = PageRequest.of(0, 2);
    productPage = new PageImpl<>(List.of(product, secondProduct), pageable, 2);
  }

  @Test
  public void findById() {
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

    Product actual = service.findById(1L);
    assertThat(actual).isEqualTo(product);
  }

  @Test
  public void findById_NotFoundException() {
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> service.findById(1L));
  }

  @Test
  public void findAll() {
    when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

    Page<Product> actual = service.findAll(null, pageable);
    assertThat(actual).isEqualTo(productPage);
  }

  @Test
  public void findAllWithNonNullCategoryId() {
    when(productRepository.findAllByCategory(any(Category.class), any(Pageable.class))).thenReturn(
        productPage);
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));

    Page<Product> actual = service.findAll(1L, pageable);
    assertThat(actual).isEqualTo(productPage);
  }

  @Test
  public void findAllWithNonNullCategoryId_NotFoundException() {
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.findAll(1L, pageable));
  }

  @Test
  public void create() {
    when(productMapper.toEntity(any(ProductCreationDto.class))).thenReturn(product);
    when(productRepository.save(product)).thenReturn(product);
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));

    ProductCreationDto productCreationDto = new ProductCreationDto();
    productCreationDto.setCategoryId(1L);

    Product actual = service.create(productCreationDto);
    assertThat(actual).isEqualTo(product);
  }

  @Test
  public void create_NotFoundException() {
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

    ProductCreationDto productCreationDto = new ProductCreationDto();
    productCreationDto.setCategoryId(1L);

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.create(productCreationDto));
  }

  @Test
  public void update() {
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
    when(productRepository.save(any(Product.class))).thenReturn(product);
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
    doNothing().when(productMapper).update(any(Product.class), any(ProductUpdateDto.class));

    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    productUpdateDto.setCategoryId(1L);

    Product actual = service.update(1L, productUpdateDto);
    assertThat(actual).isEqualTo(product);
  }

  @Test
  public void update_NotFoundException() {
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    productUpdateDto.setCategoryId(1L);

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.update(1L, productUpdateDto));
  }

}

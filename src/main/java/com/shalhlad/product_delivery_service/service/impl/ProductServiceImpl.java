package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.ProductCreationDto;
import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.entity.product.Product;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.ProductMapper;
import com.shalhlad.product_delivery_service.repository.CategoryRepository;
import com.shalhlad.product_delivery_service.repository.ProductRepository;
import com.shalhlad.product_delivery_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;

  @Autowired
  public ProductServiceImpl(ProductRepository productRepository,
      CategoryRepository categoryRepository,
      ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.productMapper = productMapper;
  }

  @Override
  public Iterable<Product> findAll(Long categoryId, Pageable pageable) {
    Page<Product> products;
    if (categoryId == null) {
      products = productRepository.findAll(pageable);
    } else {
      Category category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));
      products = productRepository.findAllByCategory(category, pageable);
    }
    return products;
  }

  @Override
  public Product create(ProductCreationDto productCreationDto) {
    Product product = productMapper.toEntity(productCreationDto);
    Category category = categoryRepository.findById(productCreationDto.getCategoryId())
        .orElseThrow(() -> new NotFoundException(
            "Category not found with id " + productCreationDto.getCategoryId()));
    product.setCategory(category);

    return productRepository.save(product);
  }

  @Override
  public Product findById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
  }

}

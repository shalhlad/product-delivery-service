package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.ProductCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.ProductUpdateRequest;
import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.entity.product.Product;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.ProductMapper;
import com.shalhlad.product_delivery_service.repository.CategoryRepository;
import com.shalhlad.product_delivery_service.repository.ProductRepository;
import com.shalhlad.product_delivery_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;

  @Override
  public Page<Product> findAllProducts(Long categoryId, Pageable pageable) {
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
  public Product createProduct(ProductCreateRequest productCreateRequest) {
    Product product = productMapper.toEntity(productCreateRequest);
    Category category = categoryRepository.findById(productCreateRequest.getCategoryId())
        .orElseThrow(() -> new NotFoundException(
            "Category not found with id " + productCreateRequest.getCategoryId()));
    product.setCategory(category);

    return productRepository.save(product);
  }

  @Override
  public Product findProductById(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
  }

  @Override
  public Product updateProductById(Long productId, ProductUpdateRequest productUpdateRequest) {
    Product product = findProductById(productId);

    if (productUpdateRequest.getCategoryId() != null) {
      Category category = categoryRepository.findById(productUpdateRequest.getCategoryId())
          .orElseThrow(() -> new NotFoundException("Category not found with id: " + productId));
      product.setCategory(category);
    }

    productMapper.update(product, productUpdateRequest);
    return productRepository.save(product);
  }

}

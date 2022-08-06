package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.ProductCreationDto;
import com.shalhlad.product_delivery_service.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  Page<Product> findAll(Long categoryId, Pageable pageable);

  Product create(ProductCreationDto productCreationDto);

  Product findById(Long id);
}

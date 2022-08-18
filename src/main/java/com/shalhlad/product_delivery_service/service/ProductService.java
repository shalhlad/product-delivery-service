package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.ProductCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.ProductUpdateRequest;
import com.shalhlad.product_delivery_service.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  Page<Product> findAllProducts(Long categoryId, Pageable pageable);

  Product createProduct(ProductCreateRequest productCreateRequest);

  Product findProductById(Long id);

  Product updateProductById(Long id, ProductUpdateRequest productUpdateRequest);
}

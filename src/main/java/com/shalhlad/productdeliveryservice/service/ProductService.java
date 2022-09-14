package com.shalhlad.productdeliveryservice.service;

import com.shalhlad.productdeliveryservice.dto.request.ProductCreateRequest;
import com.shalhlad.productdeliveryservice.dto.request.ProductUpdateRequest;
import com.shalhlad.productdeliveryservice.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  Page<Product> findAllProducts(Long categoryId, Pageable pageable);

  Product createProduct(ProductCreateRequest productCreateRequest);

  Product findProductById(Long id);

  Product updateProductById(Long id, ProductUpdateRequest productUpdateRequest);
}

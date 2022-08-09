package com.shalhlad.product_delivery_service.repository;

import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

  Page<Product> findAllByCategory(Category category, Pageable pageable);

}

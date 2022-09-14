package com.shalhlad.productdeliveryservice.repository;

import com.shalhlad.productdeliveryservice.entity.product.Category;
import com.shalhlad.productdeliveryservice.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

  Page<Product> findAllByCategory(Category category, Pageable pageable);

}

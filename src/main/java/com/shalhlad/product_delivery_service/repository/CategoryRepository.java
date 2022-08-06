package com.shalhlad.product_delivery_service.repository;

import com.shalhlad.product_delivery_service.entity.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}

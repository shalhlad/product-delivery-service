package com.shalhlad.product_delivery_service.repository;

import com.shalhlad.product_delivery_service.entity.product.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

}

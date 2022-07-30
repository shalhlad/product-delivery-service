package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.DepartmentCreationDto;
import com.shalhlad.product_delivery_service.dto.request.ProductQuantityToChangeDto;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.product.Product;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.repository.DepartmentRepository;
import com.shalhlad.product_delivery_service.repository.ProductRepository;
import com.shalhlad.product_delivery_service.service.DepartmentService;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final ProductRepository productRepository;

  @Autowired
  public DepartmentServiceImpl(DepartmentRepository departmentRepository,
      ProductRepository productRepository) {
    this.departmentRepository = departmentRepository;
    this.productRepository = productRepository;
  }

  @Override
  public Department create(DepartmentCreationDto departmentCreationDto) {
    Department department = new Department();
    department.setAddress(departmentCreationDto.getAddress());
    return departmentRepository.save(department);
  }

  @Override
  public Department findById(Long id) {
    return departmentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Department not found with id: " + id));
  }

  @Override
  public Iterable<Department> findAll(Pageable pageable) {
    return departmentRepository.findAll(pageable);
  }

  @Override
  public Department changeProductQuantity(Long departmentId,
      ProductQuantityToChangeDto productQuantityToChangeDto) {
    Department department = findById(departmentId);

    Product product = productRepository.findById(productQuantityToChangeDto.getProductId())
        .orElseThrow(() -> new NotFoundException(
            "Product not found with id: " + productQuantityToChangeDto.getProductId()));

    Map<Product, Integer> warehouse = department.getProductWarehouse();

    switch (productQuantityToChangeDto.getAction()) {
      case "increase" -> warehouse.put(product,
          warehouse.getOrDefault(product, 0) + productQuantityToChangeDto.getNumberOfProducts());
      case "reduce" -> {
        if (warehouse.containsKey(product)
            && warehouse.get(product) >= productQuantityToChangeDto.getNumberOfProducts()) {
          warehouse.put(product,
              warehouse.get(product) - productQuantityToChangeDto.getNumberOfProducts());
        } else {
          throw new InvalidValueException(
              "Cannot reduce the quantity of products because the quantity to reduce more products in warehouse");
        }
      }
    }

    return departmentRepository.save(department);
  }
}

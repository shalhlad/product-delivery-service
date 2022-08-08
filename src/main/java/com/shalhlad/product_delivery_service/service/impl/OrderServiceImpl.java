package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.OrderOperations;
import com.shalhlad.product_delivery_service.dto.request.OrderCreationDto;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.order.Order;
import com.shalhlad.product_delivery_service.entity.order.OrderedProductDetails;
import com.shalhlad.product_delivery_service.entity.order.Stage;
import com.shalhlad.product_delivery_service.entity.product.Product;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.exception.NoRightsException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.repository.DepartmentRepository;
import com.shalhlad.product_delivery_service.repository.EmployeeRepository;
import com.shalhlad.product_delivery_service.repository.OrderRepository;
import com.shalhlad.product_delivery_service.repository.ProductRepository;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.OrderService;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;
  private final ProductRepository productRepository;
  private final EmployeeRepository employeeRepository;

  @Autowired
  public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
      DepartmentRepository departmentRepository, ProductRepository productRepository,
      EmployeeRepository employeeRepository) {
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
    this.departmentRepository = departmentRepository;
    this.productRepository = productRepository;
    this.employeeRepository = employeeRepository;
  }

  @Override
  public Order create(Principal principal, OrderCreationDto orderCreationDto) {
    String email = principal.getName();
    Department department = departmentRepository.findById(orderCreationDto.getDepartmentId())
        .orElseThrow(() -> new NotFoundException(
            "Department not found with id: " + orderCreationDto.getDepartmentId()));

    User user = userRepository.findByEmail(email).orElseThrow();
    Map<Product, Integer> productWarehouse = department.getProductWarehouse();
    Map<Product, OrderedProductDetails> orderedProducts = new HashMap<>();
    orderCreationDto.getOrderedProducts().forEach((productId, quantity) -> {
      if (quantity <= 0) {
        throw new InvalidValueException("Quantity of product cannot be less or equal of 0");
      }

      Product product = productRepository.findById(productId)
          .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
      if (productWarehouse.getOrDefault(product, 0) >= quantity) {
        productWarehouse.put(product, productWarehouse.get(product) - quantity);
        orderedProducts.put(product, OrderedProductDetails.of(product.getPrice(), quantity));
      } else {
        throw new InvalidValueException(
            "Cannot order products because the quantity to order more products in warehouse");
      }
    });
    departmentRepository.save(department);

    return orderRepository.save(
        Order.builder()
            .user(user)
            .department(department)
            .deliveryAddress(orderCreationDto.getDeliveryAddress())
            .orderedProducts(orderedProducts)
            .stage(Stage.NEW)
            .stageHistory(Map.of(Stage.NEW, new Date()))
            .build()
    );
  }

  @Override
  public Order findById(Long id) {
    return orderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
  }

  @Override
  public Page<Order> findAllByUserId(String userId, Pageable pageable) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("User not found with userId: " + userId));
    return orderRepository.findAllByUser(user, pageable);
  }

  @Override
  public Page<Order> findAllByEmail(String email, Pageable pageable) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    return orderRepository.findAllByUser(user, pageable);
  }

  @Override
  public Order changeStage(Principal principal, Long orderId, OrderOperations operation) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    switch (operation) {
      case CANCEL -> {
        if (order.getStage() != Stage.NEW) {
          throw new NoRightsException("User can only cancel NEW orders");
        }
        if (!order.getUser().equals(user)) {
          throw new NoRightsException("Cannot cancel order because it is not belongs to current");
        }
        order.setStage(Stage.CANCELED);
      }
      case NEXT -> {
        Stage currentStage = order.getStage();
        Role userRole = user.getRole();
        if (userRole != Role.ROLE_COLLECTOR && userRole != Role.ROLE_COURIER) {
          throw new NoRightsException("Only collector and courier can manage order stage");
        }

        Employee employee = employeeRepository.findById(user.getId()).orElseThrow();
        if (!employee.getDepartment().equals(order.getDepartment())) {
          throw new NoRightsException("Employee can process orders only of his department");
        }

        if (currentStage.canBeChangedByCollector() && userRole != Role.ROLE_COLLECTOR) {
          throw new NoRightsException("Order of current stage can be manager only by collector");
        } else if (currentStage.canBeChangedByCourier() && userRole != Role.ROLE_COURIER) {
          throw new NoRightsException("Order of current stage can be managed only by courier");
        }

        order.setStage(currentStage.next());
      }
    }

    return orderRepository.save(order);
  }

  @Override
  public Page<Order> findAllByDepartmentAndStage(Principal principal, Long departmentId,
      Stage stage, Pageable pageable) {
    Department department = departmentRepository.findById(departmentId)
        .orElseThrow(() -> new NotFoundException("Department not found with id: " + departmentId));
    Employee employee = employeeRepository.findByEmail(principal.getName()).orElseThrow();
    if (!employee.getDepartment().equals(department)) {
      throw new NoRightsException(
          "Cannot get orders of department because current user does not works in requested department");
    }
    return orderRepository.findAllByDepartmentAndStage(department, stage, pageable);
  }


}

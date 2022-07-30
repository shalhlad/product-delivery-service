package com.shalhlad.product_delivery_service.service.impl;

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
  public Order create(String email, OrderCreationDto orderCreationDto) {
    Department department = departmentRepository.findById(orderCreationDto.getDepartmentId())
        .orElseThrow(() -> new NotFoundException(
            "Department not found with id: " + orderCreationDto.getDepartmentId()));

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

    Map<Product, Integer> productWarehouse = department.getProductWarehouse();

    Map<Product, OrderedProductDetails> orderedProducts = new HashMap<>();
    orderCreationDto.getOrderedProducts().forEach((productId, quantity) -> {
      if (quantity == 0) {
        return;
      }

      Product product = productRepository.findById(productId)
          .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
      if (productWarehouse.containsKey(product)
          && productWarehouse.getOrDefault(product, 0) >= quantity) {
        productWarehouse.put(product, productWarehouse.get(product) - quantity);
        orderedProducts.put(product, OrderedProductDetails.of(product.getPrice(), quantity));
      } else {
        throw new InvalidValueException(
            "Cannot order products because the quantity to order more products in warehouse");
      }
    });
    departmentRepository.save(department);

    return orderRepository.save(Order.builder().user(user).department(department)
        .deliveryAddress(orderCreationDto.getDeliveryAddress()).orderedProducts(orderedProducts)
        .stage(Stage.NEW).stageHistory(Map.of(Stage.NEW, new Date())).build());
  }

  @Override
  public Order getById(Long id) {
    return orderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
  }

  @Override
  public Iterable<Order> getByUserId(String userId, Pageable pageable) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("User not found with userId: " + userId));
    return orderRepository.findAllByUser(user, pageable);
  }

  @Override
  public Order changeStage(Principal principal, Long orderId, Stage newStage) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    switch (user.getRole()) {
      case ROLE_CUSTOMER -> {
        if (!order.getUser().equals(user)) {
          throw new NoRightsException("Order does not belongs to user");
        }
        if (newStage != Stage.CANCELED) {
          throw new NoRightsException("User can only cancel order");
        }
        if (order.getStage() != Stage.NEW) {
          throw new NoRightsException("User can only cancel NEW order");
        }
      }
      case ROLE_COLLECTOR, ROLE_COURIER -> {
        Employee employee = employeeRepository.findById(user.getId()).orElseThrow();
        if (!employee.getDepartment().equals(order.getDepartment())) {
          throw new NoRightsException("Employee can only process order of his department");
        }
        if (user.getRole() == Role.ROLE_COLLECTOR) {
          if (newStage != Stage.APPROVED && newStage != Stage.COLLECTING
              && newStage != Stage.COLLECTED) {
            throw new NoRightsException(
                "Collector can only approve, start collecting and finish collecting order");
          }
          if (order.getStage() != Stage.NEW && order.getStage() != Stage.APPROVED
              && order.getStage() != Stage.COLLECTING) {
            throw new NoRightsException(
                "Collector can only change stage of NEW, APPROVED or COLLECTING order");
          }
        } else {
          if (newStage != Stage.IN_DELIVERY && newStage != Stage.GIVEN) {
            throw new NoRightsException("Courier can only start delivering or give order");
          }
          if (order.getStage() != Stage.COLLECTED && order.getStage() != Stage.IN_DELIVERY) {
            throw new NoRightsException(
                "Courier can only change stage of COLLECTED, IN_DELIVERY order");
          }
        }
      }
      default -> throw new NoRightsException(
          "Only customer, collector or courier can change stage of order");
    }
    order.setStage(newStage);
    order.getStageHistory().put(newStage, new Date());

    if (newStage == Stage.CANCELED) {
      Department department = order.getDepartment();
      Map<Product, Integer> warehouse = department.getProductWarehouse();
      order.getOrderedProducts()
          .forEach((p, od) -> warehouse.put(p, warehouse.get(p) + od.getQuantity()));
      departmentRepository.save(department);
    }
    return orderRepository.save(order);
  }

}

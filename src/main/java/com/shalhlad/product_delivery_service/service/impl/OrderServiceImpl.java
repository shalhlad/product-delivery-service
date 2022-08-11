package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.OrderCreationDto;
import com.shalhlad.product_delivery_service.dto.request.OrderOperations;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.order.Order;
import com.shalhlad.product_delivery_service.entity.order.OrderHandlers;
import com.shalhlad.product_delivery_service.entity.order.OrderedProductDetails;
import com.shalhlad.product_delivery_service.entity.order.Stage;
import com.shalhlad.product_delivery_service.entity.product.Product;
import com.shalhlad.product_delivery_service.entity.user.Card;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.exception.NoRightsException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.repository.CardRepository;
import com.shalhlad.product_delivery_service.repository.DepartmentRepository;
import com.shalhlad.product_delivery_service.repository.EmployeeRepository;
import com.shalhlad.product_delivery_service.repository.OrderHandlersRepository;
import com.shalhlad.product_delivery_service.repository.OrderRepository;
import com.shalhlad.product_delivery_service.repository.ProductRepository;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.OrderService;
import java.math.BigDecimal;
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
  private final OrderHandlersRepository orderHandlersRepository;
  private final CardRepository cardRepository;

  @Autowired
  public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
      DepartmentRepository departmentRepository, ProductRepository productRepository,
      EmployeeRepository employeeRepository, OrderHandlersRepository orderHandlersRepository,
      CardRepository cardRepository) {
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
    this.departmentRepository = departmentRepository;
    this.productRepository = productRepository;
    this.employeeRepository = employeeRepository;
    this.orderHandlersRepository = orderHandlersRepository;
    this.cardRepository = cardRepository;
  }

  private BigDecimal calculateDiscount(Integer numberOfOrders) {
    BigDecimal result;
    if (numberOfOrders < 5) {
      result = new BigDecimal("0.0");
    } else if (numberOfOrders < 10) {
      result = new BigDecimal("1.0");
    } else if (numberOfOrders < 15) {
      result = new BigDecimal("1.5");
    } else if (numberOfOrders < 25) {
      result = new BigDecimal("2.5");
    } else if (numberOfOrders < 40) {
      result = new BigDecimal("4.0");
    } else {
      result = new BigDecimal("5");
    }
    return result;
  }

  @Override
  public Order create(Principal principal, OrderCreationDto orderCreationDto) {
    String email = principal.getName();
    Department department = departmentRepository.findById(orderCreationDto.getDepartmentId())
        .orElseThrow(() -> new NotFoundException(
            "Department not found with id: " + orderCreationDto.getDepartmentId()));

    User user = userRepository.findByEmail(email).orElseThrow();
    if (orderRepository.existsByUserAndStageNotAndStageNot(user, Stage.CANCELED, Stage.GIVEN)) {
      throw new NoRightsException("User cannot have more than one active order");
    }

    BigDecimal discount = user.getCard().getDiscountInPercents();

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
        orderedProducts.put(product,
            OrderedProductDetails.of(product.getPrice(), quantity, discount));
      } else {
        throw new InvalidValueException(
            "Cannot order products because the quantity to order more products in warehouse");
      }
    });
    departmentRepository.save(department);

    Card card = user.getCard();
    card.setNumberOfOrders(card.getNumberOfOrders() + 1);
    card.setDiscountInPercents(calculateDiscount(card.getNumberOfOrders()));

    cardRepository.save(card);

    return orderRepository.save(
        Order.builder()
            .user(user)
            .department(department)
            .deliveryAddress(orderCreationDto.getDeliveryAddress())
            .orderedProducts(orderedProducts)
            .orderHandlers(orderHandlersRepository.save(new OrderHandlers()))
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
  public Iterable<Order> findAllInProcessingByPrincipal(Principal principal) {
    Employee employee = employeeRepository.findByEmail(principal.getName()).orElseThrow();
    return orderRepository.findAllByOrderHandlersCurrentHandler(employee);
  }

  @Override
  public Iterable<Order> findActiveOrdersByEmail(String email) {
    User user = userRepository.findByEmail(email).orElseThrow();
    return orderRepository.findAllByUserAndStageNotAndStageNot(user, Stage.GIVEN, Stage.CANCELED);
  }

  @Override
  public Page<Order> findAllProcessableOrders(Principal principal, Pageable pageable) {
    Employee employee = employeeRepository.findByEmail(principal.getName()).orElseThrow();
    Department department = employee.getDepartment();
    Page<Order> page;
    if (employee.getRole() == Role.ROLE_COLLECTOR) {
      page = orderRepository.findAllByDepartmentAndStage(department, Stage.NEW, pageable);
    } else if (employee.getRole() == Role.ROLE_COURIER) {
      page = orderRepository.findAllByDepartmentAndStageNotAndOrderHandlersCourier(department,
          Stage.CANCELED, employee, pageable);
    } else {
      throw new NoRightsException("Only collector and courier can handle orders");
    }
    return page;
  }

  @Override
  public Order applyOperation(Principal principal, Long orderId, OrderOperations operation) {
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
        Map<Stage, Date> stageHistory = order.getStageHistory();
        stageHistory.put(Stage.CANCELED, new Date());

        Card card = user.getCard();
        card.setNumberOfOrders(card.getNumberOfOrders() - 1);
        card.setDiscountInPercents(calculateDiscount(card.getNumberOfOrders()));
        cardRepository.save(card);
      }
      case NEXT -> {
        Role userRole = user.getRole();
        if (userRole != Role.ROLE_COLLECTOR && userRole != Role.ROLE_COURIER) {
          throw new NoRightsException("Only collector and courier can manage order stage");
        }

        Employee employee = employeeRepository.findById(user.getId()).orElseThrow();
        if (!employee.getDepartment().equals(order.getDepartment())) {
          throw new NoRightsException("Employee can process orders only of his department");
        }

        Stage currentStage = order.getStage();
        OrderHandlers orderHandlers = order.getOrderHandlers();

        if (currentStage.canBeChangedByCollector()) {
          if (userRole != Role.ROLE_COLLECTOR) {
            throw new NoRightsException("Order of current stage can be manager only by collector");
          }
          if (orderHandlers.getCollector() == null) {
            throw new InvalidValueException("First you need start order handling");
          } else if (!orderHandlers.getCollector().equals(employee)) {
            throw new NoRightsException(
                "Cannot change order stage because it handles by another collector");
          }
        } else if (currentStage.canBeChangedByCourier()) {
          if (userRole != Role.ROLE_COURIER) {
            throw new NoRightsException("Order of current stage can be manager only by courier");
          }
          if (orderHandlers.getCourier() == null) {
            throw new InvalidValueException("First you need start order handling");
          } else if (!orderHandlers.getCourier().equals(employee)) {
            throw new NoRightsException(
                "Cannot change order stage because it handles by another courier");
          }
        }

        try {
          order.setStage(currentStage.next());
        } catch (UnsupportedOperationException e) {
          throw new InvalidValueException(e.getMessage());
        }

        if (order.getStage() == Stage.COLLECTED ||
            order.getStage() == Stage.GIVEN) {
          orderHandlers.setCurrentHandler(null);
        }

        Map<Stage, Date> stageHistory = order.getStageHistory();
        stageHistory.put(order.getStage(), new Date());
      }
      case HANDLE -> {
        Role userRole = user.getRole();
        if (userRole != Role.ROLE_COLLECTOR && userRole != Role.ROLE_COURIER) {
          throw new NoRightsException("Only collector and courier can manage order stage");
        }

        Employee employee = employeeRepository.findById(user.getId()).orElseThrow();
        if (orderRepository.existsByOrderHandlersCurrentHandler(employee)) {
          throw new NoRightsException("You cannot handle more that 1 order at the same time");
        }

        if (!employee.getDepartment().equals(order.getDepartment())) {
          throw new NoRightsException("Employee can process orders only of his department");
        }

        Stage currentStage = order.getStage();
        OrderHandlers orderHandlers = order.getOrderHandlers();

        if (userRole == Role.ROLE_COLLECTOR) {
          if (currentStage != Stage.NEW) {
            throw new InvalidValueException(
                "Cannot handle order, because it canceled or already handled by another collector");
          }
          orderHandlers.setCollector(employee);
        } else {
          if (!currentStage.canBeChangedByCollector() && currentStage != Stage.COLLECTED) {
            throw new InvalidValueException(
                "Cannot handle order, because it canceled or already handled by another courier");
          }
          orderHandlers.setCourier(employee);
        }
        orderHandlers.setCurrentHandler(employee);
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

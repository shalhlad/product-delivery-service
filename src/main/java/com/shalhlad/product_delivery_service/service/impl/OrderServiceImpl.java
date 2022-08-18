package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.OrderCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.OrderOperations;
import com.shalhlad.product_delivery_service.dto.response.OrderDetailsResponse;
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
import com.shalhlad.product_delivery_service.mapper.OrderMapper;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;
  private final ProductRepository productRepository;
  private final EmployeeRepository employeeRepository;
  private final OrderHandlersRepository orderHandlersRepository;
  private final CardRepository cardRepository;
  private final OrderMapper orderMapper;

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
  public OrderDetailsResponse createOrder(Principal principal,
      OrderCreateRequest orderCreateRequest) {
    String email = principal.getName();
    Department department = departmentRepository.findById(orderCreateRequest.getDepartmentId())
        .orElseThrow(() -> new NotFoundException(
            "Department not found with id: " + orderCreateRequest.getDepartmentId()));

    User user = userRepository.findByEmail(email).orElseThrow();
    if (orderRepository.existsByUserAndStageNotAndStageNot(user, Stage.CANCELED, Stage.GIVEN)) {
      throw new NoRightsException("User cannot have more than one active order");
    }

    BigDecimal discount = user.getCard().getDiscountInPercents();

    Map<Product, Integer> productWarehouse = department.getProductWarehouse();
    Map<Product, OrderedProductDetails> orderedProducts = new HashMap<>();
    orderCreateRequest.getOrderedProducts().forEach((productId, quantity) -> {
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

    Order saved = orderRepository.save(
        Order.builder()
            .user(user)
            .department(department)
            .deliveryAddress(orderCreateRequest.getDeliveryAddress())
            .orderedProducts(orderedProducts)
            .orderHandlers(orderHandlersRepository.save(new OrderHandlers()))
            .stage(Stage.NEW)
            .stageHistory(Map.of(Stage.NEW, new Date()))
            .build()
    );
    return orderMapper.toDetailsResponse(saved);
  }

  @Override
  public OrderDetailsResponse findOrderById(Long orderId, Principal principal) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    if (user.getRole().isEmployee()) {
      Employee employee = employeeRepository.findById(user.getId()).orElseThrow();
      if (!employee.getDepartment().equals(order.getDepartment())) {
        throw new NoRightsException("Employee cannot get order of other departments");
      }
    } else if (user.getRole() != Role.ROLE_ADMIN && !order.getUser().equals(user)) {
      throw new NoRightsException("Only admin can get info about another user orders");
    }

    return orderMapper.toDetailsResponse(order);
  }

  @Override
  public OrderDetailsResponse applyOperationToOrder(Principal principal, Long orderId,
      OrderOperations operation) {
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

        if (order.getStage() == Stage.GIVEN) {
          orderHandlers.setCurrentHandler(null);
        } else if (order.getStage() == Stage.IN_DELIVERY) {
          orderHandlers.setCurrentHandler(employee);
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
          if (currentStage != Stage.APPROVED) {
            throw new InvalidValueException(
                "Collector can start handle only approved orders");
          }
          orderHandlers.setCollector(employee);
          orderHandlers.setCurrentHandler(employee);
        } else {
          if (currentStage != Stage.NEW) {
            throw new InvalidValueException(
                "Courier can start handle only new orders");
          }
          orderHandlers.setCourier(employee);
        }
      }

    }

    Order saved = orderRepository.save(order);
    return orderMapper.toDetailsResponse(saved);
  }

}

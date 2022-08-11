package com.shalhlad.product_delivery_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.shalhlad.product_delivery_service.dto.request.OrderCreationDto;
import com.shalhlad.product_delivery_service.dto.request.OrderOperations;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.order.Order;
import com.shalhlad.product_delivery_service.entity.order.OrderHandlers;
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
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class OrderServiceImplTest {

  @InjectMocks
  private OrderServiceImpl service;

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private DepartmentRepository departmentRepository;
  @Mock
  private ProductRepository productRepository;
  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private CardRepository cardRepository;
  @Mock
  private OrderHandlersRepository orderHandlersRepository;

  private Product firstProduct;
  private Product secondProduct;
  private Order order;
  private User firstUser;
  private User secondUser;
  private Department department;
  private Employee firstEmployee;
  private Principal principal;
  private Pageable pageable;
  private Page<Order> page;
  private Card card;
  private OrderHandlers orderHandlers;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this).close();

    firstProduct = new Product();
    firstProduct.setId(1L);
    firstProduct.setName("first");

    secondProduct = new Product();
    secondProduct.setId(2L);
    secondProduct.setName("second");

    Map<Product, Integer> productWarehouse = new HashMap<>();
    productWarehouse.put(firstProduct, 2);
    productWarehouse.put(secondProduct, 8);

    department = new Department();
    department.setId(1L);
    department.setAddress("test");
    department.setProductWarehouse(productWarehouse);

    firstUser = new User();
    firstUser.setId(1L);
    firstUser.setEmail("test");
    firstUser.setUserId("test");
    firstUser.setRole(Role.ROLE_CUSTOMER);

    secondUser = new User();
    secondUser.setId(2L);

    order = new Order();
    order.setId(1L);
    order.setStage(Stage.NEW);
    order.setDepartment(department);
    order.setDeliveryAddress("test");
    order.setStageHistory(new HashMap<>());

    orderHandlers = new OrderHandlers();
    orderHandlers.setId(1L);
    order.setOrderHandlers(orderHandlers);

    firstEmployee = new Employee();
    firstEmployee.setId(3L);
    firstEmployee.setUserId("test");
    firstEmployee.setRole(Role.ROLE_COLLECTOR);
    firstEmployee.setDepartment(department);

    principal = () -> "Test";
    pageable = PageRequest.of(0, 1);
    page = new PageImpl<>(List.of(order), pageable, 1);

    card = new Card();
    card.setId(1L);
    card.setNumberOfOrders(0);
    card.setDiscountInPercents(BigDecimal.ZERO);

    firstUser.setCard(card);
  }


  @Test
  public void create() {
    OrderCreationDto orderCreationDto = new OrderCreationDto();
    orderCreationDto.setOrderedProducts(Map.of(
        firstProduct.getId(), 2,
        secondProduct.getId(), 5
    ));
    orderCreationDto.setDepartmentId(department.getId());
    orderCreationDto.setDeliveryAddress("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
    when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(cardRepository.save(any(Card.class))).thenReturn(card);
    when(orderHandlersRepository.save(any(OrderHandlers.class))).thenReturn(orderHandlers);

    Order actual = service.create(principal, orderCreationDto);
    assertThat(actual).isEqualTo(order);
  }

  @Test
  public void nonExistentDepartment_create_NotFoundException() {
    OrderCreationDto orderCreationDto = new OrderCreationDto();
    orderCreationDto.setDepartmentId(department.getId());

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.create(principal, orderCreationDto));
  }

  @Test
  public void orderedProductQuantityMoreThanInWarehouse_create_InvalidValueException() {
    OrderCreationDto orderCreationDto = new OrderCreationDto();
    orderCreationDto.setOrderedProducts(Map.of(
        firstProduct.getId(), 3,
        secondProduct.getId(), 5
    ));
    orderCreationDto.setDepartmentId(department.getId());
    orderCreationDto.setDeliveryAddress("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
    when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));

    assertThatExceptionOfType(InvalidValueException.class).isThrownBy(
        () -> service.create(principal, orderCreationDto));
  }

  @Test
  public void orderedProductQuantityLessThatZero_create_InvalidValueException() {
    OrderCreationDto orderCreationDto = new OrderCreationDto();
    orderCreationDto.setOrderedProducts(Map.of(
        firstProduct.getId(), -1,
        secondProduct.getId(), 5
    ));
    orderCreationDto.setDepartmentId(department.getId());
    orderCreationDto.setDeliveryAddress("test");

    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
    when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));

    assertThatExceptionOfType(InvalidValueException.class).isThrownBy(
        () -> service.create(principal, orderCreationDto));
  }

  @Test
  public void findById() {
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

    Order actual = service.findById(1L);
    assertThat(actual).isEqualTo(order);
  }

  @Test
  public void findById_NotFoundException() {
    when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> service.findById(1L));
  }

  @Test
  public void findAllByUserId() {
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findAllByUser(any(User.class), any(Pageable.class))).thenReturn(page);

    Page<Order> actual = service.findAllByUserId("test", pageable);
    assertThat(actual).isEqualTo(page);
  }

  @Test
  public void nonExistentUser_findAllByUserId_NotFoundException() {
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.findAllByUserId("test", pageable));
  }

  @Test
  public void findAllByEmail() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findAllByUser(any(User.class), any(Pageable.class))).thenReturn(page);

    Page<Order> actual = service.findAllByEmail("test", pageable);
    assertThat(actual).isEqualTo(page);
  }

  @Test
  public void nonExistentUser_findAllByEmail_NotFoundException() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.findAllByEmail("test", pageable));
  }

  @Test
  public void findAllByDepartmentAndStage() {
    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(firstEmployee));
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(orderRepository.findAllByDepartmentAndStage(any(Department.class), any(Stage.class),
        any(Pageable.class))).thenReturn(page);

    Page<Order> actual = service.findAllByDepartmentAndStage(principal, 1L, Stage.NEW, pageable);
    assertThat(actual).isEqualTo(page);
  }

  @Test
  public void nonExistentDepartment_findAllByDepartmentAndStage_NotFoundException() {
    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(firstEmployee));
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.findAllByDepartmentAndStage(principal, 1L, Stage.NEW, pageable));
  }

  @Test
  public void orderCreatedInAnotherDepartment_findAllByDepartmentAndStage_NoRightsException() {
    Department anotherDepartment = new Department();
    anotherDepartment.setId(2L);
    firstEmployee.setDepartment(anotherDepartment);
    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(firstEmployee));
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.findAllByDepartmentAndStage(principal, 1L, Stage.NEW, pageable));
  }

  @Test
  public void cancelOperation_changeStage() {
    order.setStage(Stage.NEW);
    order.setUser(firstUser);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    Order actual = service.applyOperation(principal, 1L, OrderOperations.CANCEL);
    assertThat(actual).isEqualTo(order);
  }

  @Test
  public void cancelOperationOnNonNewStageOrder_changeStage_NoRightsException() {
    order.setStage(Stage.COLLECTED);
    order.setUser(firstUser);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.applyOperation(principal, 1L, OrderOperations.CANCEL));
  }

  @Test
  public void cancelOperationOnAnotherUserOrder_changeStage_NoRightsException() {
    order.setStage(Stage.NEW);
    order.setUser(secondUser);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.applyOperation(principal, 1L, OrderOperations.CANCEL));
  }

  @Test
  public void nextOperationByUnresolvedRole_changeStage_NoRightsException() {
    firstUser.setRole(Role.ROLE_DEPARTMENT_HEAD);
    firstEmployee.setRole(Role.ROLE_DEPARTMENT_HEAD);
    order.setStage(Stage.NEW);
    order.setDepartment(department);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.applyOperation(principal, 1L, OrderOperations.NEXT));
  }

  @Test
  public void nextOperationByEmployeeOfAnotherDepartment_changeStage_NoRightsException() {
    firstUser.setRole(Role.ROLE_COURIER);
    firstEmployee.setRole(Role.ROLE_COURIER);
    Department anotherDepartment = new Department();
    anotherDepartment.setId(9L);
    firstEmployee.setDepartment(anotherDepartment);
    order.setStage(Stage.COLLECTED);
    order.setDepartment(department);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.applyOperation(principal, 1L, OrderOperations.NEXT));
  }

  @Test
  public void nextOperationOnOrderThatCannotBeManagedByCollector_changeStage_NoRightsException() {
    firstUser.setRole(Role.ROLE_COLLECTOR);
    firstEmployee.setRole(Role.ROLE_COLLECTOR);
    order.setStage(Stage.IN_DELIVERY);
    order.setDepartment(department);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.applyOperation(principal, 1L, OrderOperations.NEXT));
  }

  @Test
  public void nextOperationOnOrderThatCannotBeManagerByCourier_changeStage_NoRightsException() {
    firstUser.setRole(Role.ROLE_COURIER);
    firstEmployee.setRole(Role.ROLE_COURIER);
    order.setStage(Stage.APPROVED);
    order.setDepartment(department);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(
        () -> service.applyOperation(principal, 1L, OrderOperations.NEXT));
  }

  @Test
  public void nextOperationByCollector_changeStage() {
    firstUser.setRole(Role.ROLE_COLLECTOR);
    firstEmployee.setRole(Role.ROLE_COLLECTOR);
    order.setStage(Stage.NEW);
    order.setDepartment(department);

    orderHandlers.setCurrentHandler(firstEmployee);
    orderHandlers.setCollector(firstEmployee);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(cardRepository.save(any(Card.class))).thenReturn(card);
    when(orderHandlersRepository.save(any(OrderHandlers.class))).thenReturn(orderHandlers);

    Order actual = service.applyOperation(principal, 1L, OrderOperations.NEXT);
    assertThat(actual).isEqualTo(order);
  }

  @Test
  public void nextOperationByCourier_changeStage() {
    firstUser.setRole(Role.ROLE_COURIER);
    firstEmployee.setRole(Role.ROLE_COURIER);
    order.setStage(Stage.COLLECTED);
    order.setDepartment(department);

    orderHandlers.setCurrentHandler(firstEmployee);
    orderHandlers.setCourier(firstEmployee);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(firstUser));
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(firstEmployee));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(cardRepository.save(any(Card.class))).thenReturn(card);
    when(orderHandlersRepository.save(any(OrderHandlers.class))).thenReturn(orderHandlers);

    Order actual = service.applyOperation(principal, 1L, OrderOperations.NEXT);
    assertThat(actual).isEqualTo(order);
  }


}

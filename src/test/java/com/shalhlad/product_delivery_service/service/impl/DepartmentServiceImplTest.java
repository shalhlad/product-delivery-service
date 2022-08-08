package com.shalhlad.product_delivery_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.shalhlad.product_delivery_service.dto.request.DepartmentCreationDto;
import com.shalhlad.product_delivery_service.dto.request.DepartmentUpdateDto;
import com.shalhlad.product_delivery_service.dto.request.ProductQuantityToChangeDto;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.product.Product;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.DepartmentMapper;
import com.shalhlad.product_delivery_service.repository.DepartmentRepository;
import com.shalhlad.product_delivery_service.repository.EmployeeRepository;
import com.shalhlad.product_delivery_service.repository.ProductRepository;
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

public class DepartmentServiceImplTest {

  @InjectMocks
  private DepartmentServiceImpl service;

  @Mock
  private DepartmentRepository departmentRepository;
  @Mock
  private ProductRepository productRepository;
  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private DepartmentMapper departmentMapper;


  private Department department;
  private Product firstProduct;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this).close();

    firstProduct = new Product();
    firstProduct.setId(1L);
    department = new Department();
    department.setId(1L);
    department.setAddress("test");
  }

  @Test
  public void create() {
    when(departmentRepository.save(any(Department.class))).thenReturn(department);

    Department actual = service.create(new DepartmentCreationDto());
    assertThat(actual).isEqualTo(department);
  }

  @Test
  public void findById() {
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));

    Department actual = service.findById(1L);
    assertThat(actual).isEqualTo(department);
  }

  @Test
  public void findById_NotFoundException() {
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> service.findById(1L));
  }

  @Test
  public void findAll() {
    when(departmentRepository.findAll()).thenReturn(List.of(department));

    assertThat(service.findAll()).isEqualTo(List.of(department));
  }

  @Test
  public void findByPrincipal() {
    Principal principal = () -> "test";
    Employee employee = new Employee();
    employee.setDepartment(department);

    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(employee));

    Department actual = service.findByPrincipal(principal);
    assertThat(actual).isEqualTo(department);
  }

  @Test
  public void update() {
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    doNothing().when(departmentMapper)
        .update(any(Department.class), any(DepartmentUpdateDto.class));
    when(departmentRepository.save(any(Department.class))).thenReturn(department);

    Department actual = service.update(1L, new DepartmentUpdateDto());
    assertThat(actual).isEqualTo(department);
  }

  @Test
  public void update_NotFoundException() {
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.update(1L, new DepartmentUpdateDto()));
  }

  @Test
  public void changeProductQuantityIncrease() {
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(firstProduct));
    when(departmentRepository.save(any(Department.class))).thenReturn(department);

    Integer defaultQuantity = 2;
    Map<Product, Integer> warehouse = new HashMap<>();
    warehouse.put(firstProduct, defaultQuantity);
    department.setProductWarehouse(warehouse);

    ProductQuantityToChangeDto productQuantityToChangeDto = new ProductQuantityToChangeDto();
    productQuantityToChangeDto.setProductId(1L);
    productQuantityToChangeDto.setAction("increase");
    productQuantityToChangeDto.setNumberOfProducts(1);

    Integer expected = defaultQuantity + productQuantityToChangeDto.getNumberOfProducts();
    Integer actual = service.changeProductQuantity(1L, productQuantityToChangeDto)
        .getProductWarehouse()
        .get(firstProduct);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void changeProductQuantityReduce() {
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(firstProduct));
    when(departmentRepository.save(any(Department.class))).thenReturn(department);

    Integer defaultQuantity = 2;
    Map<Product, Integer> warehouse = new HashMap<>();
    warehouse.put(firstProduct, defaultQuantity);
    department.setProductWarehouse(warehouse);

    ProductQuantityToChangeDto productQuantityToChangeDto = new ProductQuantityToChangeDto();
    productQuantityToChangeDto.setProductId(1L);
    productQuantityToChangeDto.setAction("reduce");
    productQuantityToChangeDto.setNumberOfProducts(1);

    Integer expected = defaultQuantity - productQuantityToChangeDto.getNumberOfProducts();
    Integer actual = service.changeProductQuantity(1L, productQuantityToChangeDto)
        .getProductWarehouse()
        .get(firstProduct);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void changeProductQuantityReduce_InvalidValueException() {
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(firstProduct));
    when(departmentRepository.save(any(Department.class))).thenReturn(department);

    Integer defaultQuantity = 2;
    Map<Product, Integer> warehouse = new HashMap<>();
    warehouse.put(firstProduct, defaultQuantity);
    department.setProductWarehouse(warehouse);

    ProductQuantityToChangeDto productQuantityToChangeDto = new ProductQuantityToChangeDto();
    productQuantityToChangeDto.setProductId(1L);
    productQuantityToChangeDto.setAction("reduce");
    productQuantityToChangeDto.setNumberOfProducts(3);

    assertThatExceptionOfType(InvalidValueException.class).isThrownBy(
        () -> service.changeProductQuantity(1L, productQuantityToChangeDto));
  }

  @Test
  public void changeProductQuantity_NotFoundException() {
    when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.changeProductQuantity(1L, new ProductQuantityToChangeDto()));
  }


}

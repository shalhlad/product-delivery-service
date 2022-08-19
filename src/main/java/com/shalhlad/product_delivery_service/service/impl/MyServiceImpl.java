package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.EmployeeRoles;
import com.shalhlad.product_delivery_service.dto.request.ProcessingOrderCharacteristics;
import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateRequest;
import com.shalhlad.product_delivery_service.dto.response.DepartmentDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.EmployeeDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.OrderDetailsResponse;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.order.Order;
import com.shalhlad.product_delivery_service.entity.order.Stage;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.exception.NoRightsException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.DepartmentMapper;
import com.shalhlad.product_delivery_service.mapper.EmployeeMapper;
import com.shalhlad.product_delivery_service.mapper.OrderMapper;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.repository.EmployeeRepository;
import com.shalhlad.product_delivery_service.repository.OrderRepository;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.MyService;
import java.security.Principal;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MyServiceImpl implements MyService {

  private final UserMapper userMapper;
  private final EmployeeMapper employeeMapper;
  private final DepartmentMapper departmentMapper;
  private final OrderMapper orderMapper;
  private final UserRepository userRepository;
  private final EmployeeRepository employeeRepository;
  private final OrderRepository orderRepository;

  @Override
  public UserDetailsResponse getDetailsOfAuthorizedUser(Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    if (user.getRole().isEmployee()) {
      user = employeeRepository.findById(user.getId()).orElseThrow();
    }
    return mapUser(user);
  }

  @Override
  public UserDetailsResponse updateDetailsOfAuthorizedUser(Principal principal,
      UserDetailsUpdateRequest userDetailsUpdateRequest) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    User saved;
    if (user.getRole().isEmployee()) {
      user = employeeRepository.findById(user.getId()).orElseThrow();
      userMapper.update(user, userDetailsUpdateRequest);
      saved = employeeRepository.save((Employee) user);
    } else {
      userMapper.update(user, userDetailsUpdateRequest);
      saved = userRepository.save(user);
    }
    return mapUser(saved);
  }

  @Override
  public DepartmentDetailsResponse getDepartmentOfAuthorizedUser(Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    if (!user.getRole().isEmployee()) {
      throw new InvalidValueException("Only employee can have department");
    }

    Employee employee = employeeRepository.findById(user.getId()).orElseThrow();
    Department department = employee.getDepartment();
    return departmentMapper.toDetailsResponse(department);
  }

  @Override
  public Page<OrderDetailsResponse> getOrdersOfAuthorizedUserDepartment(Principal principal,
      ProcessingOrderCharacteristics orderCharacteristic, Pageable pageable) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    if (!user.getRole().isEmployee()) {
      throw new InvalidValueException("Only employee can have department");
    }
    Employee employee = employeeRepository.findById(user.getId()).orElseThrow();
    Department department = employee.getDepartment();

    Role role = employee.getRole();
    if (role != Role.ROLE_COLLECTOR && role != Role.ROLE_COURIER) {
      throw new NoRightsException(
          "Only collector and courier can get orders of department to process");
    }

    Page<Order> orders = switch (orderCharacteristic) {
      case PROCESSABLE -> {
        Stage stage = (role == Role.ROLE_COURIER) ? Stage.NEW : Stage.APPROVED;
        yield orderRepository.findAllByDepartmentAndStage(department, stage, pageable);
      }
      case IN_PROCESSING ->
          orderRepository.findAllByOrderHandlersCurrentHandler(employee, pageable);
    };

    return orderMapper.toDetailsResponse(orders);
  }

  @Override
  public Page<OrderDetailsResponse> getOrdersOfAuthorizedUser(Principal principal,
      Pageable pageable) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    Page<Order> orders = orderRepository.findAllByUser(user, pageable);
    return orderMapper.toDetailsResponse(orders);
  }

  @Override
  public OrderDetailsResponse getOrderOfAuthorizedUserByOrderId(Principal principal, Long orderId) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    return orderRepository.findByIdAndUser(orderId, user)
        .map(orderMapper::toDetailsResponse)
        .orElseThrow(
            () -> new NotFoundException("Order of authorized user not found with id: " + orderId));
  }

  @Override
  public Iterable<EmployeeDetailsResponse> getEmployeesOfAuthorizedUserDepartment(
      Principal principal, EmployeeRoles employeeRole) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    if (!user.getRole().isEmployee()) {
      throw new InvalidValueException("Only employee can have department");
    }
    Employee employee = employeeRepository.findById(user.getId()).orElseThrow();
    Department department = employee.getDepartment();

    Iterable<Employee> employees = employeeRole == null ?
        employeeRepository.findAllByDepartment(department) :
        employeeRepository.findAllByDepartmentAndRole(department, employeeRole.toEntityRole());
    return employeeMapper.toDetailsResponse(employees);
  }


  private UserDetailsResponse mapUser(User user) {
    return user.getRole().isEmployee() ?
        employeeMapper.toDetailsResponse((Employee) user) :
        userMapper.toDetailsResponse(user);
  }
}

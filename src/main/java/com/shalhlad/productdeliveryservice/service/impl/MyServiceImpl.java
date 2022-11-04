package com.shalhlad.productdeliveryservice.service.impl;

import com.shalhlad.productdeliveryservice.dto.request.EmployeeRoles;
import com.shalhlad.productdeliveryservice.dto.request.ProcessingOrderCharacteristics;
import com.shalhlad.productdeliveryservice.dto.request.UserDetailsUpdateRequest;
import com.shalhlad.productdeliveryservice.dto.response.DepartmentDetailsResponse;
import com.shalhlad.productdeliveryservice.dto.response.EmployeeDetailsResponse;
import com.shalhlad.productdeliveryservice.dto.response.OrderDetailsResponse;
import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;
import com.shalhlad.productdeliveryservice.entity.department.Department;
import com.shalhlad.productdeliveryservice.entity.order.Order;
import com.shalhlad.productdeliveryservice.entity.order.Stage;
import com.shalhlad.productdeliveryservice.entity.user.Employee;
import com.shalhlad.productdeliveryservice.entity.user.Role;
import com.shalhlad.productdeliveryservice.entity.user.User;
import com.shalhlad.productdeliveryservice.exception.InvalidValueException;
import com.shalhlad.productdeliveryservice.exception.NoRightsException;
import com.shalhlad.productdeliveryservice.exception.NotFoundException;
import com.shalhlad.productdeliveryservice.mapper.DepartmentMapper;
import com.shalhlad.productdeliveryservice.mapper.EmployeeMapper;
import com.shalhlad.productdeliveryservice.mapper.OrderMapper;
import com.shalhlad.productdeliveryservice.mapper.UserMapper;
import com.shalhlad.productdeliveryservice.repository.EmployeeRepository;
import com.shalhlad.productdeliveryservice.repository.OrderRepository;
import com.shalhlad.productdeliveryservice.repository.UserRepository;
import com.shalhlad.productdeliveryservice.service.MyService;
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
    return userMapper.toDetailsResponse(user);
  }

  @Override
  public UserDetailsResponse updateDetailsOfAuthorizedUser(Principal principal,
      UserDetailsUpdateRequest userDetailsUpdateRequest) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    userMapper.update(user, userDetailsUpdateRequest);

    User saved = userRepository.save(user);
    return userMapper.toDetailsResponse(saved);
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
  public Page<OrderDetailsResponse> getOrdersOfAuthorizedUser(Principal principal, boolean active,
      Pageable pageable) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    Page<Order> orders = active ?
        orderRepository.findAllByUserAndStageNotAndStageNot(user, Stage.GIVEN, Stage.CANCELED,
            pageable) :
        orderRepository.findAllByUser(user, pageable);
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
}

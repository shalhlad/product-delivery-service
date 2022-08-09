package com.shalhlad.product_delivery_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateDto;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.repository.EmployeeRepository;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import java.security.Principal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProfileServiceImplTest {

  @InjectMocks
  private ProfileServiceImpl service;

  @Mock
  private UserMapper userMapper;
  @Mock
  private UserRepository userRepository;
  @Mock
  private EmployeeRepository employeeRepository;

  private Principal principal;
  private User user;
  private Employee employee;


  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this).close();

    principal = () -> "test";
    user = new User();
    user.setId(1L);
    user.setRole(Role.ROLE_CUSTOMER);
    user.setEmail("test");

    employee = new Employee();
    employee.setId(user.getId());
    employee.setRole(Role.ROLE_COLLECTOR);
    employee.setEmail(user.getEmail());
  }

  @Test
  public void getDetailsFromPrincipal_forUser() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

    User actual = service.getDetailsFromPrincipal(principal);
    assertThat(actual).isEqualTo(user);
  }

  @Test
  public void getDetailsFromPrincipal_forEmployee() {
    user.setRole(employee.getRole());
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));

    User actual = service.getDetailsFromPrincipal(principal);
    assertThat(actual).isEqualTo(employee).isInstanceOf(Employee.class);
  }

  @Test
  public void updateDetailsByPrincipal_forUser() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    doNothing().when(userMapper).update(any(User.class), any(UserDetailsUpdateDto.class));
    when(userRepository.save(any(User.class))).thenReturn(user);

    User actual = service.updateDetailsByPrincipal(principal, new UserDetailsUpdateDto());
    assertThat(actual).isEqualTo(user);
  }

  @Test
  public void updateDetailsByPrincipal_forEmployee() {
    user.setRole(employee.getRole());

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
    doNothing().when(userMapper).update(any(User.class), any(UserDetailsUpdateDto.class));
    when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

    User actual = service.updateDetailsByPrincipal(principal, new UserDetailsUpdateDto());
    assertThat(actual).isEqualTo(employee).isInstanceOf(Employee.class);
  }


}

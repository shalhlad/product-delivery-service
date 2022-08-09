package com.shalhlad.product_delivery_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AdminServiceImplTest {

  @InjectMocks
  private AdminServiceImpl service;

  @Mock
  private UserRepository userRepository;

  private User admin;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this).close();

    admin = new User();
    admin.setId(1L);
    admin.setRole(Role.ROLE_ADMIN);
  }

  @Test
  public void findAll() {
    when(userRepository.findAllByRole(Role.ROLE_ADMIN)).thenReturn(List.of(admin));

    assertThat(service.findAll()).isEqualTo(List.of(admin));
  }

  @Test
  public void findByUserId() {
    when(userRepository.findByUserIdAndRole(anyString(), any(Role.class))).thenReturn(
        Optional.of(admin));

    User actual = service.findByUserId("test");
    assertThat(actual).isEqualTo(admin);
  }

  @Test
  public void findByUserId_NotFoundException() {
    when(userRepository.findByUserIdAndRole(anyString(), any(Role.class))).thenReturn(
        Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.findByUserId("test"));
  }


}

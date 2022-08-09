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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserServiceImplTest {

  @InjectMocks
  private UserServiceImpl service;

  @Mock
  private UserRepository userRepository;


  private User user;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this).close();

    user = new User();
    user.setId(1L);
    user.setEmail("test");
    user.setEncryptedPassword("test");
    user.setRole(Role.ROLE_CUSTOMER);
    user.setUserId("test");
  }


  @Test
  public void findAll() {
    Pageable pageable = PageRequest.of(0, 1);
    Page<User> page = new PageImpl<>(List.of(user), pageable, 1);

    when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

    Page<User> actual = service.findAll(pageable);
    assertThat(actual).isEqualTo(actual);
  }

  @Test
  public void findByUserId() {
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));

    User actual = service.findByUserId("test");
    assertThat(actual).isEqualTo(user);
  }

  @Test
  public void findByUserId_NotFoundException() {
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.findByUserId("test"));
  }

  @Test
  public void findByEmail() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

    User actual = service.findByEmail("test");
    assertThat(actual).isEqualTo(user);
  }

  @Test
  public void findByEmail_NotFoundException() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.findByEmail("test"));
  }

  @Test
  public void loadByUsername() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

    UserDetails actual = service.loadUserByUsername("test");
    UserDetails expected = new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getEncryptedPassword(),
        List.of(user.getRole()));
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void loadByUsername_UsernameNotFoundException() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(
        () -> service.loadUserByUsername("test"));
  }


}

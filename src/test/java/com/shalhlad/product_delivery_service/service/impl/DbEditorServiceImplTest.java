package com.shalhlad.product_delivery_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.NoRightsException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DbEditorServiceImplTest {

  @InjectMocks
  private DbEditorServiceImpl service;

  @Mock
  private UserRepository userRepository;


  private User dbEditor;
  private User customer;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this).close();

    dbEditor = new User();
    dbEditor.setId(1L);
    dbEditor.setRole(Role.ROLE_DB_EDITOR);

    customer = new User();
    customer.setId(1L);
    customer.setRole(Role.ROLE_CUSTOMER);
  }

  @Test
  public void findAll() {
    when(userRepository.findAllByRole(Role.ROLE_DB_EDITOR)).thenReturn(List.of(dbEditor));

    assertThat(service.findAll()).isEqualTo(List.of(dbEditor));
  }

  @Test
  public void findByUserId() {
    when(userRepository.findByUserIdAndRole(anyString(), any(Role.class))).thenReturn(
        Optional.of(dbEditor));

    User actual = service.findByUserId("test");
    assertThat(actual).isEqualTo(dbEditor);
  }

  @Test
  public void findByUserId_NotFoundException() {
    when(userRepository.findByUserIdAndRole(anyString(), any(Role.class))).thenReturn(
        Optional.empty());

    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> service.findByUserId("test"));
  }

  @Test
  public void add() {
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(customer));
    when(userRepository.save(any(User.class))).thenReturn(dbEditor);

    User actual = service.add("test");
    assertThat(actual).isEqualTo(dbEditor);
  }

  @Test
  public void add_NoRightsException() {
    customer.setRole(Role.ROLE_COLLECTOR);
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(customer));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(() -> service.add("test"));
  }

  @Test
  public void remove() {
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(dbEditor));
    when(userRepository.save(any(User.class))).thenReturn(customer);

    User actual = service.remove("test");
    assertThat(actual).isEqualTo(customer);
  }

  @Test
  public void remove_NoRightsException() {
    when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(customer));

    assertThatExceptionOfType(NoRightsException.class).isThrownBy(() -> service.remove("test"));
  }


}

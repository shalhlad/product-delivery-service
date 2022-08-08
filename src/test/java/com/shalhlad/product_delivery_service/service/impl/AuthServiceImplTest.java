package com.shalhlad.product_delivery_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.shalhlad.product_delivery_service.dto.request.SignUpDetailsDto;
import com.shalhlad.product_delivery_service.dto.request.UserLoginDetailsDto;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.InvalidLoginOrPasswordException;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.security.jwt.JwtProvider;
import com.shalhlad.product_delivery_service.util.Utils;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceImplTest {

  @InjectMocks
  private AuthServiceImpl service;

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private JwtProvider jwtProvider;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this).close();
  }

  @Test
  public void signUp() {
    User user = new User();
    user.setId(1L);

    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByUserId(anyString())).thenReturn(false);
    Mockito.mockStatic(Utils.class).when(() -> Utils.generateUserId(anyInt())).thenReturn("test");
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(passwordEncoder.encode(anyString())).thenReturn("test");

    SignUpDetailsDto signUpDetailsDto = new SignUpDetailsDto();
    signUpDetailsDto.setEmail("test");
    signUpDetailsDto.setFirstName("test");
    signUpDetailsDto.setPassword("test");

    User actual = service.signUp(new SignUpDetailsDto());
    assertThat(actual).isEqualTo(user);
  }

  @Test
  public void signUp_InvalidValueException() {
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    SignUpDetailsDto signUpDetailsDto = new SignUpDetailsDto();
    signUpDetailsDto.setEmail("test");

    assertThatExceptionOfType(InvalidValueException.class).isThrownBy(
        () -> service.signUp(signUpDetailsDto));
  }

  @Test
  public void signIn() {
    UserLoginDetailsDto userLoginDetailsDto = new UserLoginDetailsDto();
    userLoginDetailsDto.setEmail("test");
    userLoginDetailsDto.setPassword("test");

    User user = new User();
    user.setEncryptedPassword("test");

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtProvider.generateToken(anyString())).thenReturn("test");

    assertThatNoException().isThrownBy(() -> service.signIn(userLoginDetailsDto));
  }

  @Test
  public void signIn_InvalidLoginOrPasswordException() {
    UserLoginDetailsDto userLoginDetailsDto = new UserLoginDetailsDto();
    userLoginDetailsDto.setEmail("test");
    userLoginDetailsDto.setPassword("test");

    User user = new User();
    user.setEncryptedPassword("test");

    when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    assertThatExceptionOfType(InvalidLoginOrPasswordException.class).isThrownBy(
        () -> service.signIn(userLoginDetailsDto));
  }

}

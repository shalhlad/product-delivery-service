package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.SignUpDetailsDto;
import com.shalhlad.product_delivery_service.dto.request.UserLoginDetailsDto;
import com.shalhlad.product_delivery_service.dto.response.AccessTokenResponse;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsDto;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.service.AuthService;
import com.shalhlad.product_delivery_service.util.Utils;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService service;
  private final UserMapper mapper;

  public AuthController(AuthService service, UserMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @PostMapping("/sign-up")
  @ResponseStatus(HttpStatus.CREATED)
  public UserDetailsDto signUp(
      @RequestBody @Valid SignUpDetailsDto signUpDetailsDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return mapper.toUserDetailsDto(service.signUp(signUpDetailsDto));
  }

  @PostMapping("/sign-in")
  public AccessTokenResponse signIn(
      @RequestBody @Valid UserLoginDetailsDto userLoginDetailsDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return AccessTokenResponse.of(service.signIn(userLoginDetailsDto));
  }

}

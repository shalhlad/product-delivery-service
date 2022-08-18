package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.SignUpRequest;
import com.shalhlad.product_delivery_service.dto.request.UserLoginRequest;
import com.shalhlad.product_delivery_service.dto.response.AccessTokenResponse;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.service.AuthService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService service;

  @PostMapping("/sign-up")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "signUp", notes = "Sign up user")
  public UserDetailsResponse signUp(
      @RequestBody @Valid SignUpRequest signUpRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.signUp(signUpRequest);
  }

  @PostMapping("/sign-in")
  @ApiOperation(value = "signIn", notes = "Returns auth token")
  public AccessTokenResponse signIn(
      @RequestBody @Valid UserLoginRequest userLoginRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.signIn(userLoginRequest);
  }

}

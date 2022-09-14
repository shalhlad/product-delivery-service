package com.shalhlad.productdeliveryservice.controller;

import com.shalhlad.productdeliveryservice.dto.request.SignUpRequest;
import com.shalhlad.productdeliveryservice.dto.request.UserLoginRequest;
import com.shalhlad.productdeliveryservice.dto.response.AccessTokenResponse;
import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;
import com.shalhlad.productdeliveryservice.service.AuthService;
import com.shalhlad.productdeliveryservice.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPrefix}/auth")
@RequiredArgsConstructor
@Tag(name = "auth")
public class AuthController {

  private final AuthService service;

  @PostMapping(value = "/sign-up",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "signUp", description = "Sign up user")
  public UserDetailsResponse signUp(
      @RequestBody @Valid SignUpRequest signUpRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.signUp(signUpRequest);
  }

  @PostMapping(value = "/sign-in",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "signIn", description = "Returns auth token")
  public AccessTokenResponse signIn(
      @RequestBody @Valid UserLoginRequest userLoginRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.signIn(userLoginRequest);
  }

}

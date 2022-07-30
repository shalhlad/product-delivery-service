package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.SignUpDetailsDto;
import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateDto;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsDto;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.service.UserService;
import com.shalhlad.product_delivery_service.util.Utils;
import java.security.Principal;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService service;
  private final UserMapper mapper;

  @Autowired
  public UserController(UserService service, UserMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserDetailsDto signUp(
      @RequestBody @Valid SignUpDetailsDto signUpDetailsDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return mapper.toUserDetailsDto(service.signUp(signUpDetailsDto));
  }

  @PatchMapping("/{userId}")
  public UserDetailsDto updateDetails(
      Principal principal,
      @PathVariable String userId,
      @RequestBody @Valid UserDetailsUpdateDto userDetailsUpdateDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return mapper.toUserDetailsDto(service.update(principal, userId, userDetailsUpdateDto));
  }

  @GetMapping
  public Iterable<UserDetailsDto> getUsers() {
    return mapper.toUserDetailsDto(service.findAll());
  }

  @GetMapping("/{userId}")
  public UserDetailsDto getByUserId(@PathVariable String userId) {
    return mapper.toUserDetailsDto(service.findByUserId(userId));
  }
}

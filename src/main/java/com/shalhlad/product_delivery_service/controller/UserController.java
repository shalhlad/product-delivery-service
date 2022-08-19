package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Api(tags = "users")
public class UserController {

  private final UserService service;

  @GetMapping
  @ApiOperation(value = "getAllUsers", notes = "Returns all users")
  public Page<UserDetailsResponse> getUsers(
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "15") int size
  ) {
    return service.findAllUsers(PageRequest.of(page, size));
  }

  @GetMapping("/{userId}")
  @ApiOperation(value = "getUserByUserId", notes = "Returns user by userId")
  public UserDetailsResponse getByUserId(@PathVariable String userId) {
    return service.findUserByUserId(userId);
  }

}

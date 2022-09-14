package com.shalhlad.productdeliveryservice.controller;

import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;
import com.shalhlad.productdeliveryservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPrefix}/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "users")
public class UserController {

  private final UserService service;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getAllUsers", description = "Returns all users")
  public Page<UserDetailsResponse> getUsers(
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "15") int size
  ) {
    return service.findAllUsers(PageRequest.of(page, size));
  }

  @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getUserByUserId", description = "Returns user by userId")
  public UserDetailsResponse getByUserId(@PathVariable String userId) {
    return service.findUserByUserId(userId);
  }

}

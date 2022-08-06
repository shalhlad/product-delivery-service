package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsDto;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.service.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
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
@PreAuthorize("isAuthenticated()")
public class UserController {

  private final UserService service;
  private final UserMapper mapper;

  @Autowired
  public UserController(UserService service, UserMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Page<UserDetailsDto> getUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size
  ) {
    return mapper.toDetailsDto(service.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/me")
  public UserDetailsDto getByPrincipal(Principal principal) {
    return mapper.toDetailsDto(service.findByEmail(principal.getName()));
  }

  @GetMapping("/{userId}")
  public UserDetailsDto getByUserId(@PathVariable String userId) {
    return mapper.toDetailsDto(service.findByUserId(userId));
  }

}

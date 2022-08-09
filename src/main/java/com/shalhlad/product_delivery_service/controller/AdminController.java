package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsDto;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admins")
public class AdminController {

  private final AdminService service;
  private final UserMapper mapper;

  @Autowired
  public AdminController(AdminService service, UserMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping
  public Iterable<UserDetailsDto> getAll() {
    return mapper.toDetailsDto(service.findAll());
  }

  @GetMapping("/{userId}")
  public UserDetailsDto getByUserId(@PathVariable String userId) {
    return mapper.toDetailsDto(service.findByUserId(userId));
  }
}

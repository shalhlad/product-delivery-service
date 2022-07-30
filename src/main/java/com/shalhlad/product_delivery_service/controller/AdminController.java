package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.entity.user.User;
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

  @Autowired
  public AdminController(AdminService service) {
    this.service = service;
  }

  @GetMapping
  public Iterable<User> getAll() {
    return service.findAll();
  }

  @GetMapping("/{userId}")
  public User getByUserId(@PathVariable String userId) {
    return service.findByUserId(userId);
  }
}

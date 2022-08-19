package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admins")
@RequiredArgsConstructor
@Api(tags = "admins")
public class AdminController {

  private final AdminService service;

  @GetMapping
  @ApiOperation(value = "getAllAdmins", notes = "Returns all admins")
  public Iterable<UserDetailsResponse> getAll() {
    return service.findAllAdmins();
  }

  @GetMapping("/{userId}")
  @ApiOperation(value = "getAdminByUserId", notes = "Returns admin by userId")
  public UserDetailsResponse getByUserId(@PathVariable String userId) {
    return service.findAdminByUserId(userId);
  }
}

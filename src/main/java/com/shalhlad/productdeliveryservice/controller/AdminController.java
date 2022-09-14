package com.shalhlad.productdeliveryservice.controller;

import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;
import com.shalhlad.productdeliveryservice.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("${apiPrefix}/admins")
@RequiredArgsConstructor
@Tag(name = "admins")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

  private final AdminService service;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getAllAdmins", description = "Returns all admins")
  public Iterable<UserDetailsResponse> getAll() {
    return service.findAllAdmins();
  }

  @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getAdminByUserId", description = "Returns admin by userId")
  public UserDetailsResponse getByUserId(@PathVariable String userId) {
    return service.findAdminByUserId(userId);
  }
}

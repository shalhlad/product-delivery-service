package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.service.DbEditorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${apiPrefix}/db-editors")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "db-editors")
@SecurityRequirement(name = "Bearer Authentication")
public class DbEditorController {

  private final DbEditorService service;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getAllDbEditors", description = "Returns all DB editors")
  public Iterable<UserDetailsResponse> getAll() {
    return service.findAllDbEditors();
  }

  @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getDbEditorByUserId", description = "Returns DB editor by userId")
  public UserDetailsResponse getByUserId(@PathVariable String userId) {
    return service.findDbEditorByUserId(userId);
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "addDbEditorByUserId", description = "Makes customer the DB editor by userId")
  public UserDetailsResponse add(@RequestParam String userId) {
    return service.addDbEditor(userId);
  }

  @DeleteMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "removeDbEditorByUserId", description = "Makes DB editor the customer by userId")
  public UserDetailsResponse remove(@PathVariable String userId) {
    return service.removeDbEditor(userId);
  }
}

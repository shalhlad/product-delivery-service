package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.service.DbEditorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/db-editors")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Api(tags = "db-editors")
public class DbEditorController {

  private final DbEditorService service;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "getAllDbEditors", notes = "Returns all DB editors")
  public Iterable<UserDetailsResponse> getAll() {
    return service.findAllDbEditors();
  }

  @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "getDbEditorByUserId", notes = "Returns DB editor by userId")
  public UserDetailsResponse getByUserId(@PathVariable String userId) {
    return service.findDbEditorByUserId(userId);
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "addDbEditorByUserId", notes = "Makes customer the DB editor by userId")
  public UserDetailsResponse add(@RequestParam String userId) {
    return service.addDbEditor(userId);
  }

  @DeleteMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "removeDbEditorByUserId", notes = "Makes DB editor the customer by userId")
  public UserDetailsResponse remove(@PathVariable String userId) {
    return service.removeDbEditor(userId);
  }
}

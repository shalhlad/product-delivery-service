package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsDto;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.service.DbEditorService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DbEditorController {

  private final DbEditorService dbEditorService;
  private final UserMapper mapper;

  @Autowired
  public DbEditorController(DbEditorService dbEditorService, UserMapper mapper) {
    this.dbEditorService = dbEditorService;
    this.mapper = mapper;
  }

  @GetMapping
  @ApiOperation(value = "getAllDbEditors", notes = "Returns all DB editors")
  public Iterable<UserDetailsDto> getAll() {
    return mapper.toDetailsDto(dbEditorService.findAll());
  }

  @GetMapping("/{userId}")
  @ApiOperation(value = "getDbEditorByUserId", notes = "Returns DB editor by userId")
  public UserDetailsDto getByUserId(@PathVariable String userId) {
    return mapper.toDetailsDto(dbEditorService.findByUserId(userId));
  }

  @PostMapping
  @ApiOperation(value = "addDbEditor", notes = "Makes customer the DB editor by userId")
  public UserDetailsDto add(@RequestParam String userId) {
    return mapper.toDetailsDto(dbEditorService.add(userId));
  }

  @DeleteMapping("/{userId}")
  @ApiOperation(value = "removeDbEditor", notes = "Makes DB editor the customer by userId")
  public UserDetailsDto remove(@PathVariable String userId) {
    return mapper.toDetailsDto(dbEditorService.remove(userId));
  }
}

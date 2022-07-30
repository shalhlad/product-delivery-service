package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsDto;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.service.DbEditorService;
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
  public Iterable<UserDetailsDto> getAll() {
    return mapper.toUserDetailsDto(dbEditorService.findAll());
  }

  @GetMapping("/{userId}")
  public UserDetailsDto getByUserId(@PathVariable String userId) {
    return mapper.toUserDetailsDto(dbEditorService.findByUserId(userId));
  }

  @PostMapping
  public UserDetailsDto add(@RequestParam String userId) {
    return mapper.toUserDetailsDto(dbEditorService.add(userId));
  }

  @DeleteMapping("/{userId}")
  public UserDetailsDto remove(@PathVariable String userId) {
    return mapper.toUserDetailsDto(dbEditorService.remove(userId));
  }
}

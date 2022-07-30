package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.entity.user.User;
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

  @Autowired
  public DbEditorController(DbEditorService dbEditorService) {
    this.dbEditorService = dbEditorService;
  }

  @GetMapping
  public Iterable<User> getAll() {
    return dbEditorService.findAll();
  }

  @GetMapping("/{userId}")
  public User getByUserId(@PathVariable String userId) {
    return dbEditorService.findByUserId(userId);
  }

  @PostMapping
  public User add(@RequestParam String userId) {
    return dbEditorService.add(userId);
  }

  @DeleteMapping("/{userId}")
  public User remove(@PathVariable String userId) {
    return dbEditorService.remove(userId);
  }
}

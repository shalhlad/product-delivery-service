package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.CategoryCreationDto;
import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.service.CategoryService;
import com.shalhlad.product_delivery_service.util.Utils;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class CategoryController {

  private final CategoryService service;

  @Autowired
  public CategoryController(CategoryService service) {
    this.service = service;
  }

  @GetMapping
  public Iterable<Category> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "25") int size) {
    return service.findAll(PageRequest.of(page, size));
  }

  @GetMapping("/{id}")
  public Category getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('DB_EDITOR')")
  public Category create(
      @RequestBody @Valid CategoryCreationDto categoryCreationDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.create(categoryCreationDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('DB_EDITOR')")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}

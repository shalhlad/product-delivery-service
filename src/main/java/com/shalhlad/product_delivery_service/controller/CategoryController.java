package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.CategoryCreationDto;
import com.shalhlad.product_delivery_service.dto.request.CategoryUpdateDto;
import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.service.CategoryService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  @ApiOperation(value = "getAllCategories", notes = "Returns all categories")
  public Iterable<Category> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "getCategoryById", notes = "Returns category by id")
  public Category getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @ApiOperation(value = "createCategory", notes = "Creates category")
  public Category create(
      @RequestBody @Valid CategoryCreationDto categoryCreationDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.create(categoryCreationDto);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @ApiOperation(value = "updateCategory", notes = "Updates category fields")
  public Category update(
      @PathVariable Long id,
      @RequestBody @Valid CategoryUpdateDto categoryUpdateDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.update(id, categoryUpdateDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @ApiOperation(value = "deleteCategory", notes = "Deletes category")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}

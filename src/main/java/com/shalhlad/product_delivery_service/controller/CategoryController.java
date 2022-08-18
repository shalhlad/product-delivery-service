package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.CategoryCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.CategoryUpdateRequest;
import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.service.CategoryService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService service;

  @GetMapping
  @ApiOperation(value = "getAllCategories", notes = "Returns all categories")
  public Iterable<Category> getAll() {
    return service.findAllCategories();
  }

  @GetMapping("/{categoryId}")
  @ApiOperation(value = "getCategoryById", notes = "Returns category by id")
  public Category getById(@PathVariable Long categoryId) {
    return service.findCategoryById(categoryId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @ApiOperation(value = "createCategory", notes = "Creates category")
  public Category create(
      @RequestBody @Valid CategoryCreateRequest categoryCreateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.createCategory(categoryCreateRequest);
  }

  @PatchMapping("/{categoryId}")
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @ApiOperation(value = "updateCategoryById", notes = "Updates category fields")
  public Category update(
      @PathVariable Long categoryId,
      @RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.updateCategoryById(categoryId, categoryUpdateRequest);
  }

  @DeleteMapping("/{categoryId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @ApiOperation(value = "deleteCategoryById", notes = "Deletes category")
  public void delete(@PathVariable Long categoryId) {
    service.deleteCategoryById(categoryId);
  }
}

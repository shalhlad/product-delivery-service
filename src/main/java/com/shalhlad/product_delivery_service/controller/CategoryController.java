package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.CategoryCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.CategoryUpdateRequest;
import com.shalhlad.product_delivery_service.entity.product.Category;
import com.shalhlad.product_delivery_service.service.CategoryService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("${apiPrefix}/categories")
@RequiredArgsConstructor
@Tag(name = "categories")
public class CategoryController {

  private final CategoryService service;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getAllCategories", description = "Returns all categories")
  public Iterable<Category> getAll() {
    return service.findAllCategories();
  }

  @GetMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getCategoryById", description = "Returns category by id")
  public Category getById(@PathVariable Long categoryId) {
    return service.findCategoryById(categoryId);
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @Operation(summary = "createCategory", description = "Creates category")
  @SecurityRequirement(name = "Bearer Authentication")
  public Category create(
      @RequestBody @Valid CategoryCreateRequest categoryCreateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.createCategory(categoryCreateRequest);
  }

  @PatchMapping(value = "/{categoryId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @Operation(summary = "updateCategoryById", description = "Updates category fields")
  @SecurityRequirement(name = "Bearer Authentication")
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
  @Operation(summary = "deleteCategoryById", description = "Deletes category")
  @SecurityRequirement(name = "Bearer Authentication")
  public void delete(@PathVariable Long categoryId) {
    service.deleteCategoryById(categoryId);
  }
}

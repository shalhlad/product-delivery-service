package com.shalhlad.productdeliveryservice.controller;

import com.shalhlad.productdeliveryservice.dto.request.ProductCreateRequest;
import com.shalhlad.productdeliveryservice.dto.request.ProductUpdateRequest;
import com.shalhlad.productdeliveryservice.entity.product.Product;
import com.shalhlad.productdeliveryservice.service.ProductService;
import com.shalhlad.productdeliveryservice.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPrefix}/products")
@RequiredArgsConstructor
@Tag(name = "products")
public class ProductController {

  private final ProductService service;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getAllProducts", description = "Returns all products")
  public Page<Product> getProducts(
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "25") int size
  ) {
    return service.findAllProducts(categoryId, PageRequest.of(page, size));
  }

  @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getProductById", description = "Returns product by id")
  public Product getById(@PathVariable Long productId) {
    return service.findProductById(productId);
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @Operation(summary = "createProduct", description = "Create product")
  @SecurityRequirement(name = "Bearer Authentication")
  public Product create(
      @RequestBody @Valid ProductCreateRequest productCreateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.createProduct(productCreateRequest);
  }

  @PatchMapping(value = "/{productId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @Operation(summary = "updateProductById", description = "Updates product fields by product id")
  @SecurityRequirement(name = "Bearer Authentication")
  public Product update(
      @PathVariable Long productId,
      @RequestBody @Valid ProductUpdateRequest productUpdateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.updateProductById(productId, productUpdateRequest);
  }

}

package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.ProductCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.ProductUpdateRequest;
import com.shalhlad.product_delivery_service.entity.product.Product;
import com.shalhlad.product_delivery_service.service.ProductService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService service;

  @GetMapping
  @ApiOperation(value = "getAllProducts", notes = "Returns all products")
  public Page<Product> getProducts(
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "25") int size
  ) {
    return service.findAllProducts(categoryId, PageRequest.of(page, size));
  }

  @GetMapping("/{productId}")
  @ApiOperation(value = "getProductById", notes = "Returns product by id")
  public Product getById(@PathVariable Long productId) {
    return service.findProductById(productId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @ApiOperation(value = "createProduct", notes = "Create product")
  public Product create(
      @RequestBody @Valid ProductCreateRequest productCreateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.createProduct(productCreateRequest);
  }

  @PatchMapping("/{productId}")
  @PreAuthorize("hasAnyRole({'DB_EDITOR', 'ADMIN'})")
  @ApiOperation(value = "updateProductById", notes = "Updates product fields by product id")
  public Product update(
      @PathVariable Long productId,
      @RequestBody @Valid ProductUpdateRequest productUpdateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.updateProductById(productId, productUpdateRequest);
  }

}

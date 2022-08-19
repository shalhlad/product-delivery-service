package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.OrderCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.OrderOperations;
import com.shalhlad.product_delivery_service.dto.response.OrderDetailsResponse;
import com.shalhlad.product_delivery_service.service.OrderService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/${apiPrefix}/orders")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@Tag(name = "orders")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

  private final OrderService service;

  @GetMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "getOrderById", description = "Returns order by id")
  public OrderDetailsResponse getById(
      @Parameter(hidden = true) Principal principal,
      @PathVariable Long orderId) {
    return service.findOrderById(orderId, principal);
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "createOrder", description = "Create order")
  public OrderDetailsResponse create(
      @Parameter(hidden = true) Principal principal,
      @RequestBody @Valid OrderCreateRequest orderCreateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.createOrder(principal, orderCreateRequest);
  }

  @PatchMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "applyOperationToOrderById", description = "Applies an operation on order by orderId")
  public OrderDetailsResponse applyOperation(
      @Parameter(hidden = true) Principal principal,
      @PathVariable Long orderId,
      @RequestParam OrderOperations operation) {
    return service.applyOperationToOrder(principal, orderId, operation);
  }
}

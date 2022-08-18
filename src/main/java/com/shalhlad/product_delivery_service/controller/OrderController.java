package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.OrderCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.OrderOperations;
import com.shalhlad.product_delivery_service.dto.response.OrderDetailsResponse;
import com.shalhlad.product_delivery_service.service.OrderService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService service;

  @GetMapping("/orders/{orderId}")
  @ApiOperation(value = "getOrderById", notes = "Returns order by id")
  public OrderDetailsResponse getById(
      @ApiIgnore Principal principal,
      @PathVariable Long orderId) {
    return service.findOrderById(orderId, principal);
  }

  @PostMapping("/orders")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "createOrder", notes = "Create order")
  public OrderDetailsResponse create(
      @ApiIgnore Principal principal,
      @RequestBody @Valid OrderCreateRequest orderCreateRequest,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return service.createOrder(principal, orderCreateRequest);
  }

  @PatchMapping("/orders/{orderId}")
  @ApiOperation(value = "applyOperationToOrderById", notes = "Applies an operation on order by orderId")
  public OrderDetailsResponse applyOperation(
      @ApiIgnore Principal principal,
      @PathVariable Long orderId,
      @RequestParam OrderOperations operation) {
    return service.applyOperationToOrder(principal, orderId, operation);
  }
}

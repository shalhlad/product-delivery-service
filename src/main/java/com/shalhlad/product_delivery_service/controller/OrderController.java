package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.OrderCreationDto;
import com.shalhlad.product_delivery_service.dto.request.OrderOperations;
import com.shalhlad.product_delivery_service.dto.response.OrderDetailsDto;
import com.shalhlad.product_delivery_service.entity.order.Stage;
import com.shalhlad.product_delivery_service.mapper.OrderMapper;
import com.shalhlad.product_delivery_service.service.OrderService;
import com.shalhlad.product_delivery_service.util.Utils;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@PreAuthorize("isAuthenticated()")
public class OrderController {

  private final OrderService service;
  private final OrderMapper mapper;

  @Autowired
  public OrderController(OrderService service, OrderMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping("/orders/{id}")
  @ApiOperation(value = "getOrderById", notes = "Returns order by id")
  public OrderDetailsDto getById(@PathVariable Long id) {
    return mapper.toDetailsDto(service.findById(id));
  }

  @GetMapping("/users/me/orders")
  @ApiOperation(value = "getAllOrdersOfAuthorizedUser", notes = "Returns all orders of authorized user")
  public Page<OrderDetailsDto> getAllByPrincipal(
      @ApiIgnore Principal principal,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "25") int size) {
    String email = principal.getName();
    return mapper.toDetailsDto(service.findAllByEmail(email, PageRequest.of(page, size)));
  }

  @GetMapping("/users/me/orders/active")
  @ApiOperation(value = "getAllActiveOrdersOfAuthorizedUser",
      notes = "Returns all active orders of authorized user")
  public Iterable<OrderDetailsDto> getAllActiveByPrincipal(
      @ApiIgnore Principal principal) {
    String email = principal.getName();
    return mapper.toDetailsDto(service.findActiveOrdersByEmail(email));
  }

  @GetMapping("/users/{userId}/orders")
  @ApiOperation(value = "getAllOrdersOfUserByUserId",
      notes = "Returns all orders of user by userId")
  public Page<OrderDetailsDto> getByUserId(
      @PathVariable String userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size) {
    return mapper.toDetailsDto(service.findAllByUserId(userId, PageRequest.of(page, size)));
  }

  @PostMapping("/orders")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "createOrder", notes = "Create order")
  public OrderDetailsDto create(
      @ApiIgnore Principal principal,
      @RequestBody @Valid OrderCreationDto orderCreationDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return mapper.toDetailsDto(service.create(principal, orderCreationDto));
  }

  @PatchMapping("/orders/{id}")
  @ApiOperation(value = "applyOrderOperationById", notes = "Applies an operation on order by orderId")
  public OrderDetailsDto applyOperation(
      @ApiIgnore Principal principal,
      @PathVariable Long id,
      @RequestParam OrderOperations operation) {
    return mapper.toDetailsDto(service.applyOperation(principal, id, operation));
  }

  @GetMapping("/departments/{departmentId}/orders")
  @PreAuthorize("hasAnyRole({'COLLECTOR', 'COURIER'})")
  @ApiOperation(value = "getAllOrdersOfDepartmentByStageByDepartmentId",
      notes = "Returns all order of department by department id and stage")
  public Page<OrderDetailsDto> getOrdersOfDepartmentByStage(
      @ApiIgnore Principal principal,
      @PathVariable Long departmentId,
      @RequestParam Stage stage,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size
  ) {
    return mapper.toDetailsDto(service.findAllByDepartmentAndStage(principal, departmentId, stage,
        PageRequest.of(page, size)));
  }

  @GetMapping("/orders/processable")
  @PreAuthorize("hasAnyRole({'COLLECTOR', 'COURIER'})")
  @ApiOperation(value = "getAllProcessableOrders",
      notes = "Returns all orders that authorized employee can start process")
  public Page<OrderDetailsDto> getProcessableOrders(
      @ApiIgnore Principal principal,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size
  ) {
    return mapper.toDetailsDto(
        service.findAllProcessableOrders(principal, PageRequest.of(page, size)));
  }

  @GetMapping("/orders/in-processing")
  @PreAuthorize("hasAnyRole({'COLLECTOR', 'COURIER'})")
  @ApiOperation(value = "getOrdersInProcessing",
      notes = "Returns all orders that in processing by authorized employee")
  public Iterable<OrderDetailsDto> getOrdersInProcessing(@ApiIgnore Principal principal) {
    return mapper.toDetailsDto(service.findAllInProcessingByPrincipal(principal));
  }
}

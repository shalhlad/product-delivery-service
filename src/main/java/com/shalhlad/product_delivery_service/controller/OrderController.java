package com.shalhlad.product_delivery_service.controller;

import com.shalhlad.product_delivery_service.dto.request.OrderCreationDto;
import com.shalhlad.product_delivery_service.dto.response.OrderDetailsDto;
import com.shalhlad.product_delivery_service.entity.order.Stage;
import com.shalhlad.product_delivery_service.mapper.OrderMapper;
import com.shalhlad.product_delivery_service.service.OrderService;
import com.shalhlad.product_delivery_service.util.Utils;
import java.security.Principal;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {

  private final OrderService service;
  private final OrderMapper mapper;

  @Autowired
  public OrderController(OrderService service, OrderMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping("/{id}")
  public OrderDetailsDto getById(@PathVariable Long id) {
    return mapper.toDetailsDto(service.getById(id));
  }

  @GetMapping
  public Iterable<OrderDetailsDto> getByUserId(
      @RequestParam String userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "25") int size) {
    return mapper.toDetailsDto(service.findAllByUserId(userId, PageRequest.of(page, size)));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderDetailsDto create(Principal principal,
      @RequestBody @Valid OrderCreationDto orderCreationDto,
      BindingResult bindingResult) {
    Utils.throwExceptionIfFailedValidation(bindingResult);
    return mapper.toDetailsDto(service.create(principal.getName(), orderCreationDto));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAnyRole({'CUSTOMER', 'COLLECTOR', 'COURIER'})")
  public OrderDetailsDto changeState(
      Principal principal,
      @PathVariable Long id,
      @RequestParam Stage stage) {
    return mapper.toDetailsDto(service.changeStage(principal, id, stage));
  }

  @GetMapping("/departments/{departmentId}/orders")
  @PreAuthorize("hasAnyRole({'COLLECTOR', 'COURIER'})")
  public Iterable<OrderDetailsDto> getOrdersOfDepartmentByStage(
      Principal principal,
      @PathVariable Long departmentId,
      @RequestParam Stage stage) {
    return mapper.toDetailsDto(service.findAllByDepartmentAndStage(principal, departmentId, stage));
  }
}

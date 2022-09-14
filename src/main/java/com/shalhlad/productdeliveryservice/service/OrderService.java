package com.shalhlad.productdeliveryservice.service;

import com.shalhlad.productdeliveryservice.dto.request.OrderCreateRequest;
import com.shalhlad.productdeliveryservice.dto.request.OrderOperations;
import com.shalhlad.productdeliveryservice.dto.response.OrderDetailsResponse;
import java.security.Principal;

public interface OrderService {

  OrderDetailsResponse createOrder(Principal principal, OrderCreateRequest orderCreateRequest);

  OrderDetailsResponse findOrderById(Long id, Principal principal);

  OrderDetailsResponse applyOperationToOrder(Principal principal, Long orderId,
      OrderOperations operation);
}

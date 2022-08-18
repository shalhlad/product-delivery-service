package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.OrderCreateRequest;
import com.shalhlad.product_delivery_service.dto.request.OrderOperations;
import com.shalhlad.product_delivery_service.dto.response.OrderDetailsResponse;
import java.security.Principal;

public interface OrderService {

  OrderDetailsResponse createOrder(Principal principal, OrderCreateRequest orderCreateRequest);

  OrderDetailsResponse findOrderById(Long id, Principal principal);

  OrderDetailsResponse applyOperationToOrder(Principal principal, Long orderId,
      OrderOperations operation);
}

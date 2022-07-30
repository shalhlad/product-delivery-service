package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.OrderCreationDto;
import com.shalhlad.product_delivery_service.entity.order.Order;
import com.shalhlad.product_delivery_service.entity.order.Stage;
import java.security.Principal;
import org.springframework.data.domain.Pageable;

public interface OrderService {

  Order create(String email, OrderCreationDto orderCreationDto);

  Order getById(Long id);

  Iterable<Order> getByUserId(String userId, Pageable pageable);

  Order changeStage(Principal principal, Long orderId, Stage newStage);
}

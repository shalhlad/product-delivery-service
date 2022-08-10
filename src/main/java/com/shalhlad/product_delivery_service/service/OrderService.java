package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.OrderCreationDto;
import com.shalhlad.product_delivery_service.dto.request.OrderOperations;
import com.shalhlad.product_delivery_service.entity.order.Order;
import com.shalhlad.product_delivery_service.entity.order.Stage;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

  Order create(Principal principal, OrderCreationDto orderCreationDto);

  Order findById(Long id);

  Page<Order> findAllByUserId(String userId, Pageable pageable);

  Order applyOperation(Principal principal, Long orderId, OrderOperations operation);

  Page<Order> findAllByDepartmentAndStage(Principal principal, Long departmentId, Stage stage,
      Pageable pageable);

  Page<Order> findAllByEmail(String email, Pageable pageable);

  Iterable<Order> findOrderInHandlingByPrincipal(Principal principal);
}

package com.shalhlad.product_delivery_service.repository;

import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.order.Order;
import com.shalhlad.product_delivery_service.entity.order.Stage;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

  Page<Order> findAllByUser(User user, Pageable pageable);

  Page<Order> findAllByDepartmentAndStage(Department department, Stage stage, Pageable pageable);

  boolean existsByOrderHandlersCurrentHandler(Employee currentHandler);

  Iterable<Order> findAllByOrderHandlersCurrentHandler(Employee currentHandler);

  boolean existsByUserAndStageNotAndStageNot(User user, Stage first, Stage second);

  Iterable<Order> findAllByUserAndStageNotAndStageNot(User user, Stage first, Stage second);

  Page<Order> findAllByDepartmentAndStageNotAndOrderHandlersCourier(Department department,
      Stage stage, Employee courier, Pageable pageable);
}

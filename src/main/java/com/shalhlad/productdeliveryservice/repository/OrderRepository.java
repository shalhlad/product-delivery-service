package com.shalhlad.productdeliveryservice.repository;

import com.shalhlad.productdeliveryservice.entity.department.Department;
import com.shalhlad.productdeliveryservice.entity.order.Order;
import com.shalhlad.productdeliveryservice.entity.order.Stage;
import com.shalhlad.productdeliveryservice.entity.user.Employee;
import com.shalhlad.productdeliveryservice.entity.user.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

  Page<Order> findAllByUser(User user, Pageable pageable);

  Optional<Order> findByIdAndUser(Long id, User user);

  boolean existsByOrderHandlersCurrentHandler(Employee currentHandler);

  boolean existsByUserAndStageNotAndStageNot(User user, Stage first, Stage second);

  Page<Order> findAllByOrderHandlersCurrentHandler(Employee currentHandler, Pageable pageable);

  Page<Order> findAllByDepartmentAndStage(Department department, Stage stage, Pageable pageable);

  Page<Order> findAllByUserAndStageNotAndStageNot(User user, Stage first, Stage second,
      Pageable pageable);


}

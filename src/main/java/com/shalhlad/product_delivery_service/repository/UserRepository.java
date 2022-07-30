package com.shalhlad.product_delivery_service.repository;

import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  Optional<User> findByUserId(String userId);

  Optional<User> findByEmail(String email);

  Iterable<User> findAllByRole(Role role);

  Optional<User> findByUserIdAndRole(String userId, Role role);

  boolean existsByUserId(String userId);

  boolean existsByEmail(String email);
}

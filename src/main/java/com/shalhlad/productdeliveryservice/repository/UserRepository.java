package com.shalhlad.productdeliveryservice.repository;

import com.shalhlad.productdeliveryservice.entity.user.Role;
import com.shalhlad.productdeliveryservice.entity.user.User;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

  Optional<User> findByUserId(String userId);

  Optional<User> findByEmail(String email);

  Iterable<User> findAllByRole(Role role);

  Optional<User> findByUserIdAndRole(String userId, Role role);

  boolean existsByUserId(String userId);

  boolean existsByEmail(String email);
}

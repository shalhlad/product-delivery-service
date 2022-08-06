package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  Page<User> findAll(Pageable pageable);

  User findByUserId(String userId);

  User findByEmail(String email);
}

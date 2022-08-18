package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  Page<UserDetailsResponse> findAllUsers(Pageable pageable);

  UserDetailsResponse findUserByUserId(String userId);
}

package com.shalhlad.productdeliveryservice.service;

import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  Page<UserDetailsResponse> findAllUsers(Pageable pageable);

  UserDetailsResponse findUserByUserId(String userId);
}

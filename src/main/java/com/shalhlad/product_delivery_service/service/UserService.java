package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateDto;
import com.shalhlad.product_delivery_service.entity.user.User;
import java.security.Principal;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  Iterable<User> findAll();

  User findByUserId(String userId);

  User update(Principal principal, String userId, UserDetailsUpdateDto userDetailsUpdateDto);
}

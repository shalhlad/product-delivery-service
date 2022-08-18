package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.UserService;
import java.util.Collections;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public Page<UserDetailsResponse> findAllUsers(Pageable pageable) {
    Page<User> users = userRepository.findAll(pageable);
    return userMapper.toDetailsResponse(users);
  }

  @Override
  public UserDetailsResponse findUserByUserId(String userId) {
    return userRepository.findByUserId(userId)
        .map(userMapper::toDetailsResponse)
        .orElseThrow(() ->
            new NotFoundException("User not found with userId: " + userId));
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email)
        .map(u -> new org.springframework.security.core.userdetails.User(
            u.getEmail(),
            u.getEncryptedPassword(),
            Collections.singleton(u.getRole())
        ))
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
  }
}

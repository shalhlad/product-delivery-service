package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.UserService;
import java.util.Collections;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @Override
  public User findByUserId(String userId) {
    return userRepository.findByUserId(userId).orElseThrow(() ->
        new NotFoundException("User not found with userId: " + userId));
  }

  @Override
  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() ->
        new NotFoundException("User not found with email: " + email));
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

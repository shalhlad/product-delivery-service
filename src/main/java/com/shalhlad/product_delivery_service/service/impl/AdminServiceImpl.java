package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.AdminService;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

  private final UserRepository userRepository;

  @Autowired
  public AdminServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Iterable<User> findAll() {
    return userRepository.findAllByRole(Role.ROLE_ADMIN);
  }

  @Override
  public User findByUserId(String userId) {
    return userRepository.findByUserIdAndRole(userId, Role.ROLE_ADMIN)
        .orElseThrow(() -> new NotFoundException("Admin not found with userId: " + userId));
  }
}

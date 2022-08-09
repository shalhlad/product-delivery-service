package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.NoRightsException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.DbEditorService;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DbEditorServiceImpl implements DbEditorService {

  private final UserRepository userRepository;

  @Autowired
  public DbEditorServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Iterable<User> findAll() {
    return userRepository.findAllByRole(Role.ROLE_DB_EDITOR);
  }

  @Override
  public User findByUserId(String userId) {
    return userRepository.findByUserIdAndRole(userId, Role.ROLE_DB_EDITOR)
        .orElseThrow(() -> new NotFoundException("DB editor not found with userId: " + userId));
  }

  @Override
  public User add(String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("User not found with userId: " + userId));
    if (user.getRole() != Role.ROLE_CUSTOMER) {
      throw new NoRightsException("Only user with role CUSTOMER can become DB editor");
    }
    user.setRole(Role.ROLE_DB_EDITOR);
    return userRepository.save(user);
  }

  @Override
  public User remove(String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("User not found with userId: " + userId));
    if (user.getRole() != Role.ROLE_DB_EDITOR) {
      throw new NoRightsException("Target user is not DB editor");
    }
    user.setRole(Role.ROLE_CUSTOMER);
    return userRepository.save(user);
  }
}

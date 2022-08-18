package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.AdminService;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public Iterable<UserDetailsResponse> findAllAdmins() {
    Iterable<User> users = userRepository.findAllByRole(Role.ROLE_ADMIN);
    return userMapper.toDetailsResponse(users);
  }

  @Override
  public UserDetailsResponse findAdminByUserId(String userId) {
    return userRepository.findByUserIdAndRole(userId, Role.ROLE_ADMIN)
        .map(userMapper::toDetailsResponse)
        .orElseThrow(() -> new NotFoundException("Admin not found with userId: " + userId));
  }
}

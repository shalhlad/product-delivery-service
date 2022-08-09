package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateDto;
import com.shalhlad.product_delivery_service.entity.user.Employee;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.repository.EmployeeRepository;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.ProfileService;
import java.security.Principal;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final EmployeeRepository employeeRepository;

  @Autowired
  public ProfileServiceImpl(UserMapper userMapper, UserRepository userRepository,
      EmployeeRepository employeeRepository) {
    this.userMapper = userMapper;
    this.userRepository = userRepository;
    this.employeeRepository = employeeRepository;
  }

  @Override
  public User getDetailsFromPrincipal(Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    if (user.getRole().isEmployee()) {
      user = employeeRepository.findById(user.getId()).orElseThrow();
    }
    return user;
  }

  @Override
  public User updateDetailsByPrincipal(Principal principal,
      UserDetailsUpdateDto userDetailsUpdateDto) {
    User user = getDetailsFromPrincipal(principal);
    userMapper.update(user, userDetailsUpdateDto);
    return user.getRole().isEmployee() ?
        employeeRepository.save((Employee) user) :
        userRepository.save(user);
  }
}

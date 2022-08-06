package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateDto;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.NoRightsException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.UserService;
import java.security.Principal;
import java.util.Collections;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public Iterable<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public User findByUserId(String userId) {
    return userRepository.findByUserId(userId).orElseThrow(() ->
        new NotFoundException("User not found with userId: " + userId));
  }

  @Override
  public User update(Principal principal, String userId,
      UserDetailsUpdateDto userDetailsUpdateDto) {
    String email = principal.getName();
    User principalUser = userRepository.findByEmail(email).orElseThrow();

    User targetUser = findByUserId(userId);
    if (!targetUser.equals(principalUser)) {
      throw new NoRightsException("Given userId does not belong to authorized user");
    }

    userMapper.update(principalUser, userDetailsUpdateDto);
    return userRepository.save(principalUser);
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

package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.SignUpDetailsDto;
import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateDto;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.NoRightsException;
import com.shalhlad.product_delivery_service.exception.NotFoundException;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.service.UserService;
import com.shalhlad.product_delivery_service.util.Utils;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
      UserMapper userMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
  }

  @Override
  public User signUp(SignUpDetailsDto signUpDetailsDto) {
    final int USER_ID_LENGTH = 15;
    if (userRepository.existsByEmail(signUpDetailsDto.getEmail())) {
      throw new RuntimeException(
          "User already exists with email: " + signUpDetailsDto.getEmail());
    }

    User user = new User();
    user.setEmail(signUpDetailsDto.getEmail());
    user.setEncryptedPassword(passwordEncoder.encode(signUpDetailsDto.getPassword()));
    user.setRegistrationDate(new Date());
    user.setFirstName(signUpDetailsDto.getFirstName());
    user.setRole(Role.ROLE_CUSTOMER);

    String userId;
    do {
      userId = Utils.generateUserId(USER_ID_LENGTH);
    } while (userRepository.existsByUserId(userId));
    user.setUserId(userId);

    return userRepository.save(user);
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
    User user = userRepository.findByEmail(email).orElseThrow();

    if (!user.getUserId().equals(userId)) {
      throw new NoRightsException("The current user cannot change information about another user");
    }

    userMapper.update(user, userDetailsUpdateDto);
    return userRepository.save(user);
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

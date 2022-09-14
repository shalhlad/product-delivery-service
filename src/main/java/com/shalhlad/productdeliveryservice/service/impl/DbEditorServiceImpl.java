package com.shalhlad.productdeliveryservice.service.impl;

import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;
import com.shalhlad.productdeliveryservice.entity.user.Role;
import com.shalhlad.productdeliveryservice.entity.user.User;
import com.shalhlad.productdeliveryservice.exception.NoRightsException;
import com.shalhlad.productdeliveryservice.exception.NotFoundException;
import com.shalhlad.productdeliveryservice.mapper.UserMapper;
import com.shalhlad.productdeliveryservice.repository.UserRepository;
import com.shalhlad.productdeliveryservice.service.DbEditorService;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DbEditorServiceImpl implements DbEditorService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public Iterable<UserDetailsResponse> findAllDbEditors() {
    Iterable<User> dbEditors = userRepository.findAllByRole(Role.ROLE_DB_EDITOR);
    return userMapper.toDetailsResponse(dbEditors);
  }

  @Override
  public UserDetailsResponse findDbEditorByUserId(String userId) {
    return userRepository.findByUserIdAndRole(userId, Role.ROLE_DB_EDITOR)
        .map(userMapper::toDetailsResponse)
        .orElseThrow(() -> new NotFoundException("DB editor not found with userId: " + userId));
  }

  @Override
  public UserDetailsResponse addDbEditor(String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("User not found with userId: " + userId));
    if (user.getRole() != Role.ROLE_CUSTOMER) {
      throw new NoRightsException("Only user with role CUSTOMER can become DB editor");
    }
    user.setRole(Role.ROLE_DB_EDITOR);

    User saved = userRepository.save(user);
    return userMapper.toDetailsResponse(saved);
  }

  @Override
  public UserDetailsResponse removeDbEditor(String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("User not found with userId: " + userId));
    if (user.getRole() != Role.ROLE_DB_EDITOR) {
      throw new NoRightsException("Target user is not DB editor");
    }
    user.setRole(Role.ROLE_CUSTOMER);
    User removed = userRepository.save(user);
    return userMapper.toDetailsResponse(removed);
  }
}

package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.SignUpRequest;
import com.shalhlad.product_delivery_service.dto.request.UserLoginRequest;
import com.shalhlad.product_delivery_service.dto.response.AccessTokenResponse;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;
import com.shalhlad.product_delivery_service.entity.user.Card;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.InvalidLoginOrPasswordException;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.mapper.UserMapper;
import com.shalhlad.product_delivery_service.repository.CardRepository;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.security.jwt.JwtProvider;
import com.shalhlad.product_delivery_service.service.AuthService;
import com.shalhlad.product_delivery_service.util.Utils;
import java.math.BigDecimal;
import java.util.Date;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final CardRepository cardRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final UserMapper userMapper;

  @Override
  public UserDetailsResponse signUp(SignUpRequest signUpRequest) {
    final int USER_ID_LENGTH = 15;
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new InvalidValueException(
          "User already exists with email: " + signUpRequest.getEmail());
    }

    Card card = new Card();
    card.setDiscountInPercents(BigDecimal.ZERO);
    card.setNumberOfOrders(0);

    User user = new User();
    user.setEmail(signUpRequest.getEmail());
    user.setEncryptedPassword(passwordEncoder.encode(signUpRequest.getPassword()));
    user.setRegistrationDate(new Date());
    user.setFirstName(signUpRequest.getFirstName());
    user.setRole(Role.ROLE_CUSTOMER);
    user.setCard(cardRepository.save(card));

    String userId;
    do {
      userId = Utils.generateUserId(USER_ID_LENGTH);
    } while (userRepository.existsByUserId(userId));
    user.setUserId(userId);

    User saved = userRepository.save(user);
    return userMapper.toDetailsResponse(saved);
  }

  @Override
  public AccessTokenResponse signIn(UserLoginRequest userLoginRequest) {
    String email = userLoginRequest.getEmail();
    String password = userLoginRequest.getPassword();

    User user = userRepository.findByEmail(email).orElse(null);
    if (user != null && passwordEncoder.matches(password, user.getEncryptedPassword())) {
      return AccessTokenResponse.of(jwtProvider.generateToken(email));
    } else {
      throw new InvalidLoginOrPasswordException("Invalid email or password");
    }
  }
}

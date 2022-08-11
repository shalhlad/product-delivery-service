package com.shalhlad.product_delivery_service.service.impl;

import com.shalhlad.product_delivery_service.dto.request.SignUpDetailsDto;
import com.shalhlad.product_delivery_service.dto.request.UserLoginDetailsDto;
import com.shalhlad.product_delivery_service.entity.user.Card;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.entity.user.User;
import com.shalhlad.product_delivery_service.exception.InvalidLoginOrPasswordException;
import com.shalhlad.product_delivery_service.exception.InvalidValueException;
import com.shalhlad.product_delivery_service.repository.CardRepository;
import com.shalhlad.product_delivery_service.repository.UserRepository;
import com.shalhlad.product_delivery_service.security.jwt.JwtProvider;
import com.shalhlad.product_delivery_service.service.AuthService;
import com.shalhlad.product_delivery_service.util.Utils;
import java.math.BigDecimal;
import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final CardRepository cardRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  @Autowired
  public AuthServiceImpl(UserRepository userRepository, CardRepository cardRepository,
      PasswordEncoder passwordEncoder,
      JwtProvider jwtProvider) {
    this.userRepository = userRepository;
    this.cardRepository = cardRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtProvider = jwtProvider;
  }

  @Override
  public User signUp(SignUpDetailsDto signUpDetailsDto) {
    final int USER_ID_LENGTH = 15;
    if (userRepository.existsByEmail(signUpDetailsDto.getEmail())) {
      throw new InvalidValueException(
          "User already exists with email: " + signUpDetailsDto.getEmail());
    }

    Card card = new Card();
    card.setDiscountInPercents(BigDecimal.ZERO);
    card.setNumberOfOrders(0);

    User user = new User();
    user.setEmail(signUpDetailsDto.getEmail());
    user.setEncryptedPassword(passwordEncoder.encode(signUpDetailsDto.getPassword()));
    user.setRegistrationDate(new Date());
    user.setFirstName(signUpDetailsDto.getFirstName());
    user.setRole(Role.ROLE_CUSTOMER);
    user.setCard(cardRepository.save(card));

    String userId;
    do {
      userId = Utils.generateUserId(USER_ID_LENGTH);
    } while (userRepository.existsByUserId(userId));
    user.setUserId(userId);

    return userRepository.save(user);
  }

  @Override
  public String signIn(UserLoginDetailsDto userLoginDetailsDto) {
    String email = userLoginDetailsDto.getEmail();
    String password = userLoginDetailsDto.getPassword();

    User user = userRepository.findByEmail(email).orElse(null);
    if (user != null && passwordEncoder.matches(password, user.getEncryptedPassword())) {
      return jwtProvider.generateToken(email);
    } else {
      throw new InvalidLoginOrPasswordException("Invalid email or password");
    }
  }
}

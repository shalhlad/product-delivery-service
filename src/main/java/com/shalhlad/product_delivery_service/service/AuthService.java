package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.SignUpDetailsDto;
import com.shalhlad.product_delivery_service.dto.request.UserLoginDetailsDto;
import com.shalhlad.product_delivery_service.entity.user.User;

public interface AuthService {

  User signUp(SignUpDetailsDto signUpDetailsDto);

  String signIn(UserLoginDetailsDto userLoginDetailsDto);

}

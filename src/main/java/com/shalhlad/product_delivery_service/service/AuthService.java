package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.SignUpRequest;
import com.shalhlad.product_delivery_service.dto.request.UserLoginRequest;
import com.shalhlad.product_delivery_service.dto.response.AccessTokenResponse;
import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;

public interface AuthService {

  UserDetailsResponse signUp(SignUpRequest signUpRequest);

  AccessTokenResponse signIn(UserLoginRequest userLoginRequest);

}

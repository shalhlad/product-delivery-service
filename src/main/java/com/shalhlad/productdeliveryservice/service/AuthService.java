package com.shalhlad.productdeliveryservice.service;

import com.shalhlad.productdeliveryservice.dto.request.SignUpRequest;
import com.shalhlad.productdeliveryservice.dto.request.UserLoginRequest;
import com.shalhlad.productdeliveryservice.dto.response.AccessTokenResponse;
import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;

public interface AuthService {

  UserDetailsResponse signUp(SignUpRequest signUpRequest);

  AccessTokenResponse signIn(UserLoginRequest userLoginRequest);

}

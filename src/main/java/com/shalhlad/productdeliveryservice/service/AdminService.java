package com.shalhlad.productdeliveryservice.service;

import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;

public interface AdminService {

  Iterable<UserDetailsResponse> findAllAdmins();

  UserDetailsResponse findAdminByUserId(String userId);

}

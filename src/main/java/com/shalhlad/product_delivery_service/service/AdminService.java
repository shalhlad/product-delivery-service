package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;

public interface AdminService {

  Iterable<UserDetailsResponse> findAllAdmins();

  UserDetailsResponse findAdminByUserId(String userId);

}

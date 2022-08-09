package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.entity.user.User;

public interface AdminService {

  Iterable<User> findAll();

  User findByUserId(String userId);

}

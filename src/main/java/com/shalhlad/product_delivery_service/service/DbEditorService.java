package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.entity.user.User;

public interface DbEditorService {

  Iterable<User> findAll();

  User findByUserId(String userId);

  User add(String userId);

  User remove(String userId);

}

package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.response.UserDetailsResponse;

public interface DbEditorService {

  Iterable<UserDetailsResponse> findAllDbEditors();

  UserDetailsResponse findDbEditorByUserId(String userId);

  UserDetailsResponse addDbEditor(String userId);

  UserDetailsResponse removeDbEditor(String userId);

}

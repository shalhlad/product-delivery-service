package com.shalhlad.productdeliveryservice.service;

import com.shalhlad.productdeliveryservice.dto.response.UserDetailsResponse;

public interface DbEditorService {

  Iterable<UserDetailsResponse> findAllDbEditors();

  UserDetailsResponse findDbEditorByUserId(String userId);

  UserDetailsResponse addDbEditor(String userId);

  UserDetailsResponse removeDbEditor(String userId);

}

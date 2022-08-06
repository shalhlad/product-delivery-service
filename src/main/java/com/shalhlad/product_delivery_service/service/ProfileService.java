package com.shalhlad.product_delivery_service.service;

import com.shalhlad.product_delivery_service.dto.request.UserDetailsUpdateDto;
import com.shalhlad.product_delivery_service.entity.user.User;
import java.security.Principal;

public interface ProfileService {

  User getDetailsFromPrincipal(Principal principal);

  User updateDetailsByPrincipal(Principal principal, UserDetailsUpdateDto userDetailsUpdateDto);
}

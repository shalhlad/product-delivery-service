package com.shalhlad.product_delivery_service.entity.user;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
  ROLE_CUSTOMER,
  ROLE_DB_EDITOR,
  ROLE_ADMIN,

  ROLE_DEPARTMENT_HEAD,
  ROLE_COURIER,
  ROLE_COLLECTOR,
  ROLE_WAREHOUSEMAN;

  @Override
  public String getAuthority() {
    return name();
  }
}

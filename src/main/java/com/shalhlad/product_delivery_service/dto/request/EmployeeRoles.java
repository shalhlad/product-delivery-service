package com.shalhlad.product_delivery_service.dto.request;

import com.shalhlad.product_delivery_service.entity.user.Role;

public enum EmployeeRoles {
  COURIER {
    @Override
    public Role toEntityRole() {
      return Role.ROLE_COURIER;
    }
  },

  COLLECTOR {
    @Override
    public Role toEntityRole() {
      return Role.ROLE_COLLECTOR;
    }
  },

  WAREHOUSEMAN {
    @Override
    public Role toEntityRole() {
      return Role.ROLE_WAREHOUSEMAN;
    }
  },

  DEPARTMENT_HEAD {
    @Override
    public Role toEntityRole() {
      return Role.ROLE_DEPARTMENT_HEAD;
    }
  };

  public abstract Role toEntityRole();
}

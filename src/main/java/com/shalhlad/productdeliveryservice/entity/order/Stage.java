package com.shalhlad.productdeliveryservice.entity.order;

public enum Stage {

  NEW {
    @Override
    public Stage next() {
      return APPROVED;
    }
  },

  APPROVED {
    @Override
    public Stage next() {
      return COLLECTING;
    }
  },

  COLLECTING {
    @Override
    public Stage next() {
      return COLLECTED;
    }
  },

  COLLECTED {
    @Override
    public Stage next() {
      return IN_DELIVERY;
    }
  },

  IN_DELIVERY {
    @Override
    public Stage next() {
      return GIVEN;
    }
  },

  GIVEN {
    @Override
    public Stage next() {
      throw new UnsupportedOperationException("GIVEN is final stage");
    }
  },

  CANCELED {
    @Override
    public Stage next() {
      throw new UnsupportedOperationException("There is no next stage after CANCELED");
    }
  };

  public abstract Stage next();

  public boolean canBeChangedByCollector() {
    return this == NEW ||
        this == APPROVED ||
        this == COLLECTING;
  }

  public boolean canBeChangedByCourier() {
    return this == COLLECTED ||
        this == IN_DELIVERY;
  }

}

package com.shalhlad.product_delivery_service.security;

public class SecurityConstants {

  public static final String SIGN_UP_URL = "/users";
  public static final String LOGIN_URL = "/users/login";
  public static final long EXPIRATION_TIME = 864000000; //10days
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String TOKEN_SECRET = "Minsk";
}

package com.shalhlad.product_delivery_service.exception;

public class InvalidLoginOrPasswordException extends RuntimeException {

  public InvalidLoginOrPasswordException(String message) {
    super(message);
  }
}

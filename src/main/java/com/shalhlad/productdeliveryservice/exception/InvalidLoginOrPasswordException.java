package com.shalhlad.productdeliveryservice.exception;

public class InvalidLoginOrPasswordException extends RuntimeException {

  public InvalidLoginOrPasswordException(String message) {
    super(message);
  }
}

package com.shalhlad.productdeliveryservice.util;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

public class Utils {

  private static final Random random = new SecureRandom();

  public static String generateUserId(int length) {
    String ALPHABET = "123456789ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz";

    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
    }
    return builder.toString();
  }

  public static void throwExceptionIfFailedValidation(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ValidationException(bindingResult.getAllErrors().stream()
          .map(DefaultMessageSourceResolvable::getDefaultMessage)
          .collect(Collectors.joining("; ")));
    }
  }

}

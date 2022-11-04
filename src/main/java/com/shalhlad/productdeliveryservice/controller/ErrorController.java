package com.shalhlad.productdeliveryservice.controller;

import com.shalhlad.productdeliveryservice.dto.response.ErrorResponse;
import com.shalhlad.productdeliveryservice.exception.InvalidLoginOrPasswordException;
import com.shalhlad.productdeliveryservice.exception.InvalidValueException;
import com.shalhlad.productdeliveryservice.exception.NoRightsException;
import com.shalhlad.productdeliveryservice.exception.NotFoundException;
import java.util.Date;
import javax.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {

  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception exception) {
    return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler({InvalidValueException.class})
  public ResponseEntity<ErrorResponse> handleConflictExceptions(Exception exception) {
    return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
  }

  @ExceptionHandler({ValidationException.class})
  public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception exception) {
    return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler({NoRightsException.class})
  public ResponseEntity<ErrorResponse> handleForbiddenExceptions(Exception exception) {
    return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage());
  }

  @ExceptionHandler({InvalidLoginOrPasswordException.class})
  public ResponseEntity<ErrorResponse> handleUnauthorizedExceptions(Exception exception) {
    return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception exception) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
  }

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus httpStatus, String message) {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(httpStatus.value());
    errorResponse.setStatus(httpStatus.getReasonPhrase());
    errorResponse.setMessage(message);
    errorResponse.setTimestamp(new Date());
    return new ResponseEntity<>(errorResponse, httpStatus);
  }
}

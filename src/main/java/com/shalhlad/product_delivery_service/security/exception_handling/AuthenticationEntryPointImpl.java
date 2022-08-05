package com.shalhlad.product_delivery_service.security.exception_handling;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.shalhlad.product_delivery_service.exception.handling.ErrorResponse;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setStatus(httpStatus.getReasonPhrase());
    errorResponse.setCode(httpStatus.value());
    errorResponse.setMessage("Not authorized");
    errorResponse.setTimestamp(new Date());

    response.setStatus(httpStatus.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    new JsonMapper().writeValue(response.getOutputStream(), errorResponse);
  }
}

package com.shalhlad.productdeliveryservice.security.exception_handling;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.shalhlad.productdeliveryservice.dto.response.ErrorResponse;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    HttpStatus httpStatus = HttpStatus.FORBIDDEN;

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setStatus(httpStatus.getReasonPhrase());
    errorResponse.setCode(httpStatus.value());
    errorResponse.setMessage("Access denied");
    errorResponse.setTimestamp(new Date());

    response.setStatus(httpStatus.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    new JsonMapper().writeValue(response.getOutputStream(), errorResponse);
  }
}

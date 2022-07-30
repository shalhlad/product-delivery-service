package com.shalhlad.product_delivery_service.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shalhlad.product_delivery_service.dto.request.UserLoginDetailsDto;
import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.exception.handling.ErrorResponse;
import com.shalhlad.product_delivery_service.security.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  public AuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp)
      throws AuthenticationException {
    try {

      UserLoginDetailsDto userLoginDetailsDto = new ObjectMapper().readValue(
          req.getInputStream(), UserLoginDetailsDto.class);
      return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
          userLoginDetailsDto.getEmail(),
          userLoginDetailsDto.getPassword(),
          Collections.singleton(Role.ROLE_CUSTOMER)
      ));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException {
    User user = (User) authResult.getPrincipal();

    String username = user.getUsername();
    GrantedAuthority role = user.getAuthorities()
        .stream()
        .findFirst().orElseThrow();

    String token = Jwts.builder()
        .setSubject(username)
        .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
        .claim("role", role)
        .signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET)
        .compact();

    response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(), Map.of("token", token));
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) throws IOException {
    HttpStatus status = HttpStatus.UNAUTHORIZED;

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(status.value());
    errorResponse.setStatus(status.getReasonPhrase());
    errorResponse.setMessage("Invalid login or password");
    errorResponse.setTimestamp(new Date());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(status.value());
    new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
  }
}

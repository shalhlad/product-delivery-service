package com.shalhlad.productdeliveryservice.security.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class JwtFilter extends GenericFilterBean {

  private static final String BEARER_PREFIX = "Bearer ";

  private final UserDetailsService userDetailsService;
  private final JwtProvider jwtProvider;

  @Autowired
  public JwtFilter(UserDetailsService userDetailsService, JwtProvider jwtProvider) {
    this.userDetailsService = userDetailsService;
    this.jwtProvider = jwtProvider;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String token = getTokenFromRequest((HttpServletRequest) request);
    if (token != null) {
      String email = jwtProvider.getEmailFromToken(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(email);
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);
    }
    chain.doFilter(request, response);
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    String token = null;
    if (header != null && header.startsWith(BEARER_PREFIX)) {
      token = header.replace(BEARER_PREFIX, "");
    }
    return token;
  }
}

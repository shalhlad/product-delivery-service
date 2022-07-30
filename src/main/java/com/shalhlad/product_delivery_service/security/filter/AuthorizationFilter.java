package com.shalhlad.product_delivery_service.security.filter;

import com.shalhlad.product_delivery_service.entity.user.Role;
import com.shalhlad.product_delivery_service.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class AuthorizationFilter extends BasicAuthenticationFilter {

  public AuthorizationFilter(
      AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header != null && header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
      UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    chain.doFilter(request, response);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;

    String token = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (token != null) {
      token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

      Jws<Claims> jws = Jwts.parser()
          .setSigningKey(SecurityConstants.TOKEN_SECRET)
          .parseClaimsJws(token);

      String user = jws
          .getBody()
          .getSubject();
      String role = jws.getBody().get("role", String.class);

      if (user != null) {
        usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
            user,
            null,
            Collections.singleton(
                Role.valueOf(role)));
      }
    }

    return usernamePasswordAuthenticationToken;
  }
}

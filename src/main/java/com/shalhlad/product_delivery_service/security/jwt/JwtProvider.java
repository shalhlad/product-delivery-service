package com.shalhlad.product_delivery_service.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration-days}")
  private int tokenExpirationDays;

  public String generateToken(String email) {
    Date expirationDate = Date.from(
        LocalDate.now()
            .plusDays(tokenExpirationDays)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
    );
    return Jwts.builder()
        .setSubject(email)
        .setExpiration(expirationDate)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  public String getEmailFromToken(String token) {
    return Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

}

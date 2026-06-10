package com.example.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtService {
  @Value("${spring.jwt.secret_key}")
  private String SECRET_KEY;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(Integer userId, String username) {
    return Jwts.builder()
            .subject(username)
            .claim("userId", userId)
            .signWith(getSigningKey())
            .compact();
  }

}

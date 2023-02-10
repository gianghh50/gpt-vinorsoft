package com.vinorsoft.gpt.service.chat.security.jwt;


import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.*;

@Component
public class JwtUtils {

  private String jwtSecret = "JwtSecretKey";

  private int jwtExpirationMs = 86400000;

  public String generateJwtToken(Authentication authentication) {
    Long now = System.currentTimeMillis();

    return Jwts.builder()
        .setSubject((authentication.getName()))
        .claim("authorities", authentication.getAuthorities().stream()
           .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now  + jwtExpirationMs)) 
        .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
        .compact();
  }

}

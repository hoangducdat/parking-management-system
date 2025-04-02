package org.project.backend.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.project.backend.entity.User;
import org.project.backend.exception.InvalidInputException;
import org.project.backend.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.access.token.ttl}")
  private long accessTokenTtl;

  @Value("${jwt.refresh.token.ttl}")
  private long refreshTokenTtl;

  @Override
  public String generateAccessToken(User user) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("role", user.getRole())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenTtl))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  @Override
  public String generateRefreshToken(User user) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenTtl))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  @Override
  public String validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
      return getUsernameFromToken(token);
    } catch (SignatureException e) {
      throw new InvalidInputException("Invalid JWT signature");
    } catch (MalformedJwtException e) {
      throw new InvalidInputException("Invalid JWT token");
    } catch (ExpiredJwtException e) {
      throw new InvalidInputException("JWT token is expired");
    } catch (UnsupportedJwtException e) {
      throw new InvalidInputException("JWT token is unsupported");
    } catch (IllegalArgumentException e) {
      throw new InvalidInputException("JWT claims string is empty");
    }
  }

  @Override
  public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }

  @Override
  public String getRoleFromToken(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody();
    return claims.get("role", String.class);
  }

  @Override
  public boolean isTokenExpired(String token) {
    try {
      Claims claims = Jwts.parser()
          .setSigningKey(jwtSecret)
          .parseClaimsJws(token)
          .getBody();
      return claims.getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    }
  }
}

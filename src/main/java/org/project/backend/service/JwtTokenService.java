package org.project.backend.service;

import org.project.backend.entity.User;

public interface JwtTokenService {
  String generateAccessToken(User user);
  String generateRefreshToken(User user);
  String validateToken(String token);
  String getUsernameFromToken(String token);
  String getRoleFromToken(String token);
  boolean isTokenExpired(String token);
}

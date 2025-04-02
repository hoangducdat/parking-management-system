package org.project.backend.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginResponse {

  private String accessToken;
  private String refreshToken;
  private long accessTokenTtl;
  private long refreshTokenTtl;
  private String tokenType;
  private UserResponse user;

  public LoginResponse() {
  }

  public LoginResponse(String accessToken, String refreshToken, long accessTokenTtl,
      long refreshTokenTtl,
      String tokenType, UserResponse user) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.accessTokenTtl = accessTokenTtl;
    this.refreshTokenTtl = refreshTokenTtl;
    this.tokenType = tokenType;
    this.user = user;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public long getAccessTokenTtl() {
    return accessTokenTtl;
  }

  public void setAccessTokenTtl(long accessTokenTtl) {
    this.accessTokenTtl = accessTokenTtl;
  }

  public long getRefreshTokenTtl() {
    return refreshTokenTtl;
  }

  public void setRefreshTokenTtl(long refreshTokenTtl) {
    this.refreshTokenTtl = refreshTokenTtl;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public UserResponse getUser() {
    return user;
  }

  public void setUser(UserResponse user) {
    this.user = user;
  }
}


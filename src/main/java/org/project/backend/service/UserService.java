package org.project.backend.service;

import org.project.backend.dto.request.LoginRequest;
import org.project.backend.dto.request.RegisterRequest;
import org.project.backend.dto.response.LoginResponse;

public interface UserService {
  void register(RegisterRequest request);
  void delete(String id);
  LoginResponse login(String username, String password);
}
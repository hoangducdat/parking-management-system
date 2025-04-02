package org.project.backend.service.impl;

import org.project.backend.constant.Constants;
import org.project.backend.dto.request.LoginRequest;
import org.project.backend.dto.request.RegisterRequest;
import org.project.backend.dto.response.LoginResponse;
import org.project.backend.dto.response.UserResponse;
import org.project.backend.entity.User;
import org.project.backend.exception.InvalidInputException;
import org.project.backend.repository.UserRepository;
import org.project.backend.service.JwtTokenService;
import org.project.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenService jwtTokenService;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
      JwtTokenService jwtTokenService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenService = jwtTokenService;
  }

  @Override
  @Transactional
  public void register(RegisterRequest request) {
    log.info("Start registering user");
    if (userRepository.findByUsername(request.getUsername()) != null) {
      log.error("Username is already exists");
      throw new InvalidInputException("Username " + request.getUsername() + " already exists");
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());

    User savedUser = userRepository.save(user);
    log.info("Registered user successfully");
  }

  @Override
  public LoginResponse login(String username, String password) {
    log.info("Start login user with username {}", username);
    User user = userRepository.findByUsername(username);
    if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
      log.error("Invalid username or password");
      throw new InvalidInputException("Invalid username or password");
    }

    String accessToken = jwtTokenService.generateAccessToken(user);
    log.info("Generated access token {}", accessToken);
    String refreshToken = jwtTokenService.generateRefreshToken(user);
    log.info("Generated refresh token {}", refreshToken);

    UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getRole());
    log.info("Successfully logged in user");
    return new LoginResponse(accessToken, refreshToken, 259200000L, 2592000000L, "Bearer",
        userResponse);
  }

  @Override
  @Transactional
  public void delete(String id) {
    log.info("Start deleting user with id {}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new InvalidInputException("User with ID " + id + " not found"));
    userRepository.delete(user);
    log.info("Deleted user successfully");
  }

}

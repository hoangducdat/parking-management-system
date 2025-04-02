package org.project.backend.controller;

import jakarta.validation.Valid;
import org.project.backend.dto.request.LoginRequest;
import org.project.backend.dto.request.RegisterRequest;
import org.project.backend.dto.response.ApplicationResponse;
import org.project.backend.dto.response.LoginResponse;
import org.project.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ApplicationResponse<String> register(@Valid @RequestBody RegisterRequest request) {
    log.info("Registering user {}", request);
    userService.register(request);
    return ApplicationResponse.of(HttpStatus.CREATED.value(),"null");
  }

  @PostMapping("/login")
  public ApplicationResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    log.info("Login user {}", request);
    LoginResponse response = userService.login(request.getUsername(), request.getPassword());
    log.info("Login response {}", response);
    return ApplicationResponse.of(HttpStatus.OK.value(), response);
  }

  @DeleteMapping("/{id}")
  public ApplicationResponse<String> delete(@PathVariable("id") String id) {
    log.info("Deleting user {}", id);
    userService.delete(id);
    return ApplicationResponse.of(HttpStatus.NO_CONTENT.value(),"null");
  }
}

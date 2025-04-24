package org.project.backend.security;

import org.project.backend.exception.InvalidInputException;
import org.project.backend.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.project.backend.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final static Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private final JwtTokenService jwtTokenService;
  private final RedisService redisService;

  public JwtAuthenticationFilter(JwtTokenService jwtTokenService, RedisService redisService) {
    this.jwtTokenService = jwtTokenService;
    this.redisService = redisService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    String token = extractToken(request);
    if (token == null) {
      log.debug("No JWT token found in request to {}", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }
    if (redisService.isTokenBlacklisted(token)) {
      log.warn("Token is blacklisted: {}", token);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Token has been blacklisted");
      return;
    }
    try {
      String username = jwtTokenService.validateToken(token);
      String role = jwtTokenService.getRoleFromToken(token);

      if (username != null && role != null) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            username, null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.info("Authenticated user {} with role {} for request {}", username, role, request.getRequestURI());
      }
    } catch (InvalidInputException e) {
      log.warn("JWT validation failed for request {}: {}", request.getRequestURI(), e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Invalid or expired token: " + e.getMessage());
      return;
    } catch (Exception e) {
      log.error("Unexpected error during JWT validation for request {}: {}", request.getRequestURI(), e.getMessage(), e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().write("Internal server error");
      return;
    }
    filterChain.doFilter(request,response);
  }


  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }
}

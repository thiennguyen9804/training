package com.example.order_service.security;

import com.example.order_service.model.entity.AuthUser;
import com.example.order_service.model.exception.UnauthenticatedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final HandlerExceptionResolver resolver;

  public JwtAuthenticationFilter(
          JwtService jwtService,
          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.jwtService = jwtService;
    this.resolver = resolver;
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain)
          throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      reject(request, response, "JWT is empty");
      return;
    }

    String token = authHeader.substring(7);
    Integer userId = jwtService.extractUserId(token);

    if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      if (!jwtService.isTokenValid(token)) {
        reject(request, response, "Invalid JWT");
        return;
      }
      AuthUser userPrincipal = new AuthUser();
      userPrincipal.setId(userId);
      UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(userPrincipal, null, new ArrayList<>());
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    filterChain.doFilter(request, response);
  }

  private void reject(HttpServletRequest request, HttpServletResponse response, String message) {
    resolver.resolveException(request, response, null, new UnauthenticatedException(message));
  }
}
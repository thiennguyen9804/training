package com.example.order_service.security;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.order_service.model.entity.AuthUser;
import com.example.order_service.model.exception.UnauthenticatedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTests {
  @Mock private JwtService jwtService;

  @Mock private HandlerExceptionResolver resolver;

  @InjectMocks private JwtAuthenticationFilter filter;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void whenNoAuthHeader_thenResolveExceptionAndReturn() throws Exception {
    when(request.getHeader("Authorization")).thenReturn(null);
    filter.doFilterInternal(request, response, filterChain);
    verify(resolver)
        .resolveException(eq(request), eq(response), isNull(), isA(UnauthenticatedException.class));
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  void whenValidToken_thenSetAuthentication() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
    when(jwtService.extractUserId("valid.token.here")).thenReturn(1);
    when(jwtService.isTokenValid("valid.token.here")).thenReturn(true);
    filter.doFilterInternal(request, response, filterChain);
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNotNull();
    assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    AuthUser principal = (AuthUser) authentication.getPrincipal();
    assertThat(principal.getId()).isEqualTo(1);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void whenTokenInvalid_thenSkipAuthentication() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");
    when(jwtService.extractUserId("invalid.token")).thenReturn(1);
    when(jwtService.isTokenValid("invalid.token")).thenReturn(false);
    filter.doFilterInternal(request, response, filterChain);
    verify(resolver)
        .resolveException(eq(request), eq(response), isNull(), isA(UnauthenticatedException.class));
    verify(filterChain, never()).doFilter(any(), any());
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }
}

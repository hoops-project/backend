package com.zerobase.hoops.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";

  private final TokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = resolveTokenFromRequest(request);
    String requestURI = request.getRequestURI();

    try {
      if (!isSearchPath(requestURI) && !authenticateRequest(accessToken,
          requestURI)) {
        throw new AccessDeniedException("Access Denied");
      }
      filterChain.doFilter(request, response);
    } catch (AccessDeniedException e) {
      handleAccessDenied(response);
    }
  }

  private boolean isSearchPath(String requestURI) {
    return requestURI.startsWith("/api/game-user/search")
        || requestURI.startsWith("/api/game-user/search-address");
  }

  private boolean authenticateRequest(String accessToken, String requestURI) {
    if (accessToken != null && tokenProvider.validateToken(accessToken)) {
      Authentication auth = tokenProvider.getAuthentication(accessToken);
      SecurityContextHolder.getContext().setAuthentication(auth);
      return true;
    }
    return isPublicPath(requestURI);
  }

  private void handleAccessDenied(HttpServletResponse response) throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    ObjectMapper objectMapper = new ObjectMapper();
    String errorMessage = objectMapper.writeValueAsString(
        Map.of("statusCode", HttpServletResponse.SC_FORBIDDEN,
            "errorCode", "ACCESS_DENIED",
            "errorMessage", "접근 권한이 없습니다. 로그인 후 이용해주세요."));
    response.getWriter().write(errorMessage);
  }
  private String resolveTokenFromRequest(HttpServletRequest request) {
    String token = request.getHeader(TOKEN_HEADER);

    if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
      return token.substring(TOKEN_PREFIX.length());
    }
    return null;
  }

  private boolean isPublicPath(String requestURI) {
    List<String> publicPaths = List.of(
        "/api/user", "/api/auth/login", "/api/oauth2/login/kakao",
        "/api/oauth2/kakao", "/swagger-ui", "/v3/api-docs",
        "/api/game-user/search",
        "/api/game-user/search-address",
        "/ws",
        "/api/game-creator/game/detail");
    return publicPaths.stream().anyMatch(requestURI::startsWith);
  }
}

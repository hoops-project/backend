package com.zerobase.hoops.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.manager.service.ManagerService;
import com.zerobase.hoops.users.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";

  private final TokenProvider tokenProvider;
  private final ObjectMapper objectMapper;
  private final UserService userService;
  private final ManagerService managerService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = resolveTokenFromRequest(request);

    try {
      if (StringUtils.hasText(accessToken) && tokenProvider.isLogOut(
          accessToken)) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        String errorMessage = objectMapper.writeValueAsString(
            Map.of("error", "Unauthorized", "message",
                "Your token is invalid."));
        response.getWriter().write(errorMessage);

        // 요청 헤더에 access token 이 없으면 Exception 발생
      } else if (!StringUtils.hasText(accessToken)) {
        log.warn("Not have Access Token!");

        // 유효 기간이 지나지 않았으면 인증 세팅 진행
      } else if (tokenProvider.validateToken(accessToken)) {
        Authentication auth = tokenProvider.getAuthentication(accessToken);

        // 토큰의 인증정보 세팅
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 블랙리스트 체크
        managerService.checkBlackList(tokenProvider.getUsername(accessToken));
        log.info(String.format("[%s] -> %s",
            tokenProvider.getUsername(accessToken), request.getRequestURI()));
      }
    } catch (ExpiredJwtException e) {
      log.warn("에러 메세지 : " + e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  private String resolveTokenFromRequest(HttpServletRequest request) {
    String token = request.getHeader(TOKEN_HEADER);

    if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
      return token.substring(TOKEN_PREFIX.length());
    }
    return null;
  }
}

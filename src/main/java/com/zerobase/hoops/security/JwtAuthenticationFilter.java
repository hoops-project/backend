package com.zerobase.hoops.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.users.service.UserService;
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

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = resolveTokenFromRequest(request);

    if (StringUtils.hasText(accessToken) && tokenProvider.isLogOut(accessToken)) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json");
      String errorMessage = objectMapper.writeValueAsString(
          Map.of("error", "Unauthorized", "message",
              "Your token is blacklisted."));
      response.getWriter().write(errorMessage);
    }

    if (StringUtils.hasText(accessToken) &&
        tokenProvider.validateToken(accessToken)) {
      Authentication auth = tokenProvider.getAuthentication(accessToken);

      SecurityContextHolder.getContext().setAuthentication(auth);

      // (블랙리스트 체크)
      userService.checkBlackList(tokenProvider.getUsername(accessToken));

      log.info(String.format("[%s] -> %s",
          tokenProvider.getUsername(accessToken), request.getRequestURI()));
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

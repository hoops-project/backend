package com.zerobase.hoops.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.exception.ErrorCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (JwtException e) {
      String message = e.getMessage();
      if(ErrorCode.NOT_FOUND_TOKEN.getDescription().equals(message)) {
        setResponse(response, ErrorCode.NOT_FOUND_TOKEN);
      } else if (ErrorCode.UNSUPPORTED_TOKEN.getDescription().equals(message)) {
        setResponse(response, ErrorCode.UNSUPPORTED_TOKEN);
      } else if (ErrorCode.INVALID_TOKEN.getDescription().equals(message)) {
        setResponse(response, ErrorCode.INVALID_TOKEN);
      } else if (ErrorCode.EXPIRED_TOKEN.getDescription().equals(message)) {
        setResponse(response, ErrorCode.EXPIRED_TOKEN);
      } else if (ErrorCode.BAN_FOR_10DAYS.getDescription().equals(message)) {
        setResponse(response, ErrorCode.BAN_FOR_10DAYS);
      } else if (ErrorCode.ALREADY_LOGOUT.getDescription().equals(message)) {
        setResponse(response, ErrorCode.ALREADY_LOGOUT);
      } else {
        setResponse(response, ErrorCode.ACCESS_DENIED);
      }
    }
  }

  private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws RuntimeException, IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(errorCode.getStatusCode());
    ObjectMapper objectMapper = new ObjectMapper();
    String errorMessage = objectMapper.writeValueAsString(
        Map.of("statusCode", errorCode.getStatusCode(),
            "errorCode", errorCode,
            "errorMessage", errorCode.getDescription()));

    response.getWriter().print(errorMessage);
  }
}

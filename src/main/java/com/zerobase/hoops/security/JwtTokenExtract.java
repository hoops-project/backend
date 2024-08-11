package com.zerobase.hoops.security;

import com.zerobase.hoops.document.UserDocument;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.users.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenExtract {

  private final JwtParser jwtParser;
  private final UserRepository userRepository;

  public UserDocument currentUser() {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();
    log.info("JwtTokenExtract - currentUser value = {}", authentication);
    if (authentication == null || !authentication.isAuthenticated()
        || authentication.getPrincipal() == null) {
      throw new CustomException(ErrorCode.EXPIRED_TOKEN);
    }
    if (authentication.getPrincipal() instanceof UserDocument) {
      return (UserDocument) authentication.getPrincipal();
    } else {
      throw new CustomException(ErrorCode.EXPIRED_TOKEN);
    }
  }

  public UserDocument getUserFromToken(String authorizationHeader) {
    log.info("JwtTokenExtract - getUserFromToken value = {}",
        authorizationHeader);
    if (authorizationHeader == null || !authorizationHeader.startsWith(
        "Bearer ")) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
    String token = authorizationHeader.substring(7);
    try {
      Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
      String userLoginId = claimsJws.getBody().getSubject();
      UserDocument userDocument = userRepository.findByLoginIdAndDeletedDateTimeNull(
              userLoginId)
          .orElseThrow(
              () -> new CustomException(ErrorCode.USER_NOT_FOUND));
      if (userDocument == null) {
        throw new CustomException(ErrorCode.EXPIRED_TOKEN);
      }
      return userDocument;
    } catch (JwtException e) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }
}

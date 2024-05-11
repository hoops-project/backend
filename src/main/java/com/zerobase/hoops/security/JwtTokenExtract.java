package com.zerobase.hoops.security;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenExtract {
  public UserEntity currentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
      throw new CustomException(ErrorCode.EXPIRED_TOKEN);
    }

    if (authentication.getPrincipal() instanceof UserEntity) {
      return (UserEntity) authentication.getPrincipal();
    } else {
      throw new CustomException(ErrorCode.EXPIRED_TOKEN);
    }
  }
}

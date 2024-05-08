package com.zerobase.hoops.security;

import com.zerobase.hoops.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenExtract {

  public UserEntity currentUser() {
    Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

    return (UserEntity) authentication.getPrincipal();
  }

}


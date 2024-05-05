package com.zerobase.hoops.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode {
  NOT_VALIDATE_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "만료된 토큰입니다."),
  NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다.");


  private final HttpStatus httpStatus;
  private final String detail;
}

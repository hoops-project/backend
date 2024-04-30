package com.zerobase.hoops.users.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  INVALID_INPUT(HttpStatus.BAD_REQUEST.value(), "필수 입력 값이 누락되었습니다."),
  INVALID_PATTERN(HttpStatus.BAD_REQUEST.value(), "형식에 맞게 입력 해야합니다."),

  MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "메일 발송에 실패하였습니다."),

  DUPLICATED_ID(HttpStatus.CONFLICT.value(), "이미 사용 중인 아이디입니다."),
  DUPLICATED_NICKNAME(HttpStatus.CONFLICT.value(), "이미 사용 중인 별명입니다."),
  DUPLICATED_EMAIL(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "아이디가 존재하지 않습니다."),
  NOT_MATCHED_PASSWORD(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다.");

  private final int statusCode;
  private final String description;
}

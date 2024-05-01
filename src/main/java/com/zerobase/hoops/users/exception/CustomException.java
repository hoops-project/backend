package com.zerobase.hoops.users.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomException extends RuntimeException {

  private ErrorCode errorCode;
  private String errorMessage;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getDescription());
    this.errorCode = errorCode;
    this.errorMessage = errorCode.getDescription();
  }
}

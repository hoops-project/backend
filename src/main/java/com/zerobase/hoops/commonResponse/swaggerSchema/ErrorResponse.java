package com.zerobase.hoops.commonResponse.swaggerSchema;

import io.swagger.v3.oas.annotations.media.Schema;

public class ErrorResponse {

  // User Error

  @Schema(name = "ExpiredRefreshToken", description = "리프레시 토큰 만료 응답")
  public static class ExpiredRefreshToken extends
      com.zerobase.hoops.exception.ErrorResponse {

    @Schema(description = "errorCode", example = "EXPIRED_REFRESH_TOKEN")
    private String errorCode;
    @Schema(description = "errorMessage", example = "리프레시 토큰의 기간이 만료되었습니다.")
    private String errorMessage;
    @Schema(description = "응답 상태", example = "403")
    private String statusCode;
  }

  @Schema(name = "UserNotFound", description = "유저정보 없음")
  public static class UserNotFound extends
      com.zerobase.hoops.exception.ErrorResponse {

    @Schema(description = "errorCode", example = "USER_NOT_FOUND")
    private String errorCode;
    @Schema(description = "errorMessage", example = "아이디가 존재하지 않습니다.")
    private String errorMessage;
    @Schema(description = "응답 상태", example = "400")
    private String statusCode;
  }

  // Server Error

  @Schema(name = "ServerError", description = "서버에러")
  public static class ServerError extends
      com.zerobase.hoops.exception.ErrorResponse {

    @Schema(description = "errorCode", example = "INTERNAL_SERVER_ERROR")
    private String errorCode;
    @Schema(description = "errorMessage", example = "내부 서버 오류")
    private String errorMessage;
    @Schema(description = "응답 상태", example = "500")
    private String statusCode;

  }

  // Report Error

  @Schema(name = "AlREADY_REPORTED", description = "이미 신고됨")
  public static class AlreadyReported extends
      com.zerobase.hoops.exception.ErrorResponse {

    @Schema(description = "errorCode", example = "AlREADY_REPORTED")
    private String errorCode;
    @Schema(description = "errorMessage", example = "이미 신고가 완료되었습니다.")
    private String errorMessage;
    @Schema(description = "응답 상태", example = "400")
    private String statusCode;

  }

  @Schema(name = "NOT_EXIST_REPORTED", description = "신고 내용이 없음")
  public static class NotExistReported extends
      com.zerobase.hoops.exception.ErrorResponse {

    @Schema(description = "errorCode", example = "NOT_EXIST_REPORTED")
    private String errorCode;
    @Schema(description = "errorMessage", example = "신고 내용이 없습니다..")
    private String errorMessage;
    @Schema(description = "응답 상태", example = "400")
    private String statusCode;

  }
}

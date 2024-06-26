package com.zerobase.hoops.commonResponse.swaggerSchema;

import io.swagger.v3.oas.annotations.media.Schema;

public class ErrorResponse {

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

  @Schema(name = "CustomError", description = "커스텀에러")
  public static class CustomError extends
      com.zerobase.hoops.exception.ErrorResponse {

    @Schema(description = "errorCode", example = "CustomError")
    private String errorCode;
    @Schema(description = "errorMessage", example = "상황에 맞는 커스텀 에러.")
    private String errorMessage;
    @Schema(description = "응답 상태", example = "400")
    private String statusCode;

  }
}

package com.zerobase.hoops.commonResponse.swaggerSchema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class BlackListResponse {
  @Getter
  @AllArgsConstructor
  @Schema(name = "BlackListSuccess", description = "블랙리스트 적용 성공")
  public static class BlackListSuccess implements SwaggerApiResponse {

    @Schema(description = "응답 메시지", example = "블랙리스트 적용")
    private String title;
    @Schema(description = "응답 상태", example = "Success")
    private String detail;

  }

  @Getter
  @AllArgsConstructor
  @Schema(name = "BlackUserTrueFalse", description = "블랙유저 체크")
  public static class BlackUserTrueFalse implements SwaggerApiResponse {

    @Schema(description = "응답 메시지", example = "블랙리스트 여부")
    private String title;
    @Schema(description = "응답 상태", example = "True")
    private String detail;

  }

  @Getter
  @AllArgsConstructor
  @Schema(name = "BlackUserUnlock", description = "블랙유저 해제")
  public static class BlackUserUnlock implements SwaggerApiResponse {

    @Schema(description = "응답 메시지", example = "블랙리스트 해제")
    private String title;
    @Schema(description = "응답 상태", example = "Success")
    private String detail;

  }
}

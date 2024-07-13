package com.zerobase.hoops.commonResponse.swaggerSchema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class GameUserResponse {
  @Getter
  @AllArgsConstructor
  @Schema(name = "GiveMannerPoint", description = "매너포인트 점수 주기")
  public static class GiveMannerPoint implements SwaggerApiResponse {

    @Schema(description = "응답 메시지", example = "매너포인트 점수 주기")
    private String title;
    @Schema(description = "응답 상태", example = "Success")
    private String detail;

  }
}

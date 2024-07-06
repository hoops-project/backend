package com.zerobase.hoops.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FindDto {

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class findIdResponse {

    @Schema(description = "찾은 아이디", example = "hoops", defaultValue = "hoops")
    private String loginId;

  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class findPasswordResponse {

    @Schema(description = "비밀번호 찾기 결과", example = "true",
        defaultValue = "true")
    private boolean isFindPassword;

  }

}

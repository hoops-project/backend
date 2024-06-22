package com.zerobase.hoops.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckDto {

  @Schema(description = "중복 검사 결과", example = "true", defaultValue = "true")
  private boolean result;

  public static CheckDto fromBoolean(boolean result) {
    return CheckDto.builder()
        .result(result)
        .build();
  }
}

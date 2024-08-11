package com.zerobase.hoops.gameCreator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class RejectParticipantDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @Schema(
        description = "참여 pk",
        defaultValue = "2",
        requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "참여 아이디는 필수 값입니다.")
    private String participantId;
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    @Schema(description = "메세지", example = "오리 을(를) 경기에 거절 했습니다.")
    String message;

    public RejectParticipantDto.Response toDto(String message) {
      return RejectParticipantDto.Response.builder()
          .message(message)
          .build();
    }
  }

}

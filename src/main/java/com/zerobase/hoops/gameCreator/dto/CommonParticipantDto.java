package com.zerobase.hoops.gameCreator.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class CommonParticipantDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {
    @NotNull(message = "참가 아이디는 필수 값입니다.")
    @Min(1)
    private Long participantId;
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    String message;

    public CommonParticipantDto.Response toDto(String message) {
      return CommonParticipantDto.Response.builder()
          .message(message)
          .build();
    }
  }

}

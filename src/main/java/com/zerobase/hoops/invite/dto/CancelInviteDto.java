package com.zerobase.hoops.invite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class CancelInviteDto {

  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @Schema(
        description = "초대 pk",
        defaultValue = "1",
        requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "초대 아이디는 필수 값 입니다.")
    private String inviteId;

  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    @Schema(description = "메세지", example = "경기 초대 요청을 취소 했습니다.")
    String message;

    public Response toDto(String message) {
      return Response.builder()
          .message(message)
          .build();
    }
  }

}

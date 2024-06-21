package com.zerobase.hoops.invite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class AcceptInviteDto {

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
    @NotNull(message = "초대 아이디는 필수 값 입니다.")
    @Min(1)
    private Long inviteId;

  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    String message;

    public Response toDto(String message) {
      return Response.builder()
          .message(message)
          .build();
    }
  }

}

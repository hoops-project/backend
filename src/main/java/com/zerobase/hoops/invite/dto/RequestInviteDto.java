package com.zerobase.hoops.invite.dto;

import static com.zerobase.hoops.util.Common.getNowDateTime;

import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.document.InviteDocument;
import com.zerobase.hoops.document.UserDocument;
import com.zerobase.hoops.invite.type.InviteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class RequestInviteDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @Schema(
        description = "경기 초대 받는 유저 pk",
        defaultValue = "3",
        requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "받는 유저 아이디는 필수 입력 값 입니다.")
    private String receiverUserId;

    @Schema(
        description = "경기 pk",
        defaultValue = "1",
        requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "경기 아이디는 필수 입력 값 입니다.")
    private String gameId;

    public InviteDocument toDocument(
        UserDocument user,
        UserDocument receiverUser,
        GameDocument game,
        long inviteId,
        Clock clock
        ) {
      return InviteDocument.builder()
          .id(Long.toString(inviteId))
          .inviteStatus(InviteStatus.REQUEST)
          .requestedDateTime(getNowDateTime(clock))
          .senderUser(user)
          .receiverUser(receiverUser)
          .game(game)
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    @Schema(description = "메세지", example = "노량진근린공원에서 3:3할사람 모여라 에 "
        + "파브리 을(를) 경기 초대 요청 했습니다.")
    String message;

    public RequestInviteDto.Response toDto(String message) {
      return RequestInviteDto.Response.builder()
          .message(message)
          .build();
    }
  }

}

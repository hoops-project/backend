package com.zerobase.hoops.invite.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.invite.type.InviteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "받는 유저 아이디는 필수 입력 값 입니다.")
    @Min(1)
    private Long receiverUserId;

    @Schema(
        description = "경기 pk",
        defaultValue = "1",
        requiredMode = RequiredMode.REQUIRED)
    @NotNull(message = "경기 아이디는 필수 입력 값 입니다.")
    @Min(1)
    private Long gameId;

    public InviteEntity toEntity(
        UserEntity user,
        UserEntity receiverUser,
        GameEntity game) {
      return InviteEntity.builder()
          .inviteStatus(InviteStatus.REQUEST)
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

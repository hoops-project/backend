package com.zerobase.hoops.invite.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.invite.type.InviteStatus;
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

    @NotNull(message = "받는 유저 아이디는 필수 입력 값 입니다.")
    @Min(1)
    private Long receiverUserId;

    @NotNull(message = "게임 아이디는 필수 입력 값 입니다.")
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

}

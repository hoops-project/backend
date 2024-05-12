package com.zerobase.hoops.invite.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.dto.GameDto;
import com.zerobase.hoops.invite.type.InviteStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class InviteDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CreateRequest {

    @NotNull(message = "받는 유저 아이디는 필수 입력 값 입니다.")
    @Min(1)
    private Long receiverUserId;

    @NotNull(message = "게임 아이디는 필수 입력 값 입니다.")
    @Min(1)
    private Long gameId;

    public static InviteEntity toEntity(
        UserEntity user,
        UserEntity receiverUser,
        GameEntity game) {
      return InviteEntity.builder()
          .inviteStatus(InviteStatus.REQUEST)
          .senderUserEntity(user)
          .receiverUserEntity(receiverUser)
          .gameEntity(game)
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CreateResponse {

    private Long inviteId;

    private InviteStatus inviteStatus;

    private LocalDateTime requestedDatetime;

    private String senderUserNickName;

    private String receiverUserNickName;

    private String title;

    public static InviteDto.CreateResponse toDto(InviteEntity inviteEntity) {
      return CreateResponse.builder()
          .inviteId(inviteEntity.getInviteId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDatetime(inviteEntity.getRequestedDatetime())
          .senderUserNickName(inviteEntity.getSenderUserEntity().getNickName())
          .receiverUserNickName(inviteEntity.getReceiverUserEntity().getNickName())
          .title(inviteEntity.getGameEntity().getTitle())
          .build();
    }
  }

}

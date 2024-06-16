package com.zerobase.hoops.invite.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
          .senderUser(user)
          .receiverUser(receiverUser)
          .game(game)
          .build();
    }
  }

  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CommonRequest {

    @NotNull(message = "초대 아이디는 필수 값 입니다.")
    @Min(1)
    private Long inviteId;

  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class InviteMyListResponse {
    private Long inviteId;

    private LocalDate birthday;

    private GenderType gender;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    private Long gameId;


    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      InviteMyListResponse that = (InviteMyListResponse) o;
      return Objects.equals(inviteId, that.inviteId) &&
          Objects.equals(birthday, that.birthday) &&
          Objects.equals(gender, that.gender) &&
          Objects.equals(nickName, that.nickName) &&
          Objects.equals(playStyle, that.playStyle) &&
          Objects.equals(ability, that.ability) &&
          Objects.equals(mannerPoint, that.mannerPoint) &&
          Objects.equals(gameId, that.gameId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(inviteId, birthday, gender, nickName,
          playStyle, ability, mannerPoint, gameId);
    }

    public static InviteMyListResponse toDto(InviteEntity inviteEntity) {
      return InviteMyListResponse.builder()
          .inviteId(inviteEntity.getId())
          .birthday(inviteEntity.getSenderUser().getBirthday())
          .gender(inviteEntity.getSenderUser().getGender())
          .nickName(inviteEntity.getSenderUser().getNickName())
          .playStyle(inviteEntity.getSenderUser().getPlayStyle())
          .ability(inviteEntity.getSenderUser().getAbility())
          .mannerPoint(inviteEntity.getSenderUser().getStringAverageRating())
          .gameId(inviteEntity.getGame().getId())
          .build();
    }
  }

}

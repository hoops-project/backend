package com.zerobase.hoops.invite.dto;

import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class RequestInviteListDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
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
      Response that = (Response) o;
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

    public static Response toDto(InviteEntity inviteEntity) {
      return Response.builder()
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

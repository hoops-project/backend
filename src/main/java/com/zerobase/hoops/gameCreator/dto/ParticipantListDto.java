package com.zerobase.hoops.gameCreator.dto;

import com.zerobase.hoops.entity.ParticipantGameEntity;
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

public class ParticipantListDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    private Long participantId;

    private LocalDate birthday;

    private GenderType gender;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    public static Response toDto(ParticipantGameEntity participantGameEntity){
      return Response.builder()
          .participantId(participantGameEntity.getId())
          .birthday(participantGameEntity.getUser().getBirthday())
          .gender(participantGameEntity.getUser().getGender())
          .nickName(participantGameEntity.getUser().getNickName())
          .playStyle(participantGameEntity.getUser().getPlayStyle())
          .ability(participantGameEntity.getUser().getAbility())
          .mannerPoint(participantGameEntity.getUser().getStringAverageRating())
          .build();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Response that = (Response) o;
      return Objects.equals(participantId, that.participantId) &&
          Objects.equals(birthday, that.birthday) &&
          Objects.equals(gender, that.gender) &&
          Objects.equals(nickName, that.nickName) &&
          Objects.equals(playStyle, that.playStyle) &&
          Objects.equals(ability, that.ability) &&
          Objects.equals(mannerPoint, that.mannerPoint);
    }

    @Override
    public int hashCode() {
      return Objects.hash(participantId, birthday, gender, nickName,
          playStyle, ability, mannerPoint);
    }

  }

}

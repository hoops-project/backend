package com.zerobase.hoops.gameCreator.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class DetailGameDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    private Long gameId;

    private String title;

    private String content;

    private Long headCount;

    private FieldStatus fieldStatus;

    private Gender gender;

    private Boolean inviteYn;

    private String address;

    private String placeName;

    private Double latitude;

    private Double longitude;

    private CityName cityName;

    private MatchFormat matchFormat;

    private String nickName;

    private Long userId;

    private List<ParticipantUser> participantUserList;

    public Response toDto(GameEntity gameEntity,
        List<ParticipantUser> participantUserList) {
      return Response.builder()
          .gameId(gameEntity.getId())
          .title(gameEntity.getTitle())
          .content(gameEntity.getContent())
          .headCount(gameEntity.getHeadCount())
          .fieldStatus(gameEntity.getFieldStatus())
          .gender(gameEntity.getGender())
          .inviteYn(gameEntity.getInviteYn())
          .address(gameEntity.getAddress())
          .placeName(gameEntity.getPlaceName())
          .latitude(gameEntity.getLatitude())
          .longitude(gameEntity.getLongitude())
          .cityName(gameEntity.getCityName())
          .matchFormat(gameEntity.getMatchFormat())
          .nickName(gameEntity.getUser().getNickName())
          .userId(gameEntity.getUser().getId())
          .participantUserList(participantUserList)
          .build();
    }

    // 테스트 코드용 List 간 equals
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Response that = (Response) o;
      return Objects.equals(gameId, that.gameId) &&
          Objects.equals(title, that.title) &&
          Objects.equals(content, that.content) &&
          Objects.equals(headCount, that.headCount) &&
          Objects.equals(fieldStatus, that.fieldStatus) &&
          Objects.equals(gender, that.gender) &&
          Objects.equals(inviteYn, that.inviteYn) &&
          Objects.equals(address, that.address) &&
          Objects.equals(placeName, that.placeName) &&
          Objects.equals(latitude, that.latitude) &&
          Objects.equals(longitude, that.longitude) &&
          Objects.equals(cityName, that.cityName) &&
          Objects.equals(matchFormat, that.matchFormat) &&
          Objects.equals(nickName, that.nickName) &&
          Objects.equals(userId, that.userId) &&
          Objects.equals(participantUserList, that.participantUserList);
    }

    @Override
    public int hashCode() {
      return Objects.hash(gameId, title, content, headCount, fieldStatus,
          gender, inviteYn, address, placeName, latitude, longitude,
          cityName, matchFormat, nickName, userId, participantUserList);
    }

  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ParticipantUser {
    private Long userId;

    private LocalDate birthday;

    private GenderType genderType;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    public static ParticipantUser toDto(ParticipantGameEntity participantGame) {
      return ParticipantUser.builder()
          .userId(participantGame.getUser().getId())
          .birthday(participantGame.getUser().getBirthday())
          .genderType(participantGame.getUser().getGender())
          .nickName(participantGame.getUser().getNickName())
          .playStyle(participantGame.getUser().getPlayStyle())
          .ability(participantGame.getUser().getAbility())
          .mannerPoint(participantGame.getUser().getStringAverageRating())
          .build();
    }

    // 테스트 코드용 List 간 equals
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ParticipantUser that = (ParticipantUser) o;
      return Objects.equals(userId, that.userId) &&
          Objects.equals(birthday, that.birthday) &&
          Objects.equals(genderType, that.genderType) &&
          Objects.equals(nickName, that.nickName) &&
          Objects.equals(playStyle, that.playStyle) &&
          Objects.equals(ability, that.ability) &&
          Objects.equals(mannerPoint, that.mannerPoint);
    }

    @Override
    public int hashCode() {
      return Objects.hash(userId, birthday, genderType, nickName, playStyle,
          ability, mannerPoint);
    }
  }

}

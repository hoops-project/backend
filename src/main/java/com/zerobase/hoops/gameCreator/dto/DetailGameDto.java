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
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "경기 pk", example = "1")
    private Long gameId;

    @Schema(description = "제목", example = "이촌한강공원 농구장에서 3:3 할사람 모여라")
    private String title;

    @Schema(description = "규칙",
        example = "경기 시간: 단축된 시간 내에서 빠른 게임을 진행하기 위해 경기 시간을 10분으로 제한합니다.")
    private String content;

    @Schema(description = "인원수", example = "6")
    private Long headCount;

    @Schema(description = "실내외", example = "OUTDOOR")
    private FieldStatus fieldStatus;

    @Schema(description = "성별", example = "ALL")
    private Gender gender;

    @Schema(description = "친구 초대 여부", example = "true")
    private Boolean inviteYn;

    @Schema(description = "주소", example = "서울 용산구 이촌로72길 62")
    private String address;

    @Schema(description = "위치명", example = "이촌한강공원 농구장")
    private String placeName;

    @Schema(description = "위도", example = "37.51681737798186")
    private Double latitude;

    @Schema(description = "경도", example = "126.97220764602034")
    private Double longitude;

    @Schema(description = "도시명", example = "SEOUL")
    private CityName cityName;

    @Schema(description = "경기 형식", example = "THREEONTHREE")
    private MatchFormat matchFormat;

    @Schema(description = "경기 개설자 유저 닉네임", example = "구름")
    private String nickName;

    @Schema(description = "경기 개설자 유저 pk", example = "2")
    private Long userId;

    @Schema(description = "경기 참가자 리스트")
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

    @Schema(description = "경기 참가자 유저 pk", example = "2")
    private Long userId;

    @Schema(description = "경기 참가자 유저 생년월일", example = "2000-01-01")
    private LocalDate birthday;

    @Schema(description = "경기 참가자 유저 성별", example = "MALE")
    private GenderType genderType;

    @Schema(description = "경기 참가자 유저 닉네임", example = "구름")
    private String nickName;

    @Schema(description = "경기 참가자 유저 플레이스타일", example = "AGGRESSIVE")
    private PlayStyleType playStyle;

    @Schema(description = "경기 참가자 유저 능력", example = "PASS")
    private AbilityType ability;

    @Schema(description = "경기 참가자 유저 매너점수", example = "0")
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

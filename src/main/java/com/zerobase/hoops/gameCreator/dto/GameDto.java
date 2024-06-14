package com.zerobase.hoops.gameCreator.dto;


import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.gameCreator.validation.ValidStartTime;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class GameDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CreateRequest {

    @NotBlank(message = "제목은 필수 입력 값 입니다.")
    @Size(max = 50, message = "제목은 최대 50자 입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값 입니다.")
    @Size(max = 300, message = "내용은 최대 300자 입니다.")
    private String content;

    @NotNull(message = "인원 수는 필수 입력 값 입니다.")
    private Long headCount;

    @NotNull(message = "실내외는 필수 입력 값 입니다.")
    private FieldStatus fieldStatus;

    @NotNull(message = "성별은 필수 입력 값 입니다.")
    private Gender gender;

    @NotNull(message = "시작 날짜는 필수 입력 값 입니다.")
    @ValidStartTime
    private LocalDateTime startDateTime;

    @NotNull(message = "친구 초대 여부는 필수 입력 값 입니다.")
    private Boolean inviteYn;

    @NotBlank(message = "주소는 필수 입력 값 입니다.")
    @Size(max = 200, message = "주소는 최대 200자 입니다.")
    private String address;

    @NotBlank(message = "위치명 필수 입력 값 입니다.")
    private String placeName;

    @NotNull(message = "위도는 필수 입력 값 입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수 입력 값 입니다.")
    private Double longitude;

    @NotNull(message = "경기 형식은 필수 입력 값 입니다.")
    private MatchFormat matchFormat;

    public static GameEntity toEntity(CreateRequest request, UserEntity user) {
      return GameEntity.builder()
          .title(request.getTitle())
          .content(request.getContent())
          .headCount(request.getHeadCount())
          .fieldStatus(request.getFieldStatus())
          .gender(request.getGender())
          .startDateTime(request.getStartDateTime())
          .inviteYn(request.getInviteYn())
          .address(request.getAddress())
          .placeName(request.getPlaceName())
          .latitude(request.getLatitude())
          .longitude(request.getLongitude())
          .cityName(CityName.getCityName(request.getAddress()))
          .matchFormat(request.getMatchFormat())
          .user(user)
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DetailResponse {
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

    public static DetailResponse toDto(GameEntity gameEntity,
        List<ParticipantUser> participantUserList) {
      return DetailResponse.builder()
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
      GameDto.DetailResponse that = (GameDto.DetailResponse) o;
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
  public static class UpdateRequest {

    @NotNull(message = "게임 아이디는 필수 입력 값 입니다.")
    private Long gameId;

    @NotBlank(message = "제목은 필수 입력 값 입니다.")
    @Size(max = 50, message = "제목은 최대 50자 입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값 입니다.")
    @Size(max = 300, message = "내용은 최대 300자 입니다.")
    private String content;

    @NotNull(message = "인원 수는 필수 입력 값 입니다.")
    private Long headCount;

    @NotNull(message = "실내외는 필수 입력 값 입니다.")
    private FieldStatus fieldStatus;

    @NotNull(message = "성별은 필수 입력 값 입니다.")
    private Gender gender;

    @NotNull(message = "시작 날짜는 필수 입력 값 입니다.")
    @ValidStartTime
    private LocalDateTime startDateTime;

    @NotNull(message = "친구 초대 여부는 필수 입력 값 입니다.")
    private Boolean inviteYn;

    @NotBlank(message = "주소는 필수 입력 값 입니다.")
    @Size(max = 200, message = "주소는 최대 200자 입니다.")
    private String address;

    @NotBlank(message = "위치명 필수 입력 값 입니다.")
    private String placeName;

    @NotNull(message = "위도는 필수 입력 값 입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수 입력 값 입니다.")
    private Double longitude;

    @NotNull(message = "경기 형식은 필수 입력 값 입니다.")
    private MatchFormat matchFormat;

    public static GameEntity toEntity(UpdateRequest request, GameEntity game) {
      return GameEntity.builder()
          .id(request.getGameId())
          .title(request.getTitle())
          .content(request.getContent())
          .headCount(request.getHeadCount())
          .fieldStatus(request.getFieldStatus())
          .gender(request.getGender())
          .startDateTime(request.getStartDateTime())
          .createdDateTime(game.getCreatedDateTime())
          .inviteYn(request.getInviteYn())
          .address(request.getAddress())
          .placeName(request.getPlaceName())
          .latitude(request.getLatitude())
          .longitude(request.getLongitude())
          .cityName(CityName.getCityName(request.getAddress()))
          .matchFormat(request.getMatchFormat())
          .user(game.getUser())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DeleteRequest {

    @NotNull(message = "게임 아이디는 필수 입력 값 입니다.")
    private Long gameId;

    public static GameEntity toEntity(GameEntity game, Clock clock) {
      return GameEntity.builder()
          .id(game.getId())
          .title(game.getTitle())
          .content(game.getContent())
          .headCount(game.getHeadCount())
          .fieldStatus(game.getFieldStatus())
          .gender(game.getGender())
          .startDateTime(game.getStartDateTime())
          .createdDateTime(game.getCreatedDateTime())
          .deletedDateTime(LocalDateTime.now(clock))
          .inviteYn(game.getInviteYn())
          .address(game.getAddress())
          .placeName(game.getPlaceName())
          .latitude(game.getLatitude())
          .longitude(game.getLongitude())
          .cityName(game.getCityName())
          .matchFormat(game.getMatchFormat())
          .user(game.getUser())
          .build();
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
      GameDto.ParticipantUser that = (GameDto.ParticipantUser) o;
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

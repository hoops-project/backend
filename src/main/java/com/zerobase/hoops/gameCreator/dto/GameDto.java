package com.zerobase.hoops.gameCreator.dto;


import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
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
          .userEntity(user)
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CreateResponse {

    private Long gameId;

    private String title;

    private String content;

    private Long headCount;

    private FieldStatus fieldStatus;

    private Gender gender;

    private LocalDateTime startDateTime;

    private LocalDateTime createdDateTime;

    private Boolean inviteYn;

    private String address;

    private Double latitude;

    private Double longitude;

    private CityName cityName;

    private MatchFormat matchFormat;

    public static CreateResponse toDto(GameEntity gameEntity) {
      return CreateResponse.builder()
          .gameId(gameEntity.getGameId())
          .title(gameEntity.getTitle())
          .content(gameEntity.getContent())
          .headCount(gameEntity.getHeadCount())
          .fieldStatus(gameEntity.getFieldStatus())
          .gender(gameEntity.getGender())
          .startDateTime(gameEntity.getStartDateTime())
          .createdDateTime(gameEntity.getCreatedDateTime())
          .inviteYn(gameEntity.getInviteYn())
          .address(gameEntity.getAddress())
          .latitude(gameEntity.getLatitude())
          .longitude(gameEntity.getLongitude())
          .cityName(gameEntity.getCityName())
          .matchFormat(gameEntity.getMatchFormat())
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

    private LocalDateTime startDateTime;

    private LocalDateTime createdDateTime;

    private LocalDateTime deletedDateTime;

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
          .gameId(gameEntity.getGameId())
          .title(gameEntity.getTitle())
          .content(gameEntity.getContent())
          .headCount(gameEntity.getHeadCount())
          .fieldStatus(gameEntity.getFieldStatus())
          .gender(gameEntity.getGender())
          .startDateTime(gameEntity.getStartDateTime())
          .createdDateTime(gameEntity.getCreatedDateTime())
          .deletedDateTime(gameEntity.getDeletedDateTime())
          .inviteYn(gameEntity.getInviteYn())
          .address(gameEntity.getAddress())
          .placeName(gameEntity.getPlaceName())
          .latitude(gameEntity.getLatitude())
          .longitude(gameEntity.getLongitude())
          .cityName(gameEntity.getCityName())
          .matchFormat(gameEntity.getMatchFormat())
          .nickName(gameEntity.getUserEntity().getNickName())
          .userId(gameEntity.getUserEntity().getUserId())
          .participantUserList(participantUserList)
          .build();
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
    private LocalDateTime startDateTime;

    @NotNull(message = "친구 초대 여부는 필수 입력 값 입니다.")
    private Boolean inviteYn;

    @NotBlank(message = "주소는 필수 입력 값 입니다.")
    @Size(max = 200, message = "주소는 최대 200자 입니다.")
    private String address;

    @NotNull(message = "위도는 필수 입력 값 입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수 입력 값 입니다.")
    private Double longitude;

    @NotNull(message = "경기 형식은 필수 입력 값 입니다.")
    private MatchFormat matchFormat;

    public static GameEntity toEntity(UpdateRequest request, GameEntity game) {
      return GameEntity.builder()
          .gameId(request.getGameId())
          .title(request.getTitle())
          .content(request.getContent())
          .headCount(request.getHeadCount())
          .fieldStatus(request.getFieldStatus())
          .gender(request.getGender())
          .startDateTime(request.getStartDateTime())
          .createdDateTime(game.getCreatedDateTime())
          .inviteYn(request.getInviteYn())
          .address(request.getAddress())
          .latitude(request.getLatitude())
          .longitude(request.getLongitude())
          .cityName(CityName.getCityName(request.getAddress()))
          .matchFormat(request.getMatchFormat())
          .userEntity(game.getUserEntity())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class UpdateResponse {
    private Long gameId;

    private String title;

    private String content;

    private Long headCount;

    private FieldStatus fieldStatus;

    private Gender gender;

    private LocalDateTime startDateTime;

    private LocalDateTime createdDateTime;

    private Boolean inviteYn;

    private String address;

    private Double latitude;

    private Double longitude;

    private CityName cityName;

    private MatchFormat matchFormat;

    public static UpdateResponse toDto(GameEntity gameEntity) {
      return UpdateResponse.builder()
          .gameId(gameEntity.getGameId())
          .title(gameEntity.getTitle())
          .content(gameEntity.getContent())
          .headCount(gameEntity.getHeadCount())
          .fieldStatus(gameEntity.getFieldStatus())
          .gender(gameEntity.getGender())
          .startDateTime(gameEntity.getStartDateTime())
          .createdDateTime(gameEntity.getCreatedDateTime())
          .inviteYn(gameEntity.getInviteYn())
          .address(gameEntity.getAddress())
          .latitude(gameEntity.getLatitude())
          .longitude(gameEntity.getLongitude())
          .cityName(gameEntity.getCityName())
          .matchFormat(gameEntity.getMatchFormat())
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

    public static GameEntity toEntity(GameEntity game) {
      return GameEntity.builder()
          .gameId(game.getGameId())
          .title(game.getTitle())
          .content(game.getContent())
          .headCount(game.getHeadCount())
          .fieldStatus(game.getFieldStatus())
          .gender(game.getGender())
          .startDateTime(game.getStartDateTime())
          .createdDateTime(game.getCreatedDateTime())
          .deletedDateTime(LocalDateTime.now())
          .inviteYn(game.getInviteYn())
          .address(game.getAddress())
          .latitude(game.getLatitude())
          .longitude(game.getLongitude())
          .cityName(game.getCityName())
          .matchFormat(game.getMatchFormat())
          .userEntity(game.getUserEntity())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DeleteGameResponse {

    private Long gameId;

    private String title;

    private String content;

    private Long headCount;

    private FieldStatus fieldStatus;

    private Gender gender;

    private LocalDateTime startDateTime;

    private LocalDateTime createdDateTime;

    private LocalDateTime deletedDateTime;

    private Boolean inviteYn;

    private String address;

    private Double latitude;

    private Double longitude;

    private CityName cityName;

    private MatchFormat matchFormat;

    public static DeleteGameResponse toDto(GameEntity gameEntity) {
      return DeleteGameResponse.builder()
          .gameId(gameEntity.getGameId())
          .title(gameEntity.getTitle())
          .content(gameEntity.getContent())
          .headCount(gameEntity.getHeadCount())
          .fieldStatus(gameEntity.getFieldStatus())
          .gender(gameEntity.getGender())
          .startDateTime(gameEntity.getStartDateTime())
          .createdDateTime(gameEntity.getCreatedDateTime())
          .deletedDateTime(gameEntity.getDeletedDateTime())
          .inviteYn(gameEntity.getInviteYn())
          .address(gameEntity.getAddress())
          .latitude(gameEntity.getLatitude())
          .longitude(gameEntity.getLongitude())
          .cityName(gameEntity.getCityName())
          .matchFormat(gameEntity.getMatchFormat())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class WithDrawGameResponse {

    private ParticipantGameStatus status;

    private LocalDateTime withdrewDateTime;

    private Long gameId;

    private Long userId;

    public static WithDrawGameResponse toDto(ParticipantGameEntity entity) {
      return WithDrawGameResponse.builder()
          .status(entity.getStatus())
          .withdrewDateTime(entity.getWithdrewDateTime())
          .gameId(entity.getGameEntity().getGameId())
          .userId(entity.getUserEntity().getUserId())
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

    private GenderType genderType;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    public static ParticipantUser toDto(ParticipantGameEntity entity) {
      return ParticipantUser.builder()
          .userId(entity.getUserEntity().getUserId())
          .genderType(entity.getUserEntity().getGender())
          .nickName(entity.getUserEntity().getNickName())
          .playStyle(entity.getUserEntity().getPlayStyle())
          .ability(entity.getUserEntity().getAbility())
          .mannerPoint(entity.getUserEntity().getStringAverageRating())
          .build();
    }
  }

}

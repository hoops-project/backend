package com.zerobase.hoops.gameCreator.dto;


import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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

    private String title;

    private String content;

    private Long headCount;

    private FieldStatus fieldStatus;

    private Gender gender;

    private LocalDateTime startDateTime;

    private LocalDateTime createdDateTime;

    private Boolean inviteYn;

    private String address;

    private CityName cityName;

    private MatchFormat matchFormat;

    public static CreateResponse toDto(GameEntity gameEntity) {
      return CreateResponse.builder()
          .title(gameEntity.getTitle())
          .content(gameEntity.getContent())
          .headCount(gameEntity.getHeadCount())
          .fieldStatus(gameEntity.getFieldStatus())
          .gender(gameEntity.getGender())
          .startDateTime(gameEntity.getStartDateTime())
          .createdDateTime(gameEntity.getCreatedDateTime())
          .inviteYn(gameEntity.getInviteYn())
          .address(gameEntity.getAddress())
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

    @NotNull(message = "경기 형식은 필수 입력 값 입니다.")
    private MatchFormat matchFormat;

  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class UpdateResponse {

    private String title;

    private String content;

    private Long headCount;

    private FieldStatus fieldStatus;

    private Gender gender;

    private LocalDateTime startDateTime;

    private LocalDateTime createdDateTime;

    private Boolean inviteYn;

    private String address;

    private CityName cityName;

    private MatchFormat matchFormat;

    public static UpdateResponse toDto(GameEntity gameEntity) {
      return UpdateResponse.builder()
          .title(gameEntity.getTitle())
          .content(gameEntity.getContent())
          .headCount(gameEntity.getHeadCount())
          .fieldStatus(gameEntity.getFieldStatus())
          .gender(gameEntity.getGender())
          .startDateTime(gameEntity.getStartDateTime())
          .createdDateTime(gameEntity.getCreatedDateTime())
          .inviteYn(gameEntity.getInviteYn())
          .address(gameEntity.getAddress())
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

  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DeleteResponse {

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

    private CityName cityName;

    private MatchFormat matchFormat;

    public static DeleteResponse toDto(GameEntity gameEntity) {
      return DeleteResponse.builder()
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
          .cityName(gameEntity.getCityName())
          .matchFormat(gameEntity.getMatchFormat())
          .build();
    }
  }

}

package com.zerobase.hoops.gameCreator.dto;

import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.document.UserDocument;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.validation.ValidStartTime;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class CreateGameDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @Schema(
        description = "제목",
        defaultValue = "이촌한강공원 농구장에서 3:3 할사람 모여라",
        maxLength = 50,
        requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "제목은 필수 입력 값 입니다.")
    @Size(max = 50, message = "제목은 최대 50자 입니다.")
    private String title;

    @Schema(
        description = "규칙",
        defaultValue = "경기 시간: 단축된 시간 내에서 빠른 게임을 진행하기 위해 경기 시간을 10분으로 제한합니다.",
        maxLength = 300,
        requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "내용은 필수 입력 값 입니다.")
    @Size(max = 300, message = "내용은 최대 300자 입니다.")
    private String content;

    @Schema(description = "인원수",
        defaultValue = "9",
        requiredMode = RequiredMode.REQUIRED)
    @NotNull(message = "인원 수는 필수 입력 값 입니다.")
    private Long headCount;

    @Schema(description = "실내외",
        defaultValue = "OUTDOOR",
        requiredMode = RequiredMode.REQUIRED)
    @NotNull(message = "실내외는 필수 입력 값 입니다.")
    private FieldStatus fieldStatus;

    @Schema(description = "성별",
        defaultValue = "ALL",
        requiredMode = RequiredMode.REQUIRED)
    @NotNull(message = "성별은 필수 입력 값 입니다.")
    private Gender gender;

    @Schema(description = "시작날짜",
        defaultValue = "2026-01-01T10:00:00",
        requiredMode = RequiredMode.REQUIRED)
    @NotNull(message = "시작 날짜는 필수 입력 값 입니다.")
    @ValidStartTime
    private OffsetDateTime startDateTime;

    @Schema(description = "친구 초대 여부",
        defaultValue = "true",
        requiredMode = RequiredMode.REQUIRED)
    @NotNull(message = "친구 초대 여부는 필수 입력 값 입니다.")
    private Boolean inviteYn;

    @Schema(description = "주소",
        defaultValue = "서울 용산구 이촌로72길 62",
        maxLength = 200,
        requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "주소는 필수 입력 값 입니다.")
    @Size(max = 200, message = "주소는 최대 200자 입니다.")
    private String address;

    @Schema(description = "위치명",
        defaultValue = "이촌한강공원 농구장",
        maxLength = 100,
        requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "위치명 필수 입력 값 입니다.")
    @Size(max = 100, message = "위치명은 최대 100자 입니다.")
    private String placeName;

    @Schema(description = "위도",
        defaultValue = "37.51681737798186",
        requiredMode = RequiredMode.REQUIRED)
    @NotNull(message = "위도는 필수 입력 값 입니다.")
    private Double latitude;

    @Schema(description = "경도",
        defaultValue = "126.97220764602034",
        requiredMode = RequiredMode.REQUIRED)
    @NotNull(message = "경도는 필수 입력 값 입니다.")
    private Double longitude;

    @Schema(description = "경기 형식",
        defaultValue = "THREEONTHREE",
        requiredMode = RequiredMode.REQUIRED)
    @NotNull(message = "경기 형식은 필수 입력 값 입니다.")
    private MatchFormat matchFormat;

    public GameDocument toDocument(Request request, UserDocument user,
        long gameId, OffsetDateTime nowDateTime) {
      return GameDocument.builder()
          .id(Long.toString(gameId))
          .title(request.getTitle())
          .content(request.getContent())
          .headCount(request.getHeadCount())
          .fieldStatus(request.getFieldStatus())
          .gender(request.getGender())
          .startDateTime(request.getStartDateTime())
          .createdDateTime(nowDateTime)
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
  public static class Response {

    @Schema(description = "메세지", example = "이촌한강공원 농구장에서 3:3 할사람 모여라 경기가 생성되었습니다.")
    String message;

    public CreateGameDto.Response toDto(String message) {
      return CreateGameDto.Response.builder()
          .message(message)
          .build();
    }
  }

}

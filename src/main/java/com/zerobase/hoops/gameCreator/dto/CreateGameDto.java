package com.zerobase.hoops.gameCreator.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.validation.ValidStartTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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

    public GameEntity toEntity(Request request, UserEntity user) {
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

}

package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameSearchResponse {

  @Schema(description = "게임 엔티티 pk", example = "3")
  private Long gameId;

  @Schema(description = "게임 개최자 유저 pk", example = "7")
  private Long gameOwnerId;

  @Schema(description = "유저 본인 pk", example = "12")
  private Long myId;

  @Schema(description = "게임 방제", example = "오늘 농구 경기 한판?!")
  private String title;

  @Schema(description = "경기 규칙이나 세부사항", example = "매너 게임이요!, 경기중 욕설 금지")
  private String content;

  @Schema(description = "인원 제한", example = "10")
  private Long headCount;
  private FieldStatus fieldStatus;
  private Gender gender;
  private LocalDateTime startDateTime;
  private LocalDateTime createdDateTime;
  private LocalDateTime deletedDateTime;
  private Boolean inviteYn;

  @Schema(description = "경기 주소", example = "서울특별시 강남구 강남대로 328 농구경기장")
  private String address;
  private Double latitude;
  private Double longitude;
  private CityName cityName;
  private MatchFormat matchFormat;

  public static GameSearchResponse of(GameEntity gameEntity, Long userId) {
    return GameSearchResponse.builder()
        .gameId(gameEntity.getId())
        .gameOwnerId(gameEntity.getUser().getId())
        .myId(userId)
        .title(gameEntity.getTitle())
        .content(gameEntity.getContent())
        .headCount(gameEntity.getHeadCount())
        .fieldStatus(gameEntity.getFieldStatus())
        .gender(gameEntity.getGender())
        .startDateTime(gameEntity.getStartDateTime())
        .inviteYn(gameEntity.getInviteYn())
        .address(gameEntity.getAddress())
        .latitude(gameEntity.getLatitude())
        .longitude(gameEntity.getLongitude())
        .cityName(gameEntity.getCityName())
        .matchFormat(gameEntity.getMatchFormat())
        .build();
  }
}

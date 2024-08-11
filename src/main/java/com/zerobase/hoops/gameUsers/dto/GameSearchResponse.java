package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameSearchResponse {

  @Schema(description = "게임 엔티티 pk", example = "3")
  private String gameId;

  @Schema(description = "게임 개최자 유저 pk", example = "7")
  private String gameOwnerId;

  @Schema(description = "유저 본인 pk", example = "12")
  private String myId;

  @Schema(description = "게임 방제", example = "오늘 농구 경기 한판?!")
  private String title;

  @Schema(description = "경기 규칙이나 세부사항", example = "매너 게임이요!, 경기중 욕설 금지")
  private String content;

  @Schema(description = "인원 제한", example = "10")
  private Long headCount;
  private FieldStatus fieldStatus;
  private Gender gender;
  private OffsetDateTime startDateTime;
  private OffsetDateTime createdDateTime;
  private OffsetDateTime deletedDateTime;
  private Boolean inviteYn;

  @Schema(description = "경기 주소", example = "서울특별시 강남구 강남대로 328 농구경기장")
  private String address;
  private Double latitude;
  private Double longitude;
  private CityName cityName;
  private MatchFormat matchFormat;

  public static GameSearchResponse of(GameDocument game, String userId) {
    return GameSearchResponse.builder()
        .gameId(game.getId())
        .gameOwnerId(game.getUser().getId())
        .myId(userId)
        .title(game.getTitle())
        .content(game.getContent())
        .headCount(game.getHeadCount())
        .fieldStatus(game.getFieldStatus())
        .gender(game.getGender())
        .startDateTime(game.getStartDateTime())
        .inviteYn(game.getInviteYn())
        .address(game.getAddress())
        .latitude(game.getLatitude())
        .longitude(game.getLongitude())
        .cityName(game.getCityName())
        .matchFormat(game.getMatchFormat())
        .build();
  }
}

package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameSearchResponse {

  private Long gameId;
  private Long gameOwnerId;
  private Long myId;
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

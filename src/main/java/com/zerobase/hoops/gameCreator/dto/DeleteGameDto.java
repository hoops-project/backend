package com.zerobase.hoops.gameCreator.dto;

import com.zerobase.hoops.entity.GameEntity;
import jakarta.validation.constraints.NotNull;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class DeleteGameDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @NotNull(message = "게임 아이디는 필수 입력 값 입니다.")
    private Long gameId;

    public GameEntity toEntity(GameEntity game, Clock clock) {
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

}

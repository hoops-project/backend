package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.entity.ParticipantGameEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MannerPointListResponse {

  private Long gameId;
  private String title;
  private String address;
  private String player;
  private Long playerId;

  public static MannerPointListResponse of(
      ParticipantGameEntity participantGame) {
    return MannerPointListResponse.builder()
        .gameId(participantGame.getGame().getId())
        .title(participantGame.getGame().getTitle())
        .address(participantGame.getGame().getAddress())
        .player(participantGame.getUser().getNickName())
        .playerId(participantGame.getUser().getId())
        .build();
  }
}

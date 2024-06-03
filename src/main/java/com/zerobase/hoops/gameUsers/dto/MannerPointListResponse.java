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
        .gameId(participantGame.getGameEntity().getGameId())
        .title(participantGame.getGameEntity().getTitle())
        .address(participantGame.getGameEntity().getAddress())
        .player(participantGame.getUserEntity().getNickName())
        .playerId(participantGame.getUserEntity().getId())
        .build();
  }
}

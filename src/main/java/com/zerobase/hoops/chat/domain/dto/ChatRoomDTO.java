package com.zerobase.hoops.chat.domain.dto;

import com.zerobase.hoops.entity.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDTO {

  private Long roomId;
  private Long gameId;

  public static ChatRoomDTO entityToDto(ChatRoomEntity entity) {
    return ChatRoomDTO.builder()
        .roomId(entity.getRoomId())
        .gameId(entity.getGameEntity().getGameId())
        .build();
  }
}

package com.zerobase.hoops.chat.domain.dto;

import com.zerobase.hoops.entity.MessageEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {

  private Long roomId;
  private String content;
  private LocalDateTime sendDateTime;
  private String sender;

  public static MessageDTO entityToDto(MessageEntity message) {
    return MessageDTO.builder()
        .roomId(message.getChatRoomEntity().getRoomId())
        .content(message.getContent())
        .sendDateTime(message.getSendDateTime())
        .sender(message.getUser().getNickName())
        .build();
  }
}


package com.zerobase.hoops.chat.dto;

import com.zerobase.hoops.entity.ChatRoomEntity;
import com.zerobase.hoops.entity.MessageEntity;
import com.zerobase.hoops.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {

  @NotBlank
  private String content;

  public MessageEntity toEntity(UserEntity user, ChatRoomEntity chatRoom) {
    return MessageEntity.builder()
        .user(user)
        .content(content)
        .chatRoomEntity(chatRoom)
        .sendDateTime(LocalDateTime.now())
        .build();
  }
}

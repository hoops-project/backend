package com.zerobase.hoops.chat.dto;

import static com.zerobase.hoops.util.Common.getNowDateTime;

import com.zerobase.hoops.document.ChatRoomDocument;
import com.zerobase.hoops.document.MessageDocument;
import com.zerobase.hoops.document.UserDocument;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {

  @NotBlank
  private String content;

  public MessageDocument toDocument(UserDocument user,
      ChatRoomDocument chatRoom,
      long messageId) {
    return MessageDocument.builder()
        .id(Long.toString(messageId))
        .user(user)
        .content(content)
        .chatRoom(chatRoom)
        .sendDateTime(getNowDateTime())
        .build();
  }
}

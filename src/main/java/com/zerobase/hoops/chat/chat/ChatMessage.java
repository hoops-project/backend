package com.zerobase.hoops.chat.chat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessage {

  private MessageType type;
  private String content;
  private String sender;

}

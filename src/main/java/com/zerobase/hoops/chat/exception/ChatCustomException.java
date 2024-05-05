package com.zerobase.hoops.chat.exception;

import lombok.Getter;

@Getter
public class ChatCustomException extends RuntimeException{

  private final ChatErrorCode chatErrorCode;

  public ChatCustomException(ChatErrorCode chatErrorCode) {
    super(chatErrorCode.getDetail());
    this.chatErrorCode = chatErrorCode;
  }
}

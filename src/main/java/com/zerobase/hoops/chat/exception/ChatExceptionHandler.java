package com.zerobase.hoops.chat.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ChatExceptionHandler {

  @ExceptionHandler({
      ChatCustomException.class
  })
  public ResponseEntity<CommentExceptionResponse> commentCustomExceptionHandler(
      final ChatCustomException c
  ) {
    log.warn("ChatException   :   {}", c.getChatErrorCode());
    return ResponseEntity.badRequest().body(
        new CommentExceptionResponse(c.getMessage(),c.getChatErrorCode())
    );
  }


  @Getter
  @ToString
  @AllArgsConstructor
  public static class CommentExceptionResponse {
    private String message;
    private ChatErrorCode chatErrorCode;
  }
}

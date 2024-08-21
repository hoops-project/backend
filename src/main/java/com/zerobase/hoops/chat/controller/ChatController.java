package com.zerobase.hoops.chat.controller;

import com.zerobase.hoops.chat.chat.ChatMessage;
import com.zerobase.hoops.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

  private final SimpMessagingTemplate messagingTemplate;
  private final ChatService chatService;

  @MessageMapping("/sendMessage/{gameId}")
  public void sendMessage(
      @Payload ChatMessage chatMessage,
      @DestinationVariable String gameId,
      StompHeaderAccessor headerAccessor
  ) {
    String token = headerAccessor.getFirstNativeHeader("Authorization");
    chatService.sendMessage(chatMessage, gameId, token);
  }


  @MessageMapping("/addUser/{gameId}")
  public void addUser(
      @Payload ChatMessage chatMessage,
      @DestinationVariable String gameId,
      StompHeaderAccessor headerAccessor
  ) {
    String token = headerAccessor.getFirstNativeHeader("Authorization");
    chatService.addUser(chatMessage, gameId, token);
  }

  @MessageMapping("/loadMessages/{gameId}")
  public void loadMessages(@DestinationVariable String gameId,
      StompHeaderAccessor headerAccessor) {
    String token = headerAccessor.getFirstNativeHeader("Authorization");
    chatService.loadMessagesAndSend(gameId, token);
  }
}



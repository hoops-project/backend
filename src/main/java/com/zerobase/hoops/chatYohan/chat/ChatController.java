package com.zerobase.hoops.chatYohan.chat;

import com.zerobase.hoops.security.JwtTokenExtract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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
  private final JwtTokenExtract jwtTokenExtract;

  @MessageMapping("/sendMessage/{gameId}")
  public void sendMessage(
      @Payload ChatMessage chatMessage,
      @DestinationVariable String gameId
  ) {
    messagingTemplate.convertAndSend("/topic/" + gameId, chatMessage);
  }


  @MessageMapping("/addUser/{gameId}")
  public void addUser(
      @Payload ChatMessage chatMessage,
      StompHeaderAccessor headerAccessor,
      @DestinationVariable String gameId
  ) {
    String nickName = jwtTokenExtract.currentUser().getNickName();
    log.info(nickName);
    headerAccessor.getSessionAttributes()
        .put("username", nickName);
    headerAccessor.getSessionAttributes().put("gameId", gameId);
    messagingTemplate.convertAndSend("/topic/" + gameId, chatMessage);
  }
}

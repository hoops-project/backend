package com.zerobase.hoops.config;

import com.zerobase.hoops.chat.chat.ChatMessage;
import com.zerobase.hoops.chat.chat.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketConnectionEventListener {

  private final SimpMessageSendingOperations messageTemplate;

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String username = (String)headerAccessor.getSessionAttributes().get("username");
    String gameId = (String) headerAccessor.getSessionAttributes().get("gameId");

    if (username != null && gameId != null) {
      ChatMessage chatMessage = ChatMessage.builder()
          .type(MessageType.LEAVE)
          .sender(username)
          .build();
      messageTemplate.convertAndSend("/topic/" + gameId, chatMessage);
    }
  }
}

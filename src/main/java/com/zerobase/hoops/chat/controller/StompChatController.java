package com.zerobase.hoops.chat.controller;

import com.zerobase.hoops.chat.domain.dto.ChatRoomDTO;
import com.zerobase.hoops.chat.domain.dto.Content;
import com.zerobase.hoops.chat.domain.dto.MessageDTO;
import com.zerobase.hoops.chat.service.ChatService;
import com.zerobase.hoops.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompChatController {

  private final ChatService chatService;
  private final SimpMessageSendingOperations messagingTemplate;

  @MessageMapping("/enter/{gameId}")
  public void enter(@DestinationVariable Long gameId
      , SimpMessageHeaderAccessor accessor
  ) {

    String senderId = (String) accessor.getSessionAttributes()
        .get("senderId");

    UserEntity user = chatService.findUser(senderId);

    messagingTemplate.convertAndSend("/sub/" + gameId,
        user.getNickName() + "님이 입장했습니다");
  }

  @MessageMapping("/send/{gameId}")
  public void chat(@DestinationVariable Long gameId
      , @Payload Content content
      , SimpMessageHeaderAccessor accessor
  ) {
    String userLoginId = (String) accessor.getSessionAttributes()
        .get("senderId");

    MessageDTO messageDTO = chatService.createMessage(gameId, content,
        userLoginId);

    messagingTemplate.convertAndSend("/sub/" + gameId, messageDTO);
  }

  @PostMapping("/chat/create")
  public ResponseEntity<ChatRoomDTO> createChatRoom(
      @RequestBody Long gameId) {
    ChatRoomDTO roomDTO = chatService.createChatRoom(gameId);

    return ResponseEntity.ok(roomDTO);
  }

  /**
   * 새로운 사용자가 웹 소켓을 연결할 때 실행됨
   *
   * @param event
   */
  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = accessor.getSessionId();
    log.info("new connected sessionId : " + sessionId);
  }

  /**
   * 사용자가 웹 소켓 연결을 끊으면 실행됨
   *
   * @param event
   */
  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = accessor.getSessionId();

    log.info("sessionId Disconnected : " + sessionId);
  }
}

package com.zerobase.hoops.config;

import com.zerobase.hoops.chat.chat.ChatMessage;
import com.zerobase.hoops.chat.chat.MessageType;
import com.zerobase.hoops.chat.repository.ChatRoomRepository;
import com.zerobase.hoops.entity.ChatRoomEntity;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectionEventListener {

  private final SimpMessageSendingOperations messageTemplate;
  private final ChatRoomRepository chatRoomRepository;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(
        event.getMessage());
    String sessionId = headerAccessor.getSessionId();
    String nickName = headerAccessor.getFirstNativeHeader("nickName");
    String gameId = headerAccessor.getFirstNativeHeader("gameId");
    log.info("headerAccessor{}", headerAccessor);
    log.info("Connect event: sessionId={}, nickName={}, gameId={}",
        sessionId, nickName, gameId);

    if (nickName != null && gameId != null) {
      if (headerAccessor.getSessionAttributes() == null) {
        headerAccessor.setSessionAttributes(new ConcurrentHashMap<>());
      }
      headerAccessor.getSessionAttributes().put("nickName", nickName);
      headerAccessor.getSessionAttributes().put("gameId", gameId);
      log.info(
          "Session attributes set: sessionId={}, nickName={}, gameId={}",
          sessionId, nickName, gameId);
    } else {
      log.error("NickName or GameId is null");
    }
  }

  @EventListener
  public void handleWebSocketDisconnectListener(
      SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(
        event.getMessage());
    Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
    if (sessionAttributes != null) {
      String username = (String) sessionAttributes.get("nickName");
      String gameId = (String) sessionAttributes.get("gameId");

      Long gameIdNumber = null;
      if (gameId != null && !gameId.isEmpty()) {
        try {
          gameIdNumber = Long.parseLong(gameId);
        } catch (NumberFormatException e) {
          log.warn("Invalid user ID format: {}", gameId);
        }
      } else {
        log.warn("User ID is null or empty");
      }

      log.info("username={}", username);
      log.info("gameId={}", gameIdNumber);
      if (username != null) {
        List<ChatRoomEntity> chatRoomEntityList = chatRoomRepository.findByGameEntity_Id(
            gameIdNumber);
        for (ChatRoomEntity chatRoomEntity : chatRoomEntityList) {
          String nickName = chatRoomEntity.getUserEntity().getNickName();
          ChatMessage chatMessage = ChatMessage.builder()
              .type(MessageType.LEAVE)
              .sender(username)
              .build();
          messageTemplate.convertAndSend(
              "/topic/" + gameId + "/" + nickName,
              chatMessage);
        }
      }
    } else {
      log.error("Session attributes are null");
    }
  }
}

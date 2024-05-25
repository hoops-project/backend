package com.zerobase.hoops.chat.controller;

import com.zerobase.hoops.chat.domain.dto.ChatRoomDTO;
import com.zerobase.hoops.chat.domain.dto.Content;
import com.zerobase.hoops.chat.domain.dto.CreateRoomDTO;
import com.zerobase.hoops.chat.domain.dto.MessageDTO;
import com.zerobase.hoops.chat.service.ChatService;
import com.zerobase.hoops.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequiredArgsConstructor
@Slf4j
public class StompChatController {

  private final ChatService chatService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/enter/{gameId}")
  public void enter(@DestinationVariable Long gameId
      , SimpMessageHeaderAccessor accessor
  ) {

    String senderId = (String) accessor.getSessionAttributes()
        .get("senderId");

    UserEntity user = chatService.findUser(senderId);

    chatService.checkAcceptUser(gameId, user.getUserId());

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

  @PostMapping("/api/chat/create")
  public ResponseEntity<ChatRoomDTO> createChatRoom(
      @RequestBody CreateRoomDTO createRoomDTO) {
    ChatRoomDTO roomDTO = chatService.createChatRoom(createRoomDTO);

    return ResponseEntity.ok(roomDTO);
  }


}

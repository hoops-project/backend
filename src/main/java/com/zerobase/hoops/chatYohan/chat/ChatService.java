package com.zerobase.hoops.chatYohan.chat;

import com.zerobase.hoops.chatYohan.repository.ChatRoomRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatRoomRepository chatRoomRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final TokenProvider tokenProvider;
  private final JwtTokenExtract jwtTokenExtract;
  private final ParticipantGameRepository participantGameRepository;

  public void sendMessage(ChatMessage chatMessage, String gameId,
      String token) {
    log.info("서비스 토큰 정보 " + token);
    messagingTemplate.convertAndSend("/topic/" + gameId, chatMessage);
  }

  public void addUser(ChatMessage chatMessage, String gameId,
      String token) {
    log.info("서비스 토큰 정보 " + token);
    messagingTemplate.convertAndSend("/topic/" + gameId, chatMessage);

  }

}
package com.zerobase.hoops.chat.service;

import com.zerobase.hoops.chat.chat.ChatMessage;
import com.zerobase.hoops.chat.dto.MessageConvertDto;
import com.zerobase.hoops.chat.dto.MessageDto;
import com.zerobase.hoops.chat.repository.ChatRoomRepository;
import com.zerobase.hoops.chat.repository.MessageRepository;
import com.zerobase.hoops.entity.ChatRoomEntity;
import com.zerobase.hoops.entity.MessageEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.security.JwtTokenExtract;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
  private final JwtTokenExtract jwtTokenExtract;
  private final MessageRepository messageRepository;

  public void sendMessage(ChatMessage chatMessage, String gameId,
      String token) {
    Long gameIdNumber = Long.parseLong(gameId);
    log.info("메세지 보내기 토큰확인" + token);
    UserEntity user = jwtTokenExtract.getUserFromToken(token);
    log.info("메세지 보내기 토큰확인 + 유저 닉네임" + user.getNickName());
    ChatRoomEntity chatRoom = chatRoomRepository.findByGameEntity_Id(
            gameIdNumber)
        .orElseThrow(
            () -> new CustomException(ErrorCode.NOT_EXIST_CHATROOM));

    Long sessionId = chatRoom.getSessionId();

    if (sessionId == null) {
      throw new CustomException(ErrorCode.NOT_EXIST_CHATROOM_SESSION);
    }

    MessageDto message = MessageDto.builder()
        .content(chatMessage.getContent())
        .sessionId(sessionId)
        .build();

    messageRepository.save(message.toEntity(user, chatRoom));
    chatMessage.changeNewSessionId(sessionId);
    messagingTemplate.convertAndSend("/topic/" + gameId, chatMessage);
  }

  public void addUser(ChatMessage chatMessage, String gameId,
      String token) {
    Long gameIdNumber = Long.parseLong(gameId);

    ChatRoomEntity chatRoom = chatRoomRepository.findByGameEntity_Id(
            gameIdNumber)
        .orElseThrow(
            () -> new CustomException(ErrorCode.NOT_EXIST_CHATROOM));

    Long activeSessionId = chatRoom.getSessionId();

    if (activeSessionId == null) {
      activeSessionId = generateSessionId();
      chatRoom.changeNewSessionId(activeSessionId);
    }

    chatMessage.changeNewSessionId(activeSessionId);
    messagingTemplate.convertAndSend("/topic/" + gameId, chatMessage);
  }

  public void loadMessagesAndSend(String gameId, String token) {
    Long gameIdNumber = Long.parseLong(gameId);
    UserEntity user = jwtTokenExtract.getUserFromToken(token);
    log.info("메세지 로딩 확인 => 닉네임 나와야함" + user.getNickName());
    ChatRoomEntity chatRoom = chatRoomRepository.findByGameEntity_Id(
            gameIdNumber)
        .orElseThrow(
            () -> new CustomException(ErrorCode.NOT_EXIST_CHATROOM));
    Long sessionId = chatRoom.getSessionId();

    if (sessionId == null) {
      throw new CustomException(ErrorCode.NOT_EXIST_CHATROOM_SESSION);
    }

    List<MessageEntity> messages = messageRepository.findByChatRoomEntity_RoomIdAndSessionId(
            chatRoom.getRoomId(), sessionId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.NOT_EXIST_MESSAGE_FOR_CHATROOM));

    List<MessageConvertDto> messageDto = messages.stream()
        .map(this::convertToChatMessage)
        .collect(Collectors.toList());

    boolean isNewUser = messages.stream()
        .noneMatch(m -> m.getUser().getId().equals(user.getId()));
    if (isNewUser) {
      messagingTemplate.convertAndSend("/topic/" + gameId + "/newUser",
          messageDto);
      return;
    }
    messagingTemplate.convertAndSend("/topic/" + gameId, messageDto);
  }

  private MessageConvertDto convertToChatMessage(
      MessageEntity messageEntity) {
    return MessageConvertDto.builder()
        .id(messageEntity.getMessageId())
        .sender(messageEntity.getUser().getNickName())
        .content(messageEntity.getContent())
        .sessionId(messageEntity.getSessionId())
        .build();
  }

  private Long generateSessionId() {
    return UUID.randomUUID().getMostSignificantBits();
  }
}

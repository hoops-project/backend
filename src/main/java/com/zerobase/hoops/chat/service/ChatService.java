package com.zerobase.hoops.chat.service;

import com.zerobase.hoops.chat.chat.ChatMessage;
import com.zerobase.hoops.chat.dto.MessageConvertDto;
import com.zerobase.hoops.chat.dto.MessageDto;
import com.zerobase.hoops.chat.repository.ChatRoomRepository;
import com.zerobase.hoops.chat.repository.MessageRepository;
import com.zerobase.hoops.entity.ChatRoomEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.MessageEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import java.util.List;
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
  private final GameRepository gameRepository;

  public void sendMessage(ChatMessage chatMessage, String gameId,
      String token) {
    log.info("sendMessage 시작");
    Long gameIdNumber = Long.parseLong(gameId);
    log.info("메세지 보내기 토큰 Check={}", token);
    UserEntity user = jwtTokenExtract.getUserFromToken(token);
    log.info("메세지 보내기 유저 닉네임={}", user.getNickName());
    List<ChatRoomEntity> chatRoomEntityList = chatRoomRepository.findByGameEntity_Id(
        gameIdNumber);

    for (ChatRoomEntity chatRoomEntity : chatRoomEntityList) {
      String nickName = chatRoomEntity.getUserEntity().getNickName();

      MessageDto message = MessageDto.builder()
          .content(chatMessage.getContent())
          .build();

      messageRepository.save(message.toEntity(user, chatRoomEntity));
      messagingTemplate.convertAndSend("/topic/" + gameId + "/" + nickName,
          chatMessage);
    }
    log.info("sendMessage 종료");
  }

  public void addUser(ChatMessage chatMessage, String gameId,
      String token) {
    log.info("addUser 시작");
    Long gameIdNumber = Long.parseLong(gameId);
    UserEntity user = jwtTokenExtract.getUserFromToken(token);
    GameEntity game = gameRepository.findById(gameIdNumber)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    boolean userChatRoom = chatRoomRepository.existsByGameEntity_IdAndUserEntity_Id(
        game.getId(), user.getId());

    if (!userChatRoom) {
      log.info("{} 새로운 채팅방 생성", user.getNickName());
      ChatRoomEntity chatRoom = new ChatRoomEntity();
      chatRoom.saveGameInfo(game);
      chatRoom.saveUserInfo(user);
      chatRoomRepository.save(chatRoom);
    }

    List<ChatRoomEntity> chatRoomEntityList = chatRoomRepository.findByGameEntity_Id(
        gameIdNumber);

    for (ChatRoomEntity chatRoomEntity : chatRoomEntityList) {
      log.info("{} 채팅방 입장 메세지 전파", user.getNickName());
      String nickName = chatRoomEntity.getUserEntity().getNickName();
      messagingTemplate.convertAndSend("/topic/" + gameId + "/" + nickName,
          chatMessage);
    }

    log.info("addUser 종료");
  }

  public void loadMessagesAndSend(String gameId, String token) {
    log.info("loadMessagesAndSend 시작");
    Long gameIdNumber = Long.parseLong(gameId);
    UserEntity user = jwtTokenExtract.getUserFromToken(token);

    log.info("메세지 로딩 Check => userNickName={}", user.getNickName());

    ChatRoomEntity chatRoom = chatRoomRepository.findByGameEntity_IdAndUserEntity_Id(
            gameIdNumber, user.getId())
        .orElseThrow(
            () -> new CustomException(ErrorCode.NOT_EXIST_CHATROOM));

    List<MessageEntity> messages = messageRepository.findByChatRoomEntity(
        chatRoom);

    List<MessageConvertDto> messageDto = messages.stream()
        .map(this::convertToChatMessage)
        .collect(Collectors.toList());

    messagingTemplate.convertAndSend(
        "/topic/" + gameId + "/" + user.getNickName(), messageDto);
    log.info("loadMessagesAndSend 종료");
  }

  private MessageConvertDto convertToChatMessage(
      MessageEntity messageEntity) {
    return MessageConvertDto.builder()
        .id(messageEntity.getId())
        .sender(messageEntity.getUser().getNickName())
        .content(messageEntity.getContent())
        .build();
  }
}

package com.zerobase.hoops.chat.service;

import com.zerobase.hoops.chat.domain.dto.ChatRoomDTO;
import com.zerobase.hoops.chat.domain.dto.Content;
import com.zerobase.hoops.chat.domain.dto.CreateRoomDTO;
import com.zerobase.hoops.chat.domain.dto.MessageDTO;
import com.zerobase.hoops.chat.domain.repository.ChatRoomRepository;
import com.zerobase.hoops.chat.domain.repository.MessageRepository;
import com.zerobase.hoops.entity.ChatRoomEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.MessageEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.users.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatRoomRepository chatRoomRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final GameRepository gameRepository;
  private final ParticipantGameRepository participantGameRepository;

  /**
   * 채팅방 생성 및 DB에 저장하는 메서드
   *
   * @param createRoomDTO
   * @return
   */
  public ChatRoomDTO createChatRoom(CreateRoomDTO createRoomDTO) {
    GameEntity gameEntity = gameRepository.findById(createRoomDTO.getGameId())
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    ChatRoomEntity chattingRoom = ChatRoomEntity.builder()
        .gameEntity(gameEntity)
        .build();

    return ChatRoomDTO.entityToDto(
        chatRoomRepository.save(chattingRoom));
  }

  public MessageDTO createMessage(Long gameId, Content content,
      String senderId) {
    ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(gameId)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    UserEntity user = this.findUser(senderId);

    MessageEntity messageEntity = MessageEntity.builder()
        .content(String.valueOf(content))
        .sendDateTime(LocalDateTime.now())
        .user(user)
        .chatRoomEntity(chatRoomEntity)
        .build();

    return MessageDTO.entityToDto(messageRepository.save(messageEntity));

  }

  public UserEntity findUser(String senderId) {
    return userRepository.findByIdAndDeletedDateTimeNull(senderId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }

  public void checkAcceptUser(Long gameId, Long userId) {
    if (!participantGameRepository
        .existsByStatusAndGameEntityGameIdAndUserEntityUserId(
            ParticipantGameStatus.ACCEPT, gameId, userId
        )) {
      throw new CustomException(ErrorCode.CANNOT_ENTER_CHAT);
    }
  }
}

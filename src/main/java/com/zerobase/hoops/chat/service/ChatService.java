package com.zerobase.hoops.chat.service;


import com.zerobase.hoops.chat.domain.dto.ChatRoomDTO;
import com.zerobase.hoops.chat.domain.dto.Content;
import com.zerobase.hoops.chat.domain.dto.MessageDTO;
import com.zerobase.hoops.chat.domain.repository.ChatRoomRepository;
import com.zerobase.hoops.chat.domain.repository.MessageRepository;
import com.zerobase.hoops.entity.ChatRoomEntity;
import com.zerobase.hoops.entity.MessageEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
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

  /**
   * 채팅방 생성 및 DB에 저장하는 메서드
   *
   * @param name
   * @return
   */
  public ChatRoomDTO createChatRoom(String name) {
    ChatRoomEntity chattingRoom = ChatRoomEntity.builder()
            .roomName(name).build();
    return ChatRoomDTO.entityToDto(
        chatRoomRepository.save(chattingRoom));
  }

  public MessageDTO createMessage(Long roomId, Content content, String senderId) {
    ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(roomId)
        .orElseThrow(RuntimeException::new);

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
    return userRepository.findById(senderId)
        .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
  }

}

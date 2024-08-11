package com.zerobase.hoops.chat.service;

import com.zerobase.hoops.chat.chat.ChatMessage;
import com.zerobase.hoops.chat.dto.MessageConvertDto;
import com.zerobase.hoops.chat.dto.MessageDto;
import com.zerobase.hoops.chat.repository.ChatRoomRepository;
import com.zerobase.hoops.chat.repository.MessageRepository;
import com.zerobase.hoops.document.ChatRoomDocument;
import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.document.MessageDocument;
import com.zerobase.hoops.document.UserDocument;
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
    log.info("메세지 보내기 토큰 Check={}", token);
    UserDocument user = jwtTokenExtract.getUserFromToken(token);
    log.info("메세지 보내기 유저 닉네임={}", user.getNickName());
    List<ChatRoomDocument> chatRoomDocuments =
        chatRoomRepository.findByGameDocument_Id(gameId);

    for (ChatRoomDocument chatRoomDocument : chatRoomDocuments) {
      String nickName = chatRoomDocument.getUserDocument().getNickName();

      long messageId = messageRepository.count() + 1;

      MessageDto message = MessageDto.builder()
          .content(chatMessage.getContent())
          .build();

      messageRepository.save(message.toDocument(user, chatRoomDocument, messageId));
      messagingTemplate.convertAndSend("/topic/" + gameId + "/" + nickName,
          chatMessage);
    }
    log.info("sendMessage 종료");
  }

  public void addUser(ChatMessage chatMessage, String gameId,
      String token) {
    log.info("addUser 시작");
    UserDocument user = jwtTokenExtract.getUserFromToken(token);
    GameDocument game = gameRepository.findById(gameId)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    boolean userChatRoom = chatRoomRepository.existsByGameDocument_IdAndUserDocument_Id(
        game.getId(), user.getId());

    if (!userChatRoom) {
      log.info("{} 새로운 채팅방 생성", user.getNickName());
      long chatRoomId = chatRoomRepository.count() + 1;

      ChatRoomDocument chatRoom = new ChatRoomDocument();
      chatRoom.saveId(chatRoomId);
      chatRoom.saveGameInfo(game);
      chatRoom.saveUserInfo(user);
      chatRoomRepository.save(chatRoom);
    }

    List<ChatRoomDocument> chatRoomDocumentList =
        chatRoomRepository.findByGameDocument_Id(gameId);

    for (ChatRoomDocument chatRoomDocument : chatRoomDocumentList) {
      log.info("{} 채팅방 입장 메세지 전파", user.getNickName());
      String nickName = chatRoomDocument.getUserDocument().getNickName();
      messagingTemplate.convertAndSend("/topic/" + gameId + "/" + nickName,
          chatMessage);
    }

    log.info("addUser 종료");
  }

  public void loadMessagesAndSend(String gameId, String token) {
    log.info("loadMessagesAndSend 시작");
    UserDocument user = jwtTokenExtract.getUserFromToken(token);

    log.info("메세지 로딩 Check => userNickName={}", user.getNickName());

    ChatRoomDocument chatRoom = chatRoomRepository.findByGameDocument_IdAndUserDocument_Id(
            gameId, user.getId())
        .orElseThrow(
            () -> new CustomException(ErrorCode.NOT_EXIST_CHATROOM));

    List<MessageDocument> messages = messageRepository.findByChatRoom(
        chatRoom);

    List<MessageConvertDto> messageDto = messages.stream()
        .map(this::convertToChatMessage)
        .collect(Collectors.toList());

    messagingTemplate.convertAndSend(
        "/topic/" + gameId + "/" + user.getNickName(), messageDto);
    log.info("loadMessagesAndSend 종료");
  }

  private MessageConvertDto convertToChatMessage(
      MessageDocument messageDocument) {
    return MessageConvertDto.builder()
        .id(messageDocument.getId())
        .sender(messageDocument.getUser().getNickName())
        .content(messageDocument.getContent())
        .build();
  }
}

package com.zerobase.hoops.chat.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.chat.chat.ChatMessage;
import com.zerobase.hoops.chat.repository.ChatRoomRepository;
import com.zerobase.hoops.chat.repository.MessageRepository;
import com.zerobase.hoops.entity.ChatRoomEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.MessageEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private MessageSender messageSender;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private GameRepository gameRepository;

  @InjectMocks
  private ChatService chatService;

  private UserEntity testUser;
  private GameEntity testGame;
  private ChatRoomEntity testChatRoom;
  private ChatMessage testChatMessage;
  private String testToken;
  private String testGameId;

  @BeforeEach
  void setUp() {
    testUser = UserEntity.builder()
        .id(1L)
        .nickName("TestUser1")
        .build();

    testGame = GameEntity.builder()
        .id(1L)
        .build();

    testChatRoom = ChatRoomEntity.builder()
        .id(1L)
        .userEntity(testUser)
        .gameEntity(testGame)
        .build();

    testChatMessage = ChatMessage.builder()
        .content("Test Message")
        .build();

    testToken = "testToken";
    testGameId = "1";

    when(jwtTokenExtract.getUserFromToken(testToken)).thenReturn(testUser);
  }
  @DisplayName("메세지 보내기 성공")
  @Test
  void testSendMessage() {
    when(chatRoomRepository.findByGameEntity_Id(1L)).thenReturn(
        Collections.singletonList(testChatRoom));

    chatService.sendMessage(testChatMessage, testGameId, testToken);

    verify(messageRepository, times(1)).save(any(MessageEntity.class));
    verify(messageSender, times(1)).send(eq("/topic/1/TestUser1"),
        eq(testChatMessage));
  }

  @DisplayName("채팅방 입장 성공")
  @Test
  void testAddUser_ExistingChatRoom() {
    when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
    when(chatRoomRepository.existsByGameEntity_IdAndUserEntity_Id(1L,
        1L)).thenReturn(true);
    when(chatRoomRepository.findByGameEntity_Id(1L)).thenReturn(
        Collections.singletonList(testChatRoom));

    chatService.addUser(testChatMessage, testGameId, testToken);

    verify(chatRoomRepository, never()).save(any(ChatRoomEntity.class));
    verify(messageSender, times(1)).send(eq("/topic/1/TestUser1"),
        eq(testChatMessage));
  }

  @DisplayName("채팅방 만들기 성공")
  @Test
  void testAddUser_NewChatRoom() {
    when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
    when(chatRoomRepository.existsByGameEntity_IdAndUserEntity_Id(1L,
        1L)).thenReturn(false);
    when(chatRoomRepository.findByGameEntity_Id(1L)).thenReturn(
        Collections.singletonList(testChatRoom));

    chatService.addUser(testChatMessage, testGameId, testToken);

    verify(chatRoomRepository, times(1)).save(any(ChatRoomEntity.class));
    verify(messageSender, times(1)).send(eq("/topic/1/TestUser1"),
        eq(testChatMessage));
  }

  @DisplayName("채팅방이 존재하지 않을 때")
  @Test
  void testAddUser_GameNotFound() {
    when(gameRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(CustomException.class, () -> {
      chatService.addUser(testChatMessage, testGameId, testToken);
    });
  }

  @DisplayName("채팅방 불러오기 및 메세지 보내기 성공")
  @Test
  void testLoadMessagesAndSend() {
    MessageEntity testMessage = MessageEntity.builder()
        .id(1L)
        .user(testUser)
        .content("Test Content")
        .build();

    when(chatRoomRepository.findByGameEntity_IdAndUserEntity_Id(1L, 1L))
        .thenReturn(Optional.of(testChatRoom));
    when(messageRepository.findByChatRoomEntity(testChatRoom))
        .thenReturn(Arrays.asList(testMessage));

    chatService.loadMessagesAndSend(testGameId, testToken);

    verify(messageSender, times(1)).send(eq("/topic/1/TestUser1"), any(List.class));
  }

  @DisplayName("채팅방이 존재하지 않을 때 (승인되지 않았을 때) 실패")
  @Test
  void testLoadMessagesAndSend_ChatRoomNotFound() {
    when(chatRoomRepository.findByGameEntity_IdAndUserEntity_Id(1L, 1L))
        .thenReturn(Optional.empty());

    assertThrows(CustomException.class, () -> {
      chatService.loadMessagesAndSend(testGameId, testToken);
    });
  }
}
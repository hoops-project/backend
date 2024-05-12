package com.zerobase.hoops.invite.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteRequest;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.invite.dto.InviteDto.CreateRequest;
import com.zerobase.hoops.invite.dto.InviteDto.CreateResponse;
import com.zerobase.hoops.invite.repository.InviteRepository;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

  @InjectMocks
  private InviteService inviteService;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ParticipantGameRepository participantGameRepository;

  @Mock
  private GameRepository gameRepository;

  @Mock
  private InviteRepository inviteRepository;

  private UserEntity requestUser;

  private UserEntity receiverUser;

  private GameEntity createdGameEntity;

  private ParticipantGameEntity creatorParticipantGameEntity;

  private FriendEntity requestUserFriendEntity;

  private FriendEntity receiverUserFriendEntity;


  @BeforeEach
  void setUp() {
    requestUser = UserEntity.builder()
        .userId(1L)
        .id("test")
        .password("Testpass12!@")
        .email("test@example.com")
        .name("test")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("test")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(new ArrayList<>(List.of("ROLE_USER")))
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    receiverUser = UserEntity.builder()
        .userId(6L)
        .id("test6")
        .password("Testpass12!@")
        .email("test6@example.com")
        .name("test6")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("test6")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(new ArrayList<>(List.of("ROLE_USER")))
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    createdGameEntity = GameEntity.builder()
        .gameId(1L)
        .title("테스트제목")
        .content("테스트내용")
        .headCount(6L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.THREEONTHREE)
        .cityName(CityName.SEOUL)
        .userEntity(requestUser)
        .build();
    creatorParticipantGameEntity = ParticipantGameEntity.builder()
        .participantId(1L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(requestUser)
        .build();
    requestUserFriendEntity = FriendEntity.builder()
        .friendId(1L)
        .status(FriendStatus.ACCEPT)
        .userEntity(requestUser)
        .friendUserEntity(receiverUser)
        .build();
    receiverUserFriendEntity = FriendEntity.builder()
        .friendId(1L)
        .status(FriendStatus.ACCEPT)
        .userEntity(receiverUser)
        .friendUserEntity(requestUser)
        .build();
  }

  @Test
  @DisplayName("경기 초대 요청 성공")
  public void requestInviteGame_success() {
    //Given
    CreateRequest createRequest = CreateRequest.builder()
        .gameId(1L)
        .receiverUserId(2L)
        .build();

    CreateResponse createResponse = CreateResponse.builder()
        .inviteId(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .senderUserNickName(requestUser.getNickName())
        .receiverUserNickName(receiverUser.getNickName())
        .title(createdGameEntity.getTitle())
        .build();

    InviteEntity inviteEntity = CreateRequest
        .toEntity(requestUser, receiverUser, createdGameEntity);

    getCurrentUser();

    // 경기
    when(gameRepository
        .findByGameIdAndDeletedDateTimeNull(eq(1L)))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    when(userRepository.findById(eq(2L)))
        .thenReturn(Optional.ofNullable(receiverUser));

    // 해당 경기에 이미 초대 요청 되어 있지 않다고 가정
    when(inviteRepository
        .existsByInviteStatusAndGameEntityGameIdAndReceiverUserEntityUserId
            (eq(InviteStatus.REQUEST), eq(1L),
                eq(2L)))
        .thenReturn(false);

    // 해당 경기에 참가해 잇는 사람이 초대할 경우를 가정
    // 초대 받는 사람이 해당 경기에 참가 안했을 경우를 가정
    when(participantGameRepository
        .existsByStatusAndGameEntityGameIdAndUserEntityUserId
            (eq(ParticipantGameStatus.ACCEPT), anyLong(), anyLong()))
        .thenReturn(true)
        .thenReturn(false);

    // 경기 인원이 경기개설자(1명 만) 있다고 가정
    when(participantGameRepository
        .countByStatusAndGameEntityGameId
            (eq(ParticipantGameStatus.ACCEPT), eq(1L)))
        .thenReturn(1L);

    when(inviteRepository.save(any(InviteEntity.class))).thenAnswer(invocation -> {
      InviteEntity savedInviteEntity = invocation.getArgument(0);
      savedInviteEntity.setInviteId(1L); // friendId 동적 할당
      savedInviteEntity.setRequestedDatetime(LocalDateTime.now());
      return savedInviteEntity;
    });

    // when
    CreateResponse result = inviteService.requestInviteGame(createRequest);

    // Then
    assertEquals(createResponse.getInviteId(), result.getInviteId());
    assertEquals(createResponse.getInviteStatus(), result.getInviteStatus());
    assertEquals(createResponse.getSenderUserNickName(), result.getSenderUserNickName());
    assertEquals(createResponse.getReceiverUserNickName(), result.getReceiverUserNickName());
    assertEquals(createResponse.getTitle(), result.getTitle());
  }

  private void getCurrentUser() {
    when(jwtTokenExtract.currentUser()).thenReturn(requestUser);

    when(userRepository.findById(anyLong())).thenReturn(
        Optional.ofNullable(requestUser));
  }

}
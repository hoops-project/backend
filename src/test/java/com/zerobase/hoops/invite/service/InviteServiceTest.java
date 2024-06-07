package com.zerobase.hoops.invite.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.invite.dto.InviteDto.CancelRequest;
import com.zerobase.hoops.invite.dto.InviteDto.CancelResponse;
import com.zerobase.hoops.invite.dto.InviteDto.CreateRequest;
import com.zerobase.hoops.invite.dto.InviteDto.CreateResponse;
import com.zerobase.hoops.invite.dto.InviteDto.InviteMyListResponse;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveAcceptRequest;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveAcceptResponse;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveRejectRequest;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveRejectResponse;
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
  private FriendRepository friendRepository;

  @Mock
  private InviteRepository inviteRepository;

  private UserEntity requestUser;

  private UserEntity receiverUser;

  private UserEntity otherUser;

  private GameEntity createdGameEntity;
  private GameEntity otherCreatedGameEntity;

  private ParticipantGameEntity creatorParticipantGameEntity;

  private FriendEntity requestUserFriendEntity;

  private FriendEntity receiverUserFriendEntity;


  @BeforeEach
  void setUp() {
    requestUser = UserEntity.builder()
        .id(1L)
        .loginId("test")
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
        .id(2L)
        .loginId("test2")
        .password("Testpass12!@")
        .email("test2@example.com")
        .name("test2")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("test2")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(new ArrayList<>(List.of("ROLE_USER")))
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    otherUser = UserEntity.builder()
        .id(6L)
        .loginId("test6")
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
        .id(1L)
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
        .user(requestUser)
        .build();
    otherCreatedGameEntity = GameEntity.builder()
        .id(2L)
        .title("테스트제목2")
        .content("테스트내용2")
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
        .user(receiverUser)
        .build();
    creatorParticipantGameEntity = ParticipantGameEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .game(createdGameEntity)
        .user(requestUser)
        .build();
    requestUserFriendEntity = FriendEntity.builder()
        .id(1L)
        .status(FriendStatus.ACCEPT)
        .user(requestUser)
        .friendUser(receiverUser)
        .build();
    receiverUserFriendEntity = FriendEntity.builder()
        .id(1L)
        .status(FriendStatus.ACCEPT)
        .user(receiverUser)
        .friendUser(requestUser)
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

    CreateResponse response = CreateResponse.builder()
        .inviteId(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .senderUserNickName(requestUser.getNickName())
        .receiverUserNickName(receiverUser.getNickName())
        .title(createdGameEntity.getTitle())
        .build();

    InviteEntity inviteEntity = CreateRequest
        .toEntity(requestUser, receiverUser, createdGameEntity);

    getCurrentUser();

    validFriendUser(requestUser.getId(), createRequest.getReceiverUserId());

    // 경기
    when(gameRepository
        .findByIdAndDeletedDateTimeNull(eq(1L)))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    when(userRepository.findById(eq(2L)))
        .thenReturn(Optional.ofNullable(receiverUser));

    // 해당 경기에 이미 초대 요청 되어 있지 않다고 가정
    when(inviteRepository
        .existsByInviteStatusAndGameIdAndReceiverUserId
            (eq(InviteStatus.REQUEST), eq(1L),
                eq(2L)))
        .thenReturn(false);

    // 해당 경기에 참가해 잇는 사람이 초대할 경우를 가정
    when(participantGameRepository
        .existsByStatusAndGameIdAndUserId
            (eq(ParticipantGameStatus.ACCEPT), eq(1L), eq(1L)))
        .thenReturn(true);

    // 초대 받는 사람이 해당 경기에 참가 및 요청 안했을 경우를 가정
    when(participantGameRepository
        .existsByStatusInAndGameIdAndUserId
            (eq(List.of(ParticipantGameStatus.ACCEPT, ParticipantGameStatus.APPLY))
                ,eq(1L), eq(2L)))
        .thenReturn(false);

    // 경기 인원이 경기개설자(1명 만) 있다고 가정
    when(participantGameRepository
        .countByStatusAndGameId
            (eq(ParticipantGameStatus.ACCEPT), eq(1L)))
        .thenReturn(1);

    when(inviteRepository.save(any(InviteEntity.class))).thenAnswer(invocation -> {
      InviteEntity savedInviteEntity = invocation.getArgument(0);
      savedInviteEntity.setId(1L); // inviteId 동적 할당
      savedInviteEntity.setRequestedDateTime(LocalDateTime.now());
      return savedInviteEntity;
    });

    // when
    CreateResponse result = inviteService.requestInviteGame(createRequest);

    // Then
    assertEquals(response.getInviteId(), result.getInviteId());
    assertEquals(response.getInviteStatus(), result.getInviteStatus());
    assertEquals(response.getSenderUserNickName(), result.getSenderUserNickName());
    assertEquals(response.getReceiverUserNickName(), result.getReceiverUserNickName());
    assertEquals(response.getTitle(), result.getTitle());
  }

  @Test
  @DisplayName("경기 초대 요청 취소 성공")
  public void cancelInviteGame_success() {
    //Given
    CancelRequest cancelRequest = CancelRequest.builder()
        .inviteId(1L)
        .gameId(1L)
        .build();

    CancelResponse response = CancelResponse.builder()
        .inviteId(1L)
        .inviteStatus(InviteStatus.CANCEL)
        .senderUserNickName(requestUser.getNickName())
        .receiverUserNickName(receiverUser.getNickName())
        .title(createdGameEntity.getTitle())
        .build();

    InviteEntity inviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.CANCEL)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGameEntity)
        .build();

    getCurrentUser();

    // 경기
    when(inviteRepository
        .findByIdAndInviteStatus(eq(1L), eq(InviteStatus.REQUEST)))
        .thenReturn(Optional.ofNullable(inviteEntity));

    when(inviteRepository.save(any(InviteEntity.class))).thenReturn(inviteEntity);

    // when
    CancelResponse result = inviteService.cancelInviteGame(cancelRequest);

    // Then
    assertEquals(response.getInviteId(), result.getInviteId());
    assertEquals(response.getInviteStatus(), result.getInviteStatus());
    assertEquals(response.getSenderUserNickName(), result.getSenderUserNickName());
    assertEquals(response.getReceiverUserNickName(), result.getReceiverUserNickName());
    assertEquals(response.getTitle(), result.getTitle());
  }

  @Test
  @DisplayName("경기 초대 요청(경기 개설자) 수락 성공")
  public void acceptGameCreatorInviteGame_success() {
    //Given
    ReceiveAcceptRequest receiveAcceptRequest = ReceiveAcceptRequest.builder()
        .inviteId(1L)
        .build();

    ReceiveAcceptResponse response = ReceiveAcceptResponse.builder()
        .inviteId(1L)
        .inviteStatus(InviteStatus.ACCEPT)
        .senderUserNickName(requestUser.getNickName())
        .receiverUserNickName(receiverUser.getNickName())
        .title(createdGameEntity.getTitle())
        .build();

    InviteEntity inviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGameEntity)
        .build();

    ParticipantGameEntity gameCreatorInvite =
        ParticipantGameEntity.gameCreatorInvite(inviteEntity);

    when(jwtTokenExtract.currentUser()).thenReturn(receiverUser);

    when(userRepository.findById(anyLong())).thenReturn(
        Optional.ofNullable(receiverUser));

    validFriendUser(receiverUser.getId(), requestUser.getId());

    when(inviteRepository.findByIdAndInviteStatus
        (eq(1L), eq(InviteStatus.REQUEST)))
        .thenReturn(Optional.of(inviteEntity));

    // 해당 경기에 인원이 1명 있다고 가정
    when(participantGameRepository
        .countByStatusAndGameId(eq(ParticipantGameStatus.ACCEPT),
            eq(1L)))
        .thenReturn(0);

    when(inviteRepository.save(any(InviteEntity.class))).thenReturn(inviteEntity);

    when(participantGameRepository.save(any(ParticipantGameEntity.class)))
        .thenReturn(gameCreatorInvite);

    // when
    ReceiveAcceptResponse result = inviteService.receiveAcceptInviteGame(receiveAcceptRequest);

    // Then
    assertEquals(response.getInviteId(), result.getInviteId());
    assertEquals(response.getInviteStatus(), result.getInviteStatus());
    assertEquals(response.getSenderUserNickName(), result.getSenderUserNickName());
    assertEquals(response.getReceiverUserNickName(), result.getReceiverUserNickName());
    assertEquals(response.getTitle(), result.getTitle());
  }

  @Test
  @DisplayName("경기 초대 요청 거절 성공")
  public void rejectInviteGame_success() {
    //Given
    ReceiveRejectRequest receiveRejectRequest = ReceiveRejectRequest.builder()
        .inviteId(1L)
        .build();

    ReceiveRejectResponse response = ReceiveRejectResponse.builder()
        .inviteId(1L)
        .inviteStatus(InviteStatus.REJECT)
        .senderUserNickName(requestUser.getNickName())
        .receiverUserNickName(receiverUser.getNickName())
        .title(createdGameEntity.getTitle())
        .build();

    InviteEntity inviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGameEntity)
        .build();

    InviteEntity rejectEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REJECT)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGameEntity)
        .build();

    when(jwtTokenExtract.currentUser()).thenReturn(receiverUser);

    when(userRepository.findById(anyLong())).thenReturn(
        Optional.ofNullable(receiverUser));

    when(inviteRepository.findByIdAndInviteStatus
        (eq(1L), eq(InviteStatus.REQUEST)))
        .thenReturn(Optional.of(inviteEntity));

    when(inviteRepository.save(any(InviteEntity.class))).thenReturn(rejectEntity);

    // when
    ReceiveRejectResponse result = inviteService.receiveRejectInviteGame(receiveRejectRequest);

    // Then
    assertEquals(response.getInviteId(), result.getInviteId());
    assertEquals(response.getInviteStatus(), result.getInviteStatus());
    assertEquals(response.getSenderUserNickName(), result.getSenderUserNickName());
    assertEquals(response.getReceiverUserNickName(), result.getReceiverUserNickName());
    assertEquals(response.getTitle(), result.getTitle());
  }

  @Test
  @DisplayName("경기 초대 요청 리스트 조회 성공")
  public void getInviteRequestList_success() {
    //Given
    InviteEntity inviteEntity1 = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .senderUser(requestUser)
        .receiverUser(otherUser)
        .game(createdGameEntity)
        .build();

    InviteEntity inviteEntity2 = InviteEntity.builder()
        .id(2L)
        .inviteStatus(InviteStatus.REQUEST)
        .senderUser(receiverUser)
        .receiverUser(otherUser)
        .game(otherCreatedGameEntity)
        .build();

    List<InviteEntity> inviteEntityList = new ArrayList<>();
    inviteEntityList.add(inviteEntity1);
    inviteEntityList.add(inviteEntity2);

    List<InviteMyListResponse> responseList = inviteEntityList.stream()
        .map(InviteMyListResponse::toDto)
        .toList();

    when(jwtTokenExtract.currentUser()).thenReturn(otherUser);

    when(userRepository.findById(anyLong())).thenReturn(
        Optional.ofNullable(otherUser));

    when(inviteRepository.findByInviteStatusAndReceiverUserId
        (eq(InviteStatus.REQUEST), anyLong()))
        .thenReturn(inviteEntityList);

    // when
    List<InviteMyListResponse> result = inviteService.getInviteRequestList();

    // Then
    assertEquals(responseList.get(0).getInviteId(), result.get(0).getInviteId());
    assertEquals(responseList.get(0).getInviteStatus(), result.get(0).getInviteStatus());
    assertEquals(responseList.get(0).getGameId(), result.get(0).getGameId());

    assertEquals(responseList.get(1).getInviteId(),
        result.get(1).getInviteId());
    assertEquals(responseList.get(1).getInviteStatus(),
        result.get(1).getInviteStatus());
    assertEquals(responseList.get(1).getGameId(), result.get(1).getGameId());
  }



  private void getCurrentUser() {
    when(jwtTokenExtract.currentUser()).thenReturn(requestUser);

    when(userRepository.findById(anyLong())).thenReturn(
        Optional.ofNullable(requestUser));
  }

  private void validFriendUser(Long senderUserId, Long receiverUserId) {
    when(friendRepository.existsByUserIdAndFriendUserIdAndStatus
        (senderUserId, receiverUserId, FriendStatus.ACCEPT)).thenReturn(true);
  }

}
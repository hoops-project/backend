package com.zerobase.hoops.invite.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
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
import com.zerobase.hoops.invite.dto.InviteDto.CreateRequest;
import com.zerobase.hoops.invite.dto.InviteDto.InviteMyListResponse;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveAcceptRequest;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveRejectRequest;
import com.zerobase.hoops.invite.repository.InviteRepository;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import org.mockito.Spy;
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

  @Spy
  private Clock clock;

  private UserEntity requestUser;

  private UserEntity receiverUser;

  private UserEntity otherUser;

  private GameEntity createdGameEntity1;
  private GameEntity createdGameEntity2;

  private ParticipantGameEntity creatorParticipantGameEntity;

  private FriendEntity requestUserFriendEntity;

  private FriendEntity receiverUserFriendEntity;

  private InviteEntity expectedGameCreatorRequestInviteEntity;
  private InviteEntity expectedGameCreatorAcceptInviteEntity;
  private InviteEntity expectedGameParticipantRequestInviteEntity;
  private InviteEntity expectedGameParticipantAcceptInviteEntity;
  private InviteEntity expectedGameCreatorRejectInviteEntity;

  private LocalDateTime fixedRequestedDateTime;
  private LocalDateTime fixedCanceledDateTime;
  private LocalDateTime fixedAcceptedDateTime;
  private LocalDateTime fixedRejectedDateTime;
  private LocalDateTime fixedDeleteDateTime;

  private CreateRequest requestInvite;

  private InviteEntity requestInviteEntity;

  private InviteEntity cancelInviteEntity;

  private InviteEntity acceptInviteEntity;

  private CancelRequest cancelInvite;

  private ReceiveAcceptRequest receiveAcceptRequest;

  private ReceiveRejectRequest receiveRejectRequest;

  @BeforeEach
  void setUp() {
    fixedRequestedDateTime = LocalDateTime
        .of(2024, 6, 9, 0, 0, 0);
    fixedCanceledDateTime = LocalDateTime
        .of(2024, 6, 10, 0, 0, 0);
    fixedAcceptedDateTime = LocalDateTime
        .of(2024, 6, 10, 0, 0, 0);
    fixedRejectedDateTime = LocalDateTime
        .of(2024, 6, 10, 0, 0, 0);
    fixedDeleteDateTime = LocalDateTime
        .of(2024, 6, 10, 0, 0, 0);
    requestInvite = CreateRequest.builder()
        .gameId(1L)
        .receiverUserId(2L)
        .build();
    cancelInvite = CancelRequest.builder()
        .inviteId(1L)
        .gameId(1L)
        .build();
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
    createdGameEntity1 = GameEntity.builder()
        .id(1L)
        .title("테스트제목")
        .content("테스트내용")
        .headCount(6L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.THREEONTHREE)
        .cityName(CityName.SEOUL)
        .user(requestUser)
        .build();
    createdGameEntity2 = GameEntity.builder()
        .id(2L)
        .title("테스트제목2")
        .content("테스트내용2")
        .headCount(6L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(LocalDateTime.now().plusHours(1))
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
        .game(createdGameEntity1)
        .user(requestUser)
        .build();
    requestUserFriendEntity = FriendEntity.builder()
        .id(1L)
        .status(FriendStatus.ACCEPT)
        .user(requestUser)
        .friendUser(receiverUser)
        .build();
    receiverUserFriendEntity = FriendEntity.builder()
        .id(2L)
        .status(FriendStatus.ACCEPT)
        .user(receiverUser)
        .friendUser(requestUser)
        .build();
    expectedGameCreatorRequestInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .requestedDateTime(fixedRequestedDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGameEntity1)
        .build();
    expectedGameCreatorAcceptInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.ACCEPT)
        .requestedDateTime(fixedRequestedDateTime)
        .acceptedDateTime(fixedAcceptedDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGameEntity1)
        .build();
    expectedGameCreatorRejectInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REJECT)
        .requestedDateTime(fixedRequestedDateTime)
        .rejectedDateTime(fixedRejectedDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGameEntity1)
        .build();
        expectedGameParticipantRequestInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .requestedDateTime(fixedRequestedDateTime)
        .senderUser(receiverUser)
        .receiverUser(otherUser)
        .game(createdGameEntity1)
        .build();
    expectedGameParticipantAcceptInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.ACCEPT)
        .requestedDateTime(fixedRequestedDateTime)
        .acceptedDateTime(fixedAcceptedDateTime)
        .senderUser(receiverUser)
        .receiverUser(otherUser)
        .game(createdGameEntity1)
        .build();

    requestInviteEntity = CreateRequest
        .toEntity(requestUser, receiverUser, createdGameEntity1);
    receiveAcceptRequest = ReceiveAcceptRequest.builder()
            .inviteId(1L)
            .build();
    receiveRejectRequest = ReceiveRejectRequest.builder()
            .inviteId(1L)
            .build();
  }

  @Test
  @DisplayName("경기 초대 요청 성공")
  public void requestInviteGame_success() {
    //Given

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);
    
    // 해당 경기 조회
    getGame(requestInvite.getGameId());

    // 해당 경기에 참가해 있는 사람이 초대할 경우를 가정
    checkParticipantGame(createdGameEntity1.getId(), requestUser.getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(createdGameEntity1.getId(), 1);

    // 초대 받으려는 유저 조회
    getReceiverUser(requestInvite.getReceiverUserId());
    
    // 초대 받은 사람이 친구 라고 가정
    checkFriendUser(requestUser.getId(), receiverUser.getId(), true);

    // 해당 경기에 이미 초대 요청 되어 있지 않다고 가정
    checkAlreadyRequestInviteGame
        (createdGameEntity1.getId(), receiverUser.getId(), false);

    // 초대 받는 사람이 해당 경기에 참가 및 요청 안했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGameEntity1.getId(), receiverUser.getId(), false);

    when(inviteRepository.save(requestInviteEntity)).thenAnswer(invocation -> {
      InviteEntity savedInviteEntity = invocation.getArgument(0);
      savedInviteEntity.setId(1L); // inviteId 동적 할당
      savedInviteEntity.setRequestedDateTime(fixedRequestedDateTime);
      return savedInviteEntity;
    });

    // ArgumentCaptor를 사용하여 저장된 엔티티를 캡처합니다.
    ArgumentCaptor<InviteEntity> inviteEntityArgumentCaptor =
        ArgumentCaptor.forClass(InviteEntity.class);

    // when
    inviteService.requestInviteGame(requestInvite);

    // Then
    verify(inviteRepository).save(inviteEntityArgumentCaptor.capture());

    // 저장된 엔티티 캡처
    InviteEntity saveInviteEntity = inviteEntityArgumentCaptor.getValue();

    assertEquals(expectedGameCreatorRequestInviteEntity, saveInviteEntity);
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 해당 경기가 초대 불가능일 때")
  public void requestInviteGame_failIfGameInviteNo() {
    //Given
    createdGameEntity1.setInviteYn(false);

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);

    // 해당 경기 조회
    getGame(requestInvite.getGameId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.requestInviteGame(requestInvite);
    });

    // then
    assertEquals(ErrorCode.NOT_GAME_INVITE, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 경기가 이미 시작이 되었을 때")
  public void requestInviteGame_failIfGameStarted() {
    //Given
    createdGameEntity1.setStartDateTime(LocalDateTime.now().minusMinutes(1));

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);

    // 해당 경기 조회
    getGame(requestInvite.getGameId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.requestInviteGame(requestInvite);
    });

    // then
    assertEquals(ErrorCode.ALREADY_GAME_START, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 해당 경기에 참가해 있지 않은 사람이 초대를 할 경우")
  public void requestInviteGame_failIfNotParticipantUserRequestGameInvite() {
    //Given

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);

    // 해당 경기 조회
    getGame(requestInvite.getGameId());

    // 해당 경기에 참가해 있지 않은 사람이 초대할 경우를 가정
    checkParticipantGame(createdGameEntity1.getId(), requestUser.getId(),
        false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.requestInviteGame(requestInvite);
    });

    // then
    assertEquals(ErrorCode.NOT_PARTICIPANT_GAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 해당 경기 인원이 다 찰 경우")
  public void requestInviteGame_failIfFulledGame() {
    //Given

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);

    // 해당 경기 조회
    getGame(requestInvite.getGameId());

    // 해당 경기에 참가해 있는 사람이 초대할 경우를 가정
    checkParticipantGame(createdGameEntity1.getId(), requestUser.getId(),
        true);

    // 해당 경기 인원이 6명 있다고 가정
    countsAcceptedGame(createdGameEntity1.getId(), 6);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.requestInviteGame(requestInvite);
    });

    // then
    assertEquals(ErrorCode.FULL_PARTICIPANT, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 친구가 아닌 사람이 요청할 때")
  public void requestInviteGame_failIfNotFriendRequestInvite() {
    //Given

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);

    // 해당 경기 조회
    getGame(requestInvite.getGameId());

    // 해당 경기에 참가해 있는 사람이 초대할 경우를 가정
    checkParticipantGame(createdGameEntity1.getId(), requestUser.getId(),
        true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(createdGameEntity1.getId(), 1);

    // 초대 받으려는 유저 조회
    getReceiverUser(requestInvite.getReceiverUserId());

    // 초대 받은 사람이 친구가 아니 라고 가정
    checkFriendUser(requestUser.getId(), receiverUser.getId(), false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.requestInviteGame(requestInvite);
    });

    // then
    assertEquals(ErrorCode.NOT_FOUND_ACCEPT_FRIEND, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 해당 경기에 이미 초대 요청 되어 있을 때")
  public void requestInviteGame_failIfAlreadyRequestGameInvite() {
    //Given

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);

    // 해당 경기 조회
    getGame(requestInvite.getGameId());

    // 해당 경기에 참가해 있는 사람이 초대할 경우를 가정
    checkParticipantGame(createdGameEntity1.getId(), requestUser.getId(),
        true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(createdGameEntity1.getId(), 1);

    // 초대 받으려는 유저 조회
    getReceiverUser(requestInvite.getReceiverUserId());

    // 초대 받은 사람이 친구 라고 가정
    checkFriendUser(requestUser.getId(), receiverUser.getId(), true);

    // 해당 경기에 이미 초대 요청 되어 있다고 가정
    checkAlreadyRequestInviteGame
        (createdGameEntity1.getId(), receiverUser.getId(), true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.requestInviteGame(requestInvite);
    });

    // then
    assertEquals(ErrorCode.ALREADY_INVITE_GAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 해당 경기에 이미 참가 하거나 요청한 경우")
  public void requestInviteGame_failIfAlreadyAcceptOrApplyGame() {
    //Given

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);

    // 해당 경기 조회
    getGame(requestInvite.getGameId());

    // 해당 경기에 참가해 있는 사람이 초대할 경우를 가정
    checkParticipantGame(createdGameEntity1.getId(), requestUser.getId(),
        true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(createdGameEntity1.getId(), 1);

    // 초대 받으려는 유저 조회
    getReceiverUser(requestInvite.getReceiverUserId());

    // 초대 받은 사람이 친구 라고 가정
    checkFriendUser(requestUser.getId(), receiverUser.getId(), true);

    // 해당 경기에 이미 초대 요청 되어 있지 않다고 가정
    checkAlreadyRequestInviteGame
        (createdGameEntity1.getId(), receiverUser.getId(), false);

    // 초대 받는 사람이 해당 경기에 참가 및 요청 했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGameEntity1.getId(), receiverUser.getId(), true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.requestInviteGame(requestInvite);
    });

    // then
    assertEquals(ErrorCode.ALREADY_PARTICIPANT_GAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 취소 성공")
  public void cancelInvitation_success() {
    //Given
    InviteEntity expectedcancelInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.CANCEL)
        .requestedDateTime(fixedRequestedDateTime)
        .canceledDateTime(fixedCanceledDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGameEntity1)
        .build();

    Instant fixedInstant = fixedCanceledDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(cancelInvite.getInviteId());

    cancelInviteEntity = InviteEntity.toCancelEntity(
        expectedGameCreatorRequestInviteEntity, clock);

    when(inviteRepository.save(cancelInviteEntity)).thenReturn(cancelInviteEntity);

    // when
    inviteService.cancelInviteGame(cancelInvite);

    // Then
    assertEquals(expectedcancelInviteEntity, cancelInviteEntity);
  }

  @Test
  @DisplayName("경기 초대 요청 취소 실패 : 본인이 경기 초대 요청한 것이 아닐때")
  public void cancelInvitation_failIfNotMyRequestInvitationToGame() {
    //Given
    expectedGameCreatorRequestInviteEntity.assignSenderUser(receiverUser);

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(cancelInvite.getInviteId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.cancelInviteGame(cancelInvite);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_REQUEST, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 취소 실패 : 다른 경기 초대 요청 일때")
  public void cancelInvitation_failIfRequestInvitationToAnotherGame() {
    //Given
    cancelInvite.setGameId(2L);

    // 로그인 한 유저 조회
    getCurrentUser(requestUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(cancelInvite.getInviteId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.cancelInviteGame(cancelInvite);
    });

    // then
    assertEquals(ErrorCode.NOT_INVITE_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 수락 성공 : 경기 개설자가 요청할 때")
  public void acceptInvitation_successIfRequestByGameCreator() {
    //Given
    Instant fixedInstant = fixedAcceptedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    LocalDateTime nowDateTime = LocalDateTime.now(clock);

    ParticipantGameEntity expectedParticipantGameEntity = ParticipantGameEntity
        .builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(nowDateTime)
        .acceptedDateTime(nowDateTime)
        .game(createdGameEntity1)
        .user(receiverUser)
        .build();

    acceptInviteEntity =
        InviteEntity.toAcceptEntity(expectedGameCreatorRequestInviteEntity, nowDateTime);

    ParticipantGameEntity gameCreatorInvite =
        ParticipantGameEntity.gameCreatorInvite(
            expectedGameCreatorRequestInviteEntity, nowDateTime);

    // 로그인 한 유저 조회
    getCurrentUser(receiverUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(receiveAcceptRequest.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있다고 가정
    checkParticipantGame(createdGameEntity1.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteEntity.getGame().getId(), 1);

    // 수락 하는 사람이 해당 경기에 참가 및 요청 안했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGameEntity1.getId(), receiverUser.getId(), false);

    when(inviteRepository.save(acceptInviteEntity)).thenReturn(acceptInviteEntity);

    // 경기 개설자가 초대 한 경우 수락 -> 경기 참가
    when(participantGameRepository.save(gameCreatorInvite)).thenAnswer(invocation -> {
      ParticipantGameEntity savedParticipantGameEntity =
          invocation.getArgument(0);
      savedParticipantGameEntity.setId(2L); // id 동적 할당
      return savedParticipantGameEntity;
    });

    // ArgumentCaptor를 사용하여 저장된 엔티티를 캡처합니다.
    ArgumentCaptor<ParticipantGameEntity> participantGameEntityArgumentCaptor =
        ArgumentCaptor.forClass(ParticipantGameEntity.class);

    // when
    inviteService.receiveAcceptInviteGame(receiveAcceptRequest);

    // Then
    verify(participantGameRepository)
        .save(participantGameEntityArgumentCaptor.capture());

    // 저장된 엔티티 캡처
    ParticipantGameEntity participantGameEntity =
        participantGameEntityArgumentCaptor.getValue();

    assertEquals(expectedGameCreatorAcceptInviteEntity, acceptInviteEntity);
    assertEquals(expectedParticipantGameEntity, participantGameEntity);
  }

  @Test
  @DisplayName("경기 초대 요청 수락 성공 : 팀원이 초대한 경우")
  public void acceptInvitation_successIfRequestByGameParticipant() {
    //Given
    Instant fixedInstant = fixedAcceptedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    LocalDateTime nowDateTime = LocalDateTime.now(clock);

    ParticipantGameEntity expectedParticipantGameEntity = ParticipantGameEntity
        .builder()
        .id(3L)
        .status(APPLY)
        .createdDateTime(nowDateTime)
        .acceptedDateTime(nowDateTime)
        .game(createdGameEntity1)
        .user(otherUser)
        .build();

    acceptInviteEntity =
        InviteEntity.toAcceptEntity(expectedGameParticipantRequestInviteEntity, nowDateTime);

    ParticipantGameEntity gameUserInvite =
        ParticipantGameEntity.gameUserInvite(
            expectedGameParticipantRequestInviteEntity);

    // 로그인 한 유저 조회
    getCurrentUser(otherUser);

    // 해당 초대 정보 조회
    getGameParticipantRequestInviteEntity(receiveAcceptRequest.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(otherUser.getId(),
        expectedGameParticipantRequestInviteEntity.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있다고 가정
    checkParticipantGame(createdGameEntity1.getId(),
        expectedGameParticipantRequestInviteEntity.getSenderUser().getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteEntity.getGame().getId(), 1);

    // 수락 하는 사람이 해당 경기에 참가 및 요청 안했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGameEntity1.getId(), otherUser.getId(), false);

    when(inviteRepository.save(acceptInviteEntity)).thenReturn(acceptInviteEntity);
    // 경기 개설자가 초대 한 경우 수락 -> 경기 참가
    when(participantGameRepository.save(gameUserInvite)).thenAnswer(invocation -> {
      ParticipantGameEntity savedParticipantGameEntity =
          invocation.getArgument(0);
      savedParticipantGameEntity.setId(3L); // id 동적 할당
      savedParticipantGameEntity.setCreatedDateTime(nowDateTime);
      return savedParticipantGameEntity;
    });

    // ArgumentCaptor를 사용하여 저장된 엔티티를 캡처합니다.
    ArgumentCaptor<ParticipantGameEntity> participantGameEntityArgumentCaptor =
        ArgumentCaptor.forClass(ParticipantGameEntity.class);

    // when
    inviteService.receiveAcceptInviteGame(receiveAcceptRequest);

    // Then
    verify(participantGameRepository)
        .save(participantGameEntityArgumentCaptor.capture());

    // 저장된 엔티티 캡처
    ParticipantGameEntity participantGameEntity =
        participantGameEntityArgumentCaptor.getValue();

    assertEquals(expectedGameParticipantAcceptInviteEntity, acceptInviteEntity);
    assertEquals(expectedParticipantGameEntity, participantGameEntity);
  }

  @Test
  @DisplayName("경기 초대 요청 수락 실패 : 본인이 받은 초대 요청이 아닐때")
  public void acceptInvitation_failIfNotMyRequestInvite() {
    //Given
    expectedGameCreatorRequestInviteEntity.assignReceiverUser(requestUser);

    // 로그인 한 유저 조회
    getCurrentUser(receiverUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(receiveAcceptRequest.getInviteId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.receiveAcceptInviteGame(receiveAcceptRequest);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_INVITE_REQUEST, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 수락 실패 : 친구가 아닌 사람이 초대 요청 했을때")
  public void acceptInvitation_failIfNotFriendRequestInvite() {
    //Given

    // 로그인 한 유저 조회
    getCurrentUser(receiverUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(receiveAcceptRequest.getInviteId());

    // 초대 한 사람이 친구가 아니 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.receiveAcceptInviteGame(receiveAcceptRequest);
    });

    // then
    assertEquals(ErrorCode.NOT_FOUND_ACCEPT_FRIEND, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 수락 실패 : 초대한 사람이 해당 경기에 참가해 있지 않을 때")
  public void acceptInvitation_failIfSenderUserNotParticipantGame() {
    //Given

    // 로그인 한 유저 조회
    getCurrentUser(receiverUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(receiveAcceptRequest.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있지 않다고 가정
    checkParticipantGame(createdGameEntity1.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.receiveAcceptInviteGame(receiveAcceptRequest);
    });

    // then
    assertEquals(ErrorCode.NOT_PARTICIPANT_GAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 수락 실패 : 해당 경기에 인원이 다 찼을 때")
  public void acceptInvitation_failIfFulledGame() {
    //Given

    // 로그인 한 유저 조회
    getCurrentUser(receiverUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(receiveAcceptRequest.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있지 않다고 가정
    checkParticipantGame(createdGameEntity1.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 해당 경기 인원이 다 찼다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteEntity.getGame().getId(), 6);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.receiveAcceptInviteGame(receiveAcceptRequest);
    });

    // then
    assertEquals(ErrorCode.FULL_PARTICIPANT, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 수락 실패 : 수락 하는 사람이 해당 경기에 참가 및 요청 했을 때")
  public void acceptInvitation_failIfApplyOrAcceptGame() {
    //Given

    // 로그인 한 유저 조회
    getCurrentUser(receiverUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(receiveAcceptRequest.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있지 않다고 가정
    checkParticipantGame(createdGameEntity1.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteEntity.getGame().getId(), 1);

    // 수락 하는 사람이 해당 경기에 참가 및 요청 했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGameEntity1.getId(), receiverUser.getId(), true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.receiveAcceptInviteGame(receiveAcceptRequest);
    });

    // then
    assertEquals(ErrorCode.ALREADY_PARTICIPANT_GAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 거절 성공")
  public void rejectInvitation_success() {
    //Given
    Instant fixedInstant = fixedRejectedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    // 로그인 한 유저 조회
    getCurrentUser(receiverUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(receiveRejectRequest.getInviteId());

    InviteEntity rejectEntity = InviteEntity.toRejectEntity
        (expectedGameCreatorRequestInviteEntity, clock);


    when(inviteRepository.save(rejectEntity))
        .thenReturn(rejectEntity);

    // when
    inviteService.receiveRejectInviteGame(receiveRejectRequest);

    // Then
    assertEquals(expectedGameCreatorRejectInviteEntity ,rejectEntity);
  }

  @Test
  @DisplayName("경기 초대 요청 거절 실패 : 본인이 받은 초대 요청이 아닐때")
  public void rejectInvitation_failIfNotMyRequestInvitation() {
    //Given
    expectedGameCreatorRequestInviteEntity.assignReceiverUser(requestUser);

    // 로그인 한 유저 조회
    getCurrentUser(receiverUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(receiveRejectRequest.getInviteId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.receiveRejectInviteGame(receiveRejectRequest);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_INVITE_REQUEST, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 리스트 조회 성공")
  public void getRequestInvitationList_success() {
    //Given
    InviteEntity inviteEntity1 = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .senderUser(requestUser)
        .receiverUser(otherUser)
        .game(createdGameEntity1)
        .build();

    InviteEntity inviteEntity2 = InviteEntity.builder()
        .id(2L)
        .inviteStatus(InviteStatus.REQUEST)
        .senderUser(receiverUser)
        .receiverUser(otherUser)
        .game(createdGameEntity2)
        .build();

    List<InviteEntity> inviteEntityList = List.of(inviteEntity1, inviteEntity2);

    List<InviteMyListResponse> expectList = inviteEntityList.stream()
        .map(InviteMyListResponse::toDto)
        .toList();

    // 로그인 한 유저 조회
    getCurrentUser(otherUser);

    when(inviteRepository.findByInviteStatusAndReceiverUserId
        (InviteStatus.REQUEST, otherUser.getId()))
        .thenReturn(inviteEntityList);

    // when
    List<InviteMyListResponse> result = inviteService.getInviteRequestList();

    // Then
    assertEquals(expectList, result);
  }

  // 로그인 유저 조회
  private void getCurrentUser(UserEntity userEntity) {
    when(jwtTokenExtract.currentUser()).thenReturn(userEntity);

    when(userRepository.findById(userEntity.getId())).thenReturn(
        Optional.of(userEntity));
  }

  // 해당 경기 조회
  private void getGame(Long gameId) {
    when(gameRepository
        .findByIdAndDeletedDateTimeNull(gameId))
        .thenReturn(Optional.ofNullable(createdGameEntity1));
  }

  // 해당 경기에 참가 했는지 검사
  private void checkParticipantGame(Long gameId, Long userId, boolean flag) {
    when(participantGameRepository
        .existsByStatusAndGameIdAndUserId
            (ParticipantGameStatus.ACCEPT, gameId, userId))
        .thenReturn(flag);
  }

  // 해당 경기에 인원수를 검사
  private void countsAcceptedGame(Long gameId, int count) {
    when(participantGameRepository
        .countByStatusAndGameId
            (ParticipantGameStatus.ACCEPT, gameId))
        .thenReturn(count);
  }

  // 초대 받으려는 유저 조회
  private void getReceiverUser(Long receiverUserId) {
    when(userRepository
        .findById(receiverUserId))
        .thenReturn(Optional.ofNullable(receiverUser));
  }

  // 초대 받은 사람이 친구 인지 검사
  private void checkFriendUser(Long senderUserId, Long receiverUserId,
      boolean flag) {
    when(friendRepository.existsByUserIdAndFriendUserIdAndStatus
        (senderUserId, receiverUserId, FriendStatus.ACCEPT)).thenReturn(flag);
  }

  // 해당 경기에 이미 초대 요청 되어 있는지 검사
  private void checkAlreadyRequestInviteGame(Long gameId, Long receiverUserId,
      boolean flag) {
    when(inviteRepository
        .existsByInviteStatusAndGameIdAndReceiverUserId
            (InviteStatus.REQUEST, gameId, receiverUserId))
        .thenReturn(flag);
  }

  // 초대 받는 사람이 해당 경기에 참가 및 요청 되어 있는지 검사
  private void checkAlreadyAcceptOrApplyGame(Long gameId, Long userId,
      boolean flag) {
    when(participantGameRepository
        .existsByStatusInAndGameIdAndUserId
            (List.of(ParticipantGameStatus.ACCEPT, ParticipantGameStatus.APPLY),
                 gameId, userId))
        .thenReturn(flag);
  }

  // 경기 개설자 초대 정보 조회
  private void getGameCreatorRequestInviteEntity(Long inviteId) {
    when(inviteRepository
        .findByIdAndInviteStatus(inviteId, InviteStatus.REQUEST))
        .thenReturn(Optional.ofNullable(
            expectedGameCreatorRequestInviteEntity));
  }

  // 경기 팀원 초대 정보 조회
  private void getGameParticipantRequestInviteEntity(Long inviteId) {
    when(inviteRepository
        .findByIdAndInviteStatus(inviteId, InviteStatus.REQUEST))
        .thenReturn(Optional.ofNullable(
            expectedGameParticipantRequestInviteEntity));
  }



}
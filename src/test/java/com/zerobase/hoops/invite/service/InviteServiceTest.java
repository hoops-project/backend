package com.zerobase.hoops.invite.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.zerobase.hoops.invite.dto.AcceptInviteDto;
import com.zerobase.hoops.invite.dto.CancelInviteDto;
import com.zerobase.hoops.invite.dto.RejectInviteDto;
import com.zerobase.hoops.invite.dto.RequestInviteDto;
import com.zerobase.hoops.invite.dto.RequestInviteListDto;
import com.zerobase.hoops.invite.repository.InviteRepository;
import com.zerobase.hoops.invite.type.InviteStatus;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

  @InjectMocks
  private InviteService inviteService;

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

  private GameEntity createdGame1;
  private GameEntity createdGame2;

  private InviteEntity expectedGameCreatorRequestInviteEntity;
  private InviteEntity expectedGameCreatorAcceptInviteEntity;
  private InviteEntity expectedGameParticipantRequestInviteEntity;
  private InviteEntity expectedGameParticipantAcceptInviteEntity;
  private InviteEntity expectedGameCreatorRejectInviteEntity;

  private LocalDateTime fixedRequestedDateTime;
  private LocalDateTime fixedCanceledDateTime;
  private LocalDateTime fixedAcceptedDateTime;
  private LocalDateTime fixedRejectedDateTime;

  private RequestInviteDto.Request request;

  private InviteEntity requestInviteEntity;
  private InviteEntity cancelInviteEntity;
  private InviteEntity acceptInviteEntity;

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
    request = RequestInviteDto.Request.builder()
        .gameId(1L)
        .receiverUserId(2L)
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
    createdGame1 = GameEntity.builder()
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
    createdGame2 = GameEntity.builder()
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
    expectedGameCreatorRequestInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .requestedDateTime(fixedRequestedDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGame1)
        .build();
    expectedGameCreatorAcceptInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.ACCEPT)
        .requestedDateTime(fixedRequestedDateTime)
        .acceptedDateTime(fixedAcceptedDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGame1)
        .build();
    expectedGameCreatorRejectInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REJECT)
        .requestedDateTime(fixedRequestedDateTime)
        .rejectedDateTime(fixedRejectedDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGame1)
        .build();
        expectedGameParticipantRequestInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .requestedDateTime(fixedRequestedDateTime)
        .senderUser(receiverUser)
        .receiverUser(otherUser)
        .game(createdGame1)
        .build();
    expectedGameParticipantAcceptInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.ACCEPT)
        .requestedDateTime(fixedRequestedDateTime)
        .acceptedDateTime(fixedAcceptedDateTime)
        .senderUser(receiverUser)
        .receiverUser(otherUser)
        .game(createdGame1)
        .build();

    requestInviteEntity = new RequestInviteDto.Request()
        .toEntity(requestUser, receiverUser, createdGame1);
  }

  @Test
  @DisplayName("경기 초대 요청 성공")
  public void requestInviteGameSuccess() {
    //Given
    
    // 해당 경기 조회
    getGame(request.getGameId());

    // 해당 경기에 참가해 있는 사람이 초대할 경우를 가정
    checkParticipantGame(createdGame1.getId(), requestUser.getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(createdGame1.getId(), 1);

    // 초대 받으려는 유저 조회
    getReceiverUser(request.getReceiverUserId());
    
    // 초대 받은 사람이 친구 라고 가정
    checkFriendUser(requestUser.getId(), receiverUser.getId(), true);

    // 해당 경기에 이미 초대 요청 되어 있지 않다고 가정
    checkAlreadyRequestInviteGame
        (createdGame1.getId(), receiverUser.getId(), false);

    // 초대 받는 사람이 해당 경기에 참가 및 요청 안했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGame1.getId(), receiverUser.getId(), false);

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
    inviteService.validRequestInvite(request, requestUser);

    // Then
    verify(inviteRepository).save(inviteEntityArgumentCaptor.capture());

    // 저장된 엔티티 캡처
    InviteEntity saveInviteEntity = inviteEntityArgumentCaptor.getValue();

    assertEquals(expectedGameCreatorRequestInviteEntity, saveInviteEntity);
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 해당 경기가 초대 불가능일 때")
  public void requestInviteGameFailIfGameInviteNo() {
    //Given
    createdGame1.setInviteYn(false);

    // 해당 경기 조회
    getGame(request.getGameId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validRequestInvite(request, requestUser);
    });

    // then
    assertEquals(ErrorCode.NOT_GAME_INVITE, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 경기가 이미 시작이 되었을 때")
  public void requestInviteGameFailIfGameStarted() {
    //Given
    createdGame1.setStartDateTime(LocalDateTime.now().minusMinutes(1));


    // 해당 경기 조회
    getGame(request.getGameId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validRequestInvite(request, requestUser);
    });

    // then
    assertEquals(ErrorCode.ALREADY_GAME_START, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 해당 경기에 참가해 있지 않은 사람이 초대를 할 경우")
  public void requestInviteGameFailIfNotParticipantUserRequestGameInvite() {
    //Given

    // 해당 경기 조회
    getGame(request.getGameId());

    // 해당 경기에 참가해 있지 않은 사람이 초대할 경우를 가정
    checkParticipantGame(createdGame1.getId(), requestUser.getId(),
        false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validRequestInvite(request, requestUser);
    });

    // then
    assertEquals(ErrorCode.NOT_PARTICIPANT_GAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 해당 경기 인원이 다 찰 경우")
  public void requestInviteGameFailIfFulledGame() {
    //Given

    // 해당 경기 조회
    getGame(request.getGameId());

    // 해당 경기에 참가해 있는 사람이 초대할 경우를 가정
    checkParticipantGame(createdGame1.getId(), requestUser.getId(),
        true);

    // 해당 경기 인원이 6명 있다고 가정
    countsAcceptedGame(createdGame1.getId(), 6);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validRequestInvite(request, requestUser);
    });

    // then
    assertEquals(ErrorCode.FULL_PARTICIPANT, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 친구가 아닌 사람이 요청할 때")
  public void requestInviteGameFailIfNotFriendRequestInvite() {
    //Given

    // 해당 경기 조회
    getGame(request.getGameId());

    // 해당 경기에 참가해 있는 사람이 초대할 경우를 가정
    checkParticipantGame(createdGame1.getId(), requestUser.getId(),
        true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(createdGame1.getId(), 1);

    // 초대 받으려는 유저 조회
    getReceiverUser(request.getReceiverUserId());

    // 초대 받은 사람이 친구가 아니 라고 가정
    checkFriendUser(requestUser.getId(), receiverUser.getId(), false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validRequestInvite(request, requestUser);
    });

    // then
    assertEquals(ErrorCode.NOT_FOUND_ACCEPT_FRIEND, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 해당 경기에 이미 초대 요청 되어 있을 때")
  public void requestInviteGameFailIfAlreadyRequestGameInvite() {
    //Given

    // 해당 경기 조회
    getGame(request.getGameId());

    // 해당 경기에 참가해 있는 사람이 초대할 경우를 가정
    checkParticipantGame(createdGame1.getId(), requestUser.getId(),
        true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(createdGame1.getId(), 1);

    // 초대 받으려는 유저 조회
    getReceiverUser(request.getReceiverUserId());

    // 초대 받은 사람이 친구 라고 가정
    checkFriendUser(requestUser.getId(), receiverUser.getId(), true);

    // 해당 경기에 이미 초대 요청 되어 있다고 가정
    checkAlreadyRequestInviteGame
        (createdGame1.getId(), receiverUser.getId(), true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validRequestInvite(request, requestUser);
    });

    // then
    assertEquals(ErrorCode.ALREADY_INVITE_GAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 실패 : 해당 경기에 이미 참가 하거나 요청한 경우")
  public void requestInviteGameFailIfAlreadyAcceptOrApplyGame() {
    //Given

    // 해당 경기 조회
    getGame(request.getGameId());

    // 해당 경기에 참가해 있는 사람이 초대할 경우를 가정
    checkParticipantGame(createdGame1.getId(), requestUser.getId(),
        true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(createdGame1.getId(), 1);

    // 초대 받으려는 유저 조회
    getReceiverUser(request.getReceiverUserId());

    // 초대 받은 사람이 친구 라고 가정
    checkFriendUser(requestUser.getId(), receiverUser.getId(), true);

    // 해당 경기에 이미 초대 요청 되어 있지 않다고 가정
    checkAlreadyRequestInviteGame
        (createdGame1.getId(), receiverUser.getId(), false);

    // 초대 받는 사람이 해당 경기에 참가 및 요청 했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGame1.getId(), receiverUser.getId(), true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validRequestInvite(request, requestUser);
    });

    // then
    assertEquals(ErrorCode.ALREADY_PARTICIPANT_GAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 취소 성공")
  public void cancelInvitationSuccess() {
    //Given
    CancelInviteDto.Request request = CancelInviteDto.Request.builder()
        .inviteId(1L)
        .build();

    InviteEntity expectedcancelInviteEntity = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.CANCEL)
        .requestedDateTime(fixedRequestedDateTime)
        .canceledDateTime(fixedCanceledDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGame1)
        .build();

    Instant fixedInstant = fixedCanceledDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(request.getInviteId());

    cancelInviteEntity = InviteEntity.toCancelEntity(
        expectedGameCreatorRequestInviteEntity, clock);

    when(inviteRepository.save(cancelInviteEntity)).thenReturn(cancelInviteEntity);

    // when
    inviteService.validCancelInvite(request, requestUser);

    // Then
    assertEquals(expectedcancelInviteEntity, cancelInviteEntity);
  }

  @Test
  @DisplayName("경기 초대 요청 취소 실패 : 본인이 경기 초대 요청한 것이 아닐때")
  public void cancelInvitationFailIfNotMyRequestInvitationToGame() {
    //Given
    CancelInviteDto.Request request = CancelInviteDto.Request.builder()
        .inviteId(1L)
        .build();

    expectedGameCreatorRequestInviteEntity.assignSenderUser(receiverUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(request.getInviteId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validCancelInvite(request, requestUser);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_REQUEST, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 수락 성공 : 경기 개설자가 요청할 때")
  public void acceptInvitationSuccessIfRequestByGameCreator() {
    //Given
    AcceptInviteDto.Request request = AcceptInviteDto.Request.builder()
        .inviteId(1L)
        .build();

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
        .game(createdGame1)
        .user(receiverUser)
        .build();

    acceptInviteEntity =
        InviteEntity.toAcceptEntity(expectedGameCreatorRequestInviteEntity, nowDateTime);

    ParticipantGameEntity gameCreatorInvite =
        new ParticipantGameEntity().gameCreatorInvite
            (expectedGameCreatorRequestInviteEntity, nowDateTime);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(request.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있다고 가정
    checkParticipantGame(createdGame1.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteEntity.getGame().getId(), 1);

    // 수락 하는 사람이 해당 경기에 참가 및 요청 안했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGame1.getId(), receiverUser.getId(), false);

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
    inviteService.validAcceptInvite(request, receiverUser);

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
  public void acceptInvitationSuccessIfRequestByGameParticipant() {
    //Given
    AcceptInviteDto.Request request = AcceptInviteDto.Request.builder()
        .inviteId(1L)
        .build();

    Instant fixedInstant = fixedAcceptedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    LocalDateTime createdDateTime = LocalDateTime.now(clock);

    ParticipantGameEntity expectedParticipantGameEntity = ParticipantGameEntity
        .builder()
        .id(3L)
        .status(APPLY)
        .createdDateTime(createdDateTime)
        .game(createdGame1)
        .user(otherUser)
        .build();

    acceptInviteEntity =
        InviteEntity.toAcceptEntity(expectedGameParticipantRequestInviteEntity, createdDateTime);

    ParticipantGameEntity gameUserInvite =
        new ParticipantGameEntity()
            .gameUserInvite(expectedGameParticipantRequestInviteEntity);


    // 해당 초대 정보 조회
    getGameParticipantRequestInviteEntity(request.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(otherUser.getId(),
        expectedGameParticipantRequestInviteEntity.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있다고 가정
    checkParticipantGame(createdGame1.getId(),
        expectedGameParticipantRequestInviteEntity.getSenderUser().getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteEntity.getGame().getId(), 1);

    // 수락 하는 사람이 해당 경기에 참가 및 요청 안했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGame1.getId(), otherUser.getId(), false);

    when(inviteRepository.save(acceptInviteEntity)).thenReturn(acceptInviteEntity);
    // 경기 개설자가 초대 한 경우 수락 -> 경기 참가
    when(participantGameRepository.save(gameUserInvite)).thenAnswer(invocation -> {
      ParticipantGameEntity savedParticipantGameEntity =
          invocation.getArgument(0);
      savedParticipantGameEntity.setId(3L); // id 할당
      savedParticipantGameEntity.setCreatedDateTime(createdDateTime);
      return savedParticipantGameEntity;
    });

    // ArgumentCaptor를 사용하여 저장된 엔티티를 캡처합니다.
    ArgumentCaptor<ParticipantGameEntity> participantGameEntityArgumentCaptor =
        ArgumentCaptor.forClass(ParticipantGameEntity.class);

    // when
    inviteService.validAcceptInvite(request, otherUser);

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
    AcceptInviteDto.Request request = AcceptInviteDto.Request.builder()
        .inviteId(1L)
        .build();

    expectedGameCreatorRequestInviteEntity.assignReceiverUser(requestUser);


    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(request.getInviteId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validAcceptInvite(request, receiverUser);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_INVITE_REQUEST, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 수락 실패 : 친구가 아닌 사람이 초대 요청 했을때")
  public void acceptInvitation_failIfNotFriendRequestInvite() {
    //Given
    AcceptInviteDto.Request request = AcceptInviteDto.Request.builder()
        .inviteId(1L)
        .build();

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(request.getInviteId());

    // 초대 한 사람이 친구가 아니 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validAcceptInvite(request, receiverUser);
    });

    // then
    assertEquals(ErrorCode.NOT_FOUND_ACCEPT_FRIEND, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 수락 실패 : 초대한 사람이 해당 경기에 참가해 있지 않을 때")
  public void acceptInvitation_failIfSenderUserNotParticipantGame() {
    //Given
    AcceptInviteDto.Request request = AcceptInviteDto.Request.builder()
        .inviteId(1L)
        .build();

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(request.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있지 않다고 가정
    checkParticipantGame(createdGame1.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validAcceptInvite(request, receiverUser);
    });

    // then
    assertEquals(ErrorCode.NOT_PARTICIPANT_GAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 수락 실패 : 해당 경기에 인원이 다 찼을 때")
  public void acceptInvitation_failIfFulledGame() {
    //Given
    AcceptInviteDto.Request request = AcceptInviteDto.Request.builder()
        .inviteId(1L)
        .build();

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(request.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있지 않다고 가정
    checkParticipantGame(createdGame1.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 해당 경기 인원이 다 찼다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteEntity.getGame().getId(), 6);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validAcceptInvite(request, receiverUser);
    });

    // then
    assertEquals(ErrorCode.FULL_PARTICIPANT, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 수락 실패 : 수락 하는 사람이 해당 경기에 참가 및 요청 했을 때")
  public void acceptInvitation_failIfApplyOrAcceptGame() {
    //Given
    AcceptInviteDto.Request request = AcceptInviteDto.Request.builder()
        .inviteId(1L)
        .build();

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(request.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있지 않다고 가정
    checkParticipantGame(createdGame1.getId(),
        expectedGameCreatorRequestInviteEntity.getSenderUser().getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteEntity.getGame().getId(), 1);

    // 수락 하는 사람이 해당 경기에 참가 및 요청 했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGame1.getId(), receiverUser.getId(), true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validAcceptInvite(request, receiverUser);
    });

    // then
    assertEquals(ErrorCode.ALREADY_PARTICIPANT_GAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 초대 요청 거절 성공")
  public void rejectInvitation_success() {
    //Given
    RejectInviteDto.Request request = RejectInviteDto.Request.builder()
        .inviteId(1L)
        .build();

    Instant fixedInstant = fixedRejectedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(request.getInviteId());

    InviteEntity rejectEntity = InviteEntity.toRejectEntity
        (expectedGameCreatorRequestInviteEntity, clock);


    when(inviteRepository.save(rejectEntity))
        .thenReturn(rejectEntity);

    // when
    inviteService.validRejectInvite(request, receiverUser);

    // Then
    assertEquals(expectedGameCreatorRejectInviteEntity ,rejectEntity);
  }

  @Test
  @DisplayName("경기 초대 요청 거절 실패 : 본인이 받은 초대 요청이 아닐때")
  public void rejectInvitation_failIfNotMyRequestInvitation() {
    //Given
    RejectInviteDto.Request request = RejectInviteDto.Request.builder()
        .inviteId(1L)
        .build();

    expectedGameCreatorRequestInviteEntity.assignReceiverUser(requestUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteEntity(request.getInviteId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      inviteService.validRejectInvite(request, receiverUser);
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
        .game(createdGame1)
        .build();

    InviteEntity inviteEntity2 = InviteEntity.builder()
        .id(2L)
        .inviteStatus(InviteStatus.REQUEST)
        .senderUser(receiverUser)
        .receiverUser(otherUser)
        .game(createdGame2)
        .build();

    Pageable pageable = PageRequest.of(0, 2);

    List<InviteEntity> inviteEntityList = List.of(inviteEntity1, inviteEntity2);

    Page<InviteEntity> inviteEntityPage =
        new PageImpl<>(inviteEntityList, pageable, 2);

    List<RequestInviteListDto.Response> expectList = inviteEntityList.stream()
        .map(RequestInviteListDto.Response::toDto)
        .toList();

    when(inviteRepository.findByInviteStatusAndReceiverUserId
        (InviteStatus.REQUEST, otherUser.getId(), pageable))
        .thenReturn(inviteEntityPage);

    // when
    List<RequestInviteListDto.Response> result =
        inviteService.validGetRequestInviteList(pageable, otherUser);

    // Then
    assertEquals(expectList, result);
  }

  // 해당 경기 조회
  private void getGame(Long gameId) {
    when(gameRepository
        .findByIdAndDeletedDateTimeNull(gameId))
        .thenReturn(Optional.ofNullable(createdGame1));
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
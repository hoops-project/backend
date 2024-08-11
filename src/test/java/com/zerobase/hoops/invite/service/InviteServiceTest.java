package com.zerobase.hoops.invite.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.document.InviteDocument;
import com.zerobase.hoops.document.ParticipantGameDocument;
import com.zerobase.hoops.document.UserDocument;
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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

  private UserDocument requestUser;
  private UserDocument receiverUser;
  private UserDocument otherUser;

  private GameDocument createdGame1;
  private GameDocument createdGame2;

  private InviteDocument expectedGameCreatorRequestInviteDocument;
  private InviteDocument expectedGameCreatorAcceptInviteDocument;
  private InviteDocument expectedGameParticipantRequestInviteDocument;
  private InviteDocument expectedGameParticipantAcceptInviteDocument;
  private InviteDocument expectedGameCreatorRejectInviteDocument;

  private OffsetDateTime fixedRequestedDateTime;
  private OffsetDateTime fixedCanceledDateTime;
  private OffsetDateTime fixedAcceptedDateTime;
  private OffsetDateTime fixedRejectedDateTime;

  private RequestInviteDto.Request request;

  private InviteDocument requestInviteDocument;
  private InviteDocument cancelInviteDocument;
  private InviteDocument acceptInviteDocument;

  @BeforeEach
  void setUp() {
    fixedRequestedDateTime = OffsetDateTime.of(LocalDateTime.of(2024, 6, 9, 0, 0,
        0), ZoneOffset.ofHours(9));
    fixedCanceledDateTime = OffsetDateTime.of(LocalDateTime.of(2024, 6, 10, 0
        , 0,
        0), ZoneOffset.ofHours(9));
    fixedAcceptedDateTime = OffsetDateTime.of(LocalDateTime.of(2024, 6, 10, 0
        , 0,
        0), ZoneOffset.ofHours(9));
    fixedRejectedDateTime = OffsetDateTime.of(LocalDateTime.of(2024, 6, 10, 0
        , 0,
        0), ZoneOffset.ofHours(9));
    request = RequestInviteDto.Request.builder()
        .gameId("1")
        .receiverUserId("2")
        .build();
    requestUser = UserDocument.builder()
        .id("1")
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
        .createdDateTime(OffsetDateTime.now(ZoneOffset.ofHours(9)))
        .emailAuth(true)
        .build();
    receiverUser = UserDocument.builder()
        .id("2")
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
        .createdDateTime(OffsetDateTime.now(ZoneOffset.ofHours(9)))
        .emailAuth(true)
        .build();
    otherUser = UserDocument.builder()
        .id("6")
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
        .createdDateTime(OffsetDateTime.now(ZoneOffset.ofHours(9)))
        .emailAuth(true)
        .build();
    createdGame1 = GameDocument.builder()
        .id("1")
        .title("테스트제목")
        .content("테스트내용")
        .headCount(6L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(OffsetDateTime.now(ZoneOffset.ofHours(9)).plusHours(1))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.THREEONTHREE)
        .cityName(CityName.SEOUL)
        .user(requestUser)
        .build();
    createdGame2 = GameDocument.builder()
        .id("2")
        .title("테스트제목2")
        .content("테스트내용2")
        .headCount(6L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(OffsetDateTime.now(ZoneOffset.ofHours(9)).plusHours(1))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.THREEONTHREE)
        .cityName(CityName.SEOUL)
        .user(receiverUser)
        .build();
    expectedGameCreatorRequestInviteDocument = InviteDocument.builder()
        .id("1")
        .inviteStatus(InviteStatus.REQUEST)
        .requestedDateTime(fixedRequestedDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGame1)
        .build();
    expectedGameCreatorAcceptInviteDocument = InviteDocument.builder()
        .id("1")
        .inviteStatus(InviteStatus.ACCEPT)
        .requestedDateTime(fixedRequestedDateTime)
        .acceptedDateTime(fixedAcceptedDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGame1)
        .build();
    expectedGameCreatorRejectInviteDocument = InviteDocument.builder()
        .id("1")
        .inviteStatus(InviteStatus.REJECT)
        .requestedDateTime(fixedRequestedDateTime)
        .rejectedDateTime(fixedRejectedDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGame1)
        .build();
        expectedGameParticipantRequestInviteDocument = InviteDocument.builder()
        .id("1")
        .inviteStatus(InviteStatus.REQUEST)
        .requestedDateTime(fixedRequestedDateTime)
        .senderUser(receiverUser)
        .receiverUser(otherUser)
        .game(createdGame1)
        .build();
    expectedGameParticipantAcceptInviteDocument = InviteDocument.builder()
        .id("1")
        .inviteStatus(InviteStatus.ACCEPT)
        .requestedDateTime(fixedRequestedDateTime)
        .acceptedDateTime(fixedAcceptedDateTime)
        .senderUser(receiverUser)
        .receiverUser(otherUser)
        .game(createdGame1)
        .build();
  }

  @Test
  @DisplayName("경기 초대 요청 성공")
  public void requestInviteGameSuccess() {
    //Given
    long inviteId = 0L;

    Instant fixedInstant = fixedRequestedDateTime.toInstant();
    // Clock의 instant()와 getZone() 메서드를 설정
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneOffset.ofHours(9));

    requestInviteDocument = new RequestInviteDto.Request()
        .toDocument(requestUser, receiverUser, createdGame1, inviteId+1, clock);

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

    when(inviteRepository.count()).thenReturn(inviteId);

    when(inviteRepository.save(requestInviteDocument)).thenReturn(requestInviteDocument);

    // when
    inviteService.validRequestInvite(request, requestUser);

    // Then
    assertEquals(expectedGameCreatorRequestInviteDocument, requestInviteDocument);
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
    createdGame1.setStartDateTime(OffsetDateTime.now(ZoneOffset.ofHours(9)).minusMinutes(1));


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
        .inviteId("1")
        .build();

    InviteDocument expectedcancelInviteDocument = InviteDocument.builder()
        .id("1")
        .inviteStatus(InviteStatus.CANCEL)
        .requestedDateTime(fixedRequestedDateTime)
        .canceledDateTime(fixedCanceledDateTime)
        .senderUser(requestUser)
        .receiverUser(receiverUser)
        .game(createdGame1)
        .build();

    Instant fixedInstant = fixedCanceledDateTime.toInstant();
    // Clock의 instant()와 getZone() 메서드를 설정
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneOffset.ofHours(9));

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteDocument(request.getInviteId());

    cancelInviteDocument = InviteDocument.toCancelDocument(
        expectedGameCreatorRequestInviteDocument, clock);

    when(inviteRepository.save(cancelInviteDocument)).thenReturn(cancelInviteDocument);

    // when
    inviteService.validCancelInvite(request, requestUser);

    // Then
    assertEquals(expectedcancelInviteDocument, cancelInviteDocument);
  }

  @Test
  @DisplayName("경기 초대 요청 취소 실패 : 본인이 경기 초대 요청한 것이 아닐때")
  public void cancelInvitationFailIfNotMyRequestInvitationToGame() {
    //Given
    CancelInviteDto.Request request = CancelInviteDto.Request.builder()
        .inviteId("1")
        .build();

    expectedGameCreatorRequestInviteDocument.assignSenderUser(receiverUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteDocument(request.getInviteId());

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
        .inviteId("1")
        .build();

    long participantGameId = 1L;

    Instant fixedInstant = fixedAcceptedDateTime.toInstant();
    // Clock의 instant()와 getZone() 메서드를 설정
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneOffset.ofHours(9));

    OffsetDateTime nowDateTime = OffsetDateTime.now(clock);

    ParticipantGameDocument expectedParticipantGameDocument = ParticipantGameDocument
        .builder()
        .id("2")
        .status(ACCEPT)
        .createdDateTime(nowDateTime)
        .acceptedDateTime(nowDateTime)
        .game(createdGame1)
        .user(receiverUser)
        .build();

    acceptInviteDocument =
        InviteDocument.toAcceptDocument(expectedGameCreatorRequestInviteDocument, nowDateTime);

    ParticipantGameDocument gameCreatorInvite =
        new ParticipantGameDocument().gameCreatorInvite
            (expectedGameCreatorRequestInviteDocument, nowDateTime,
                participantGameId+1);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteDocument(request.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteDocument.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있다고 가정
    checkParticipantGame(createdGame1.getId(),
        expectedGameCreatorRequestInviteDocument.getSenderUser().getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteDocument.getGame().getId(), 1);

    // 수락 하는 사람이 해당 경기에 참가 및 요청 안했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGame1.getId(), receiverUser.getId(), false);

    when(participantGameRepository.count()).thenReturn(participantGameId);

    when(inviteRepository.save(acceptInviteDocument)).thenReturn(acceptInviteDocument);

    // 경기 개설자가 초대 한 경우 수락 -> 경기 참가
    when(participantGameRepository.save(gameCreatorInvite)).thenReturn(gameCreatorInvite);

    // when
    inviteService.validAcceptInvite(request, receiverUser);

    // Then
    assertEquals(expectedGameCreatorAcceptInviteDocument, acceptInviteDocument);
    assertEquals(expectedParticipantGameDocument, gameCreatorInvite);
  }

  @Test
  @DisplayName("경기 초대 요청 수락 성공 : 팀원이 초대한 경우")
  public void acceptInvitationSuccessIfRequestByGameParticipant() {
    //Given
    AcceptInviteDto.Request request = AcceptInviteDto.Request.builder()
        .inviteId("1")
        .build();

    long participantGameId = 2L;

    Instant fixedInstant = fixedAcceptedDateTime.toInstant();
    // Clock의 instant()와 getZone() 메서드를 설정
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneOffset.ofHours(9));

    OffsetDateTime createdDateTime = OffsetDateTime.now(clock);

    ParticipantGameDocument expectedParticipantGameDocument = ParticipantGameDocument
        .builder()
        .id("3")
        .status(APPLY)
        .createdDateTime(createdDateTime)
        .game(createdGame1)
        .user(otherUser)
        .build();

    acceptInviteDocument =
        InviteDocument.toAcceptDocument(expectedGameParticipantRequestInviteDocument, createdDateTime);

    ParticipantGameDocument gameUserInvite =
        new ParticipantGameDocument()
            .gameUserInvite(expectedGameParticipantRequestInviteDocument,
                createdDateTime, participantGameId+1);

    // 해당 초대 정보 조회
    getGameParticipantRequestInviteDocument(request.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(otherUser.getId(),
        expectedGameParticipantRequestInviteDocument.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있다고 가정
    checkParticipantGame(createdGame1.getId(),
        expectedGameParticipantRequestInviteDocument.getSenderUser().getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteDocument.getGame().getId(), 1);

    // 수락 하는 사람이 해당 경기에 참가 및 요청 안했을 경우를 가정
    checkAlreadyAcceptOrApplyGame
        (createdGame1.getId(), otherUser.getId(), false);

    when(inviteRepository.save(acceptInviteDocument)).thenReturn(acceptInviteDocument);

    when(participantGameRepository.count()).thenReturn(participantGameId);

    // 경기 개설자가 초대 한 경우 수락 -> 경기 참가
    when(participantGameRepository.save(gameUserInvite)).thenReturn(gameUserInvite);

    // when
    inviteService.validAcceptInvite(request, otherUser);

    // Then
    assertEquals(expectedGameParticipantAcceptInviteDocument, acceptInviteDocument);
    assertEquals(expectedParticipantGameDocument, gameUserInvite);
  }

  @Test
  @DisplayName("경기 초대 요청 수락 실패 : 본인이 받은 초대 요청이 아닐때")
  public void acceptInvitation_failIfNotMyRequestInvite() {
    //Given
    AcceptInviteDto.Request request = AcceptInviteDto.Request.builder()
        .inviteId("1")
        .build();

    expectedGameCreatorRequestInviteDocument.assignReceiverUser(requestUser);


    // 해당 초대 정보 조회
    getGameCreatorRequestInviteDocument(request.getInviteId());

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
        .inviteId("1")
        .build();

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteDocument(request.getInviteId());

    // 초대 한 사람이 친구가 아니 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteDocument.getSenderUser().getId(), false);

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
        .inviteId("1")
        .build();

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteDocument(request.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteDocument.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있지 않다고 가정
    checkParticipantGame(createdGame1.getId(),
        expectedGameCreatorRequestInviteDocument.getSenderUser().getId(), false);

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
        .inviteId("1")
        .build();

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteDocument(request.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteDocument.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있지 않다고 가정
    checkParticipantGame(createdGame1.getId(),
        expectedGameCreatorRequestInviteDocument.getSenderUser().getId(), true);

    // 해당 경기 인원이 다 찼다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteDocument.getGame().getId(), 6);

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
        .inviteId("1")
        .build();

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteDocument(request.getInviteId());

    // 초대 한 사람이 친구 라고 가정
    checkFriendUser(receiverUser.getId(),
        expectedGameCreatorRequestInviteDocument.getSenderUser().getId(), true);

    // 초대한 사람이 해당 경기에 참가해 있지 않다고 가정
    checkParticipantGame(createdGame1.getId(),
        expectedGameCreatorRequestInviteDocument.getSenderUser().getId(), true);

    // 해당 경기 인원이 경기개설자(1명 만) 있다고 가정
    countsAcceptedGame(expectedGameCreatorRequestInviteDocument.getGame().getId(), 1);

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
        .inviteId("1")
        .build();

    Instant fixedInstant = fixedRejectedDateTime.toInstant();
    // Clock의 instant()와 getZone() 메서드를 설정
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneOffset.ofHours(9));

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteDocument(request.getInviteId());

    InviteDocument rejectDocument = InviteDocument.toRejectDocument
        (expectedGameCreatorRequestInviteDocument, clock);


    when(inviteRepository.save(rejectDocument))
        .thenReturn(rejectDocument);

    // when
    inviteService.validRejectInvite(request, receiverUser);

    // Then
    assertEquals(expectedGameCreatorRejectInviteDocument ,rejectDocument);
  }

  @Test
  @DisplayName("경기 초대 요청 거절 실패 : 본인이 받은 초대 요청이 아닐때")
  public void rejectInvitation_failIfNotMyRequestInvitation() {
    //Given
    RejectInviteDto.Request request = RejectInviteDto.Request.builder()
        .inviteId("1")
        .build();

    expectedGameCreatorRequestInviteDocument.assignReceiverUser(requestUser);

    // 해당 초대 정보 조회
    getGameCreatorRequestInviteDocument(request.getInviteId());

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
    InviteDocument inviteDocument1 = InviteDocument.builder()
        .id("1")
        .inviteStatus(InviteStatus.REQUEST)
        .senderUser(requestUser)
        .receiverUser(otherUser)
        .game(createdGame1)
        .build();

    InviteDocument inviteDocument2 = InviteDocument.builder()
        .id("2")
        .inviteStatus(InviteStatus.REQUEST)
        .senderUser(receiverUser)
        .receiverUser(otherUser)
        .game(createdGame2)
        .build();

    Pageable pageable = PageRequest.of(0, 2);

    List<InviteDocument> inviteDocumentList = List.of(inviteDocument1, inviteDocument2);

    Page<InviteDocument> inviteDocumentPage =
        new PageImpl<>(inviteDocumentList, pageable, 2);

    List<RequestInviteListDto.Response> expectList = inviteDocumentList.stream()
        .map(RequestInviteListDto.Response::toDto)
        .toList();

    when(inviteRepository.findByInviteStatusAndReceiverUserId
        (InviteStatus.REQUEST, otherUser.getId(), pageable))
        .thenReturn(inviteDocumentPage);

    // when
    List<RequestInviteListDto.Response> result =
        inviteService.validGetRequestInviteList(pageable, otherUser);

    // Then
    assertEquals(expectList, result);
  }

  // 해당 경기 조회
  private void getGame(String gameId) {
    when(gameRepository
        .findByIdAndDeletedDateTimeNull(gameId))
        .thenReturn(Optional.ofNullable(createdGame1));
  }

  // 해당 경기에 참가 했는지 검사
  private void checkParticipantGame(String gameId, String userId, boolean flag) {
    when(participantGameRepository
        .existsByStatusAndGameIdAndUserId
            (ParticipantGameStatus.ACCEPT, gameId, userId))
        .thenReturn(flag);
  }

  // 해당 경기에 인원수를 검사
  private void countsAcceptedGame(String gameId, int count) {
    when(participantGameRepository
        .countByStatusAndGameId
            (ParticipantGameStatus.ACCEPT, gameId))
        .thenReturn(count);
  }

  // 초대 받으려는 유저 조회
  private void getReceiverUser(String receiverUserId) {
    when(userRepository
        .findById(receiverUserId))
        .thenReturn(Optional.ofNullable(receiverUser));
  }

  // 초대 받은 사람이 친구 인지 검사
  private void checkFriendUser(String senderUserId, String receiverUserId,
      boolean flag) {
    when(friendRepository.existsByUserIdAndFriendUserIdAndStatus
        (senderUserId, receiverUserId, FriendStatus.ACCEPT)).thenReturn(flag);
  }

  // 해당 경기에 이미 초대 요청 되어 있는지 검사
  private void checkAlreadyRequestInviteGame(String gameId, String receiverUserId,
      boolean flag) {
    when(inviteRepository
        .existsByInviteStatusAndGameIdAndReceiverUserId
            (InviteStatus.REQUEST, gameId, receiverUserId))
        .thenReturn(flag);
  }

  // 초대 받는 사람이 해당 경기에 참가 및 요청 되어 있는지 검사
  private void checkAlreadyAcceptOrApplyGame(String gameId, String userId,
      boolean flag) {
    when(participantGameRepository
        .existsByStatusInAndGameIdAndUserId
            (List.of(ParticipantGameStatus.ACCEPT, ParticipantGameStatus.APPLY),
                 gameId, userId))
        .thenReturn(flag);
  }

  // 경기 개설자 초대 정보 조회
  private void getGameCreatorRequestInviteDocument(String inviteId) {
    when(inviteRepository
        .findByIdAndInviteStatus(inviteId, InviteStatus.REQUEST))
        .thenReturn(Optional.ofNullable(
            expectedGameCreatorRequestInviteDocument));
  }

  // 경기 팀원 초대 정보 조회
  private void getGameParticipantRequestInviteDocument(String inviteId) {
    when(inviteRepository
        .findByIdAndInviteStatus(inviteId, InviteStatus.REQUEST))
        .thenReturn(Optional.ofNullable(
            expectedGameParticipantRequestInviteDocument));
  }



}
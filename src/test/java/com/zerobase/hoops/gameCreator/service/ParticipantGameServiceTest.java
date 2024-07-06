package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.KICKOUT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.REJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.alarm.repository.EmitterRepository;
import com.zerobase.hoops.alarm.repository.NotificationRepository;
import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.gameCreator.dto.AcceptParticipantDto;
import com.zerobase.hoops.gameCreator.dto.AcceptParticipantListDto;
import com.zerobase.hoops.gameCreator.dto.ApplyParticipantListDto;
import com.zerobase.hoops.gameCreator.dto.KickoutParticipantDto;
import com.zerobase.hoops.gameCreator.dto.RejectParticipantDto;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ParticipantGameServiceTest {

  @InjectMocks
  private ParticipantGameService participantGameService;

  @Mock
  private NotificationService notificationService;

  @Mock
  private ParticipantGameRepository participantGameRepository;

  @Mock
  private GameRepository gameRepository;

  @Mock
  private NotificationRepository notificationRepository;

  @Spy
  private Clock clock;

  @Mock
  private EmitterRepository emitterRepository;

  private LocalDateTime fixedStartDateTime;
  private LocalDateTime fixedCreatedDateTime;
  private LocalDateTime fixedAcceptedDateTime;
  private LocalDateTime fixedRejectedDateTime;
  private LocalDateTime fixedKickoutedDateTime;
  private UserEntity createdUser;
  private UserEntity applyUser;
  private GameEntity expectedCreatedGame;
  private GameEntity expectedOtherCreatedGame;
  private ParticipantGameEntity expectedApplyParticipantGame;
  private ParticipantGameEntity expectedAcceptParticipantGame;

  @BeforeEach
  void setUp() {
    fixedStartDateTime = LocalDateTime.now().plusHours(1L);
    fixedCreatedDateTime = LocalDateTime
        .of(2024, 6, 9, 0, 0, 0);
    fixedAcceptedDateTime = LocalDateTime
        .of(2024, 6, 9, 1, 0, 0);
    fixedRejectedDateTime = LocalDateTime
        .of(2024, 6, 9, 1, 0, 0);
    fixedKickoutedDateTime = LocalDateTime
        .of(2024, 6, 9, 2, 0, 0);
    createdUser = UserEntity.builder()
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
        .createdDateTime(fixedCreatedDateTime)
        .emailAuth(true)
        .build();
    applyUser = UserEntity.builder()
        .id(2L)
        .loginId("test1")
        .password("Testpass12!@")
        .email("test1@example.com")
        .name("test1")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("test1")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(new ArrayList<>(List.of("ROLE_USER")))
        .createdDateTime(fixedCreatedDateTime)
        .emailAuth(true)
        .build();
    expectedCreatedGame = GameEntity.builder()
        .id(1L)
        .title("테스트제목")
        .content("테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(fixedStartDateTime)
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .cityName(CityName.SEOUL)
        .user(createdUser)
        .build();
    expectedOtherCreatedGame = GameEntity.builder()
        .id(2L)
        .title("테스트제목")
        .content("테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(fixedStartDateTime)
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .cityName(CityName.SEOUL)
        .user(applyUser)
        .build();
    expectedApplyParticipantGame = ParticipantGameEntity.builder()
        .id(2L)
        .status(APPLY)
        .createdDateTime(fixedCreatedDateTime)
        .game(expectedCreatedGame)
        .user(applyUser)
        .build();
    expectedAcceptParticipantGame = ParticipantGameEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(fixedCreatedDateTime)
        .acceptedDateTime(fixedAcceptedDateTime)
        .game(expectedCreatedGame)
        .user(applyUser)
        .build();
  }

  @Test
  @DisplayName("경기 지원자 리스트 조회 성공")
  void testGetApplyParticipantListSuccess() {
    // Given
    Long gameId = 1L;

    ParticipantGameEntity applyParticipantGame =
        ParticipantGameEntity.builder()
        .id(2L)
        .status(APPLY)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .game(expectedCreatedGame)
        .user(applyUser)
        .build();

    Pageable pageable = PageRequest.of(0, 10);

    List<ParticipantGameEntity> applyParticipantGameList
        = List.of(applyParticipantGame);

    Page<ParticipantGameEntity> expectedPage =
        new PageImpl<>(applyParticipantGameList, pageable, applyParticipantGameList.size());

    List<ApplyParticipantListDto.Response> expectedList = expectedPage.stream()
        .map(ApplyParticipantListDto.Response::toDto)
        .toList();

    getGame(gameId, expectedCreatedGame);

    getParticipantPage(APPLY, gameId, pageable, expectedPage);

    // when
    List<ApplyParticipantListDto.Response> result = participantGameService
        .validApplyParticipantList(gameId, pageable, createdUser);

    // Then
    assertEquals(expectedList, result);
  }

  @Test
  @DisplayName("경기 지원자 리스트 조회 실패 : 로그인 한 유저가 경기 개설자가 아닐때")
  void testGetApplyParticipantListFailIfNotGameCreator() {
    // Given
    Long gameId = 2L;

    Pageable pageable = PageRequest.of(0, 10);

    getGame(gameId, expectedOtherCreatedGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.validApplyParticipantList(gameId, pageable, createdUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_GAME_CREATOR, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 참가자 리스트 조회 성공")
  void testGetAcceptParticipantListSuccess() {
    // Given
    Long gameId = 1L;

    ParticipantGameEntity acceptParticipantGame =
        ParticipantGameEntity.builder()
            .id(1L)
            .status(ACCEPT)
            .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
            .game(expectedCreatedGame)
            .user(createdUser)
            .build();

    Pageable pageable = PageRequest.of(0, 10);

    List<ParticipantGameEntity> acceptParticipantGameList
        = List.of(acceptParticipantGame);

    Page<ParticipantGameEntity> expectedPage =
        new PageImpl<>(acceptParticipantGameList, pageable, acceptParticipantGameList.size());

    List<AcceptParticipantListDto.Response> expectedList = expectedPage.stream()
        .map(AcceptParticipantListDto.Response::toDto)
        .toList();
    
    getGame(gameId, expectedCreatedGame);

    getParticipantPage(ACCEPT, gameId, pageable, expectedPage);

    // when
    List<AcceptParticipantListDto.Response> result = participantGameService
        .validAcceptParticipantList(gameId, pageable, createdUser);

    // Then
    assertEquals(expectedList, result);
  }

  @Test
  @DisplayName("경기 지원자 수락 성공")
  void testAcceptParticipantSuccess() {
    // Given
    AcceptParticipantDto.Request request = AcceptParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    Instant fixedInstant = fixedAcceptedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    ParticipantGameEntity acceptParticipantGame =
        new ParticipantGameEntity().setAccept
            (expectedApplyParticipantGame, clock);

    

    // 경기 참가 정보 조회
    getParticipantGame(request.getParticipantId(), APPLY,
        expectedApplyParticipantGame);
    
    // 경기 조회
    getGame(expectedApplyParticipantGame.getGame().getId(), expectedCreatedGame);

    // 경기에 참가자가 1명 있다고 가정
    countsParticipantGame(ACCEPT, expectedCreatedGame.getId(), 1);

    when(participantGameRepository.save(acceptParticipantGame))
        .thenReturn(acceptParticipantGame);

    // when
    participantGameService.validAcceptParticipant(request, createdUser);

    // Then
    assertEquals(expectedAcceptParticipantGame, acceptParticipantGame);
  }

  @Test
  @DisplayName("경기 지원자 수락 실패 : 경기 개설자 참가 정보를 조회 했을때")
  void testAcceptParticipantFailIfGetGameCreatorParticipantGame() {
    // Given
    AcceptParticipantDto.Request request = AcceptParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    expectedApplyParticipantGame = ParticipantGameEntity.builder()
        .id(2L)
        .status(APPLY)
        .user(createdUser)
        .build();

    

    // 경기 참가 정보 조회
    getParticipantGame(request.getParticipantId(), APPLY,
        expectedApplyParticipantGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.validAcceptParticipant(request, createdUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_UPDATE_CREATOR, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 지원자 수락 실패 : 로그인 한 유저가 경기 개설자가 아닐때")
  void testAcceptParticipantFailIfNotGameCreator() {
    // Given
    AcceptParticipantDto.Request request = AcceptParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    expectedApplyParticipantGame = ParticipantGameEntity.builder()
        .id(2L)
        .status(APPLY)
        .user(applyUser)
        .game(expectedOtherCreatedGame)
        .build();

    

    // 경기 참가 정보 조회
    getParticipantGame(request.getParticipantId(), APPLY,
        expectedApplyParticipantGame);

    // 경기 조회
    getGame(expectedApplyParticipantGame.getGame().getId(), expectedOtherCreatedGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.validAcceptParticipant(request, createdUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_GAME_CREATOR, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 지원자 수락 실패 : 경기가 이미 시작 함")
  void testAcceptParticipantFailIfGameAlreadyStarted() {
    // Given
    AcceptParticipantDto.Request request = AcceptParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    expectedCreatedGame = GameEntity.builder()
        .id(1L)
        .startDateTime(LocalDateTime.now().minusHours(1))
        .user(createdUser)
        .build();

    // 경기 참가 조회
    getParticipantGame(request.getParticipantId(), APPLY,
        expectedApplyParticipantGame);

    // 경기 조회
    getGame(expectedApplyParticipantGame.getGame().getId(), expectedCreatedGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.validAcceptParticipant(request, createdUser);
    });

    // Then
    assertEquals(ErrorCode.ALREADY_GAME_START, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 지원자 수락 실패 : 해당 경기에 참가자가 다 참")
  void testAcceptParticipantFailIfGameAlreadyFull() {
    // Given
    AcceptParticipantDto.Request request = AcceptParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    // 경기 참가 조회
    getParticipantGame(request.getParticipantId(), APPLY,
        expectedApplyParticipantGame);

    // 경기 조회
    getGame(expectedApplyParticipantGame.getGame().getId(), expectedCreatedGame);

    // 경기에 참가자가 10명 있다고 가정
    countsParticipantGame(ACCEPT, expectedCreatedGame.getId(), 10);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.validAcceptParticipant(request, createdUser);
    });

    // Then
    assertEquals(ErrorCode.FULL_PARTICIPANT, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 지원자 거절 성공")
  void testRejectParticipantSuccess() {
    // Given
    RejectParticipantDto.Request request = RejectParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    ParticipantGameEntity expectedRejectParticipantGame =
        ParticipantGameEntity.builder()
            .id(2L)
            .status(REJECT)
            .createdDateTime(fixedCreatedDateTime)
            .rejectedDateTime(fixedRejectedDateTime)
            .game(expectedCreatedGame)
            .user(applyUser)
            .build();

    Instant fixedInstant = fixedRejectedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    ParticipantGameEntity rejectParticipantGame =
        new ParticipantGameEntity().setReject
            (expectedApplyParticipantGame, clock);

    // 경기 참가 조회
    getParticipantGame(request.getParticipantId(), APPLY,
        expectedApplyParticipantGame);

    // 경기 조회
    getGame(expectedApplyParticipantGame.getGame().getId(), expectedCreatedGame);

    when(participantGameRepository.save(rejectParticipantGame))
        .thenReturn(rejectParticipantGame);

    // when
    participantGameService.validRejectParticipant(request, createdUser);

    // Then
    assertEquals(expectedRejectParticipantGame, rejectParticipantGame);
  }

  @Test
  @DisplayName("경기 지원자 거절 실패 : 경기 개설자 참가 정보 조회 할때")
  void testRejectParticipantFailIfGetGameCreatorParticipantGame() {
    // Given
    RejectParticipantDto.Request request = RejectParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    expectedApplyParticipantGame = ParticipantGameEntity.builder()
        .id(2L)
        .status(APPLY)
        .user(createdUser)
        .build();

    // 경기 참가 조회
    getParticipantGame(request.getParticipantId(), APPLY,
        expectedApplyParticipantGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.validRejectParticipant(request, createdUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_UPDATE_CREATOR, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 지원자 거절 실패 : 로그인 한 유저가 경기 개설자가 아닐때")
  void testRejectParticipantFailIfNotGameCreator() {
    // Given
    RejectParticipantDto.Request request = RejectParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    expectedApplyParticipantGame = ParticipantGameEntity.builder()
        .id(2L)
        .status(APPLY)
        .user(applyUser)
        .game(expectedOtherCreatedGame)
        .build();

    // 경기 참가 조회
    getParticipantGame(request.getParticipantId(), APPLY,
        expectedApplyParticipantGame);

    // 경기 조회
    getGame(expectedApplyParticipantGame.getGame().getId(), expectedOtherCreatedGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.validRejectParticipant(request, createdUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_GAME_CREATOR, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 참가자 강퇴 성공")
  void testKickoutParticipantSuccess() {
    // Given
    KickoutParticipantDto.Request request = KickoutParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    Instant fixedInstant = fixedKickoutedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    ParticipantGameEntity expectedKickoutParticipantGame =
        ParticipantGameEntity.builder()
        .id(2L)
        .status(KICKOUT)
        .createdDateTime(fixedCreatedDateTime)
        .acceptedDateTime(fixedAcceptedDateTime)
        .kickoutDateTime(fixedKickoutedDateTime)
        .game(expectedCreatedGame)
        .user(applyUser)
        .build();

    ParticipantGameEntity kickoutParticipantGame =
        new ParticipantGameEntity().setKickout
            (expectedAcceptParticipantGame, clock);

    // 경기 참가 조회
    getParticipantGame(request.getParticipantId(), ACCEPT,
        expectedAcceptParticipantGame);

    // 경기 조회
    getGame(expectedAcceptParticipantGame.getGame().getId(), expectedCreatedGame);

    when(participantGameRepository.save(kickoutParticipantGame))
        .thenReturn(kickoutParticipantGame);

    // when
    participantGameService.validKickoutParticipant(request, createdUser);

    // Then
    assertEquals(expectedKickoutParticipantGame, kickoutParticipantGame);
  }

  @Test
  @DisplayName("경기 지원자 강퇴 실패 : 경기 개설자 참가 정보 조회 할때")
  void testKickoutParticipantFailIfGetGameCreatorParticipantGame() {
    // Given
    KickoutParticipantDto.Request request = KickoutParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    expectedApplyParticipantGame = ParticipantGameEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .user(createdUser)
        .build();

    // 경기 참가 조회
    getParticipantGame(request.getParticipantId(), ACCEPT,
        expectedApplyParticipantGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.validKickoutParticipant(request, createdUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_UPDATE_CREATOR, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 지원자 강퇴 실패 : 로그인 한 유저가 경기 개설자가 아닐때")
  void testKickoutParticipantFailIfNotGameCreator() {
    // Given
    KickoutParticipantDto.Request request = KickoutParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    expectedApplyParticipantGame = ParticipantGameEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .user(applyUser)
        .game(expectedOtherCreatedGame)
        .build();

    // 경기 참가 조회
    getParticipantGame(request.getParticipantId(), ACCEPT,
        expectedApplyParticipantGame);

    // 경기 조회
    getGame(expectedApplyParticipantGame.getGame().getId(), expectedOtherCreatedGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.validKickoutParticipant(request, createdUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_GAME_CREATOR, exception.getErrorCode());
  }


  @Test
  @DisplayName("경기 참가자 강퇴 실패 : 경기가 이미 시작 함")
  void testKickoutParticipantFailIfGameAlreadyStarted() {
    // Given
    KickoutParticipantDto.Request request = KickoutParticipantDto.Request.builder()
        .participantId(2L)
        .build();

    expectedCreatedGame = GameEntity.builder()
        .id(1L)
        .startDateTime(LocalDateTime.now().minusHours(1))
        .user(createdUser)
        .build();

    // 경기 참가 조회
    getParticipantGame(request.getParticipantId(), ACCEPT,
        expectedAcceptParticipantGame);

    // 경기 조회
    getGame(expectedAcceptParticipantGame.getGame().getId(), expectedCreatedGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.validKickoutParticipant(request, createdUser);
    });

    // Then
    assertEquals(ErrorCode.ALREADY_GAME_START, exception.getErrorCode());
  }


  // 경기 조회
  private void getGame(Long gameId, GameEntity expectedCreatedGame) {
    when(gameRepository.findByIdAndDeletedDateTimeNull(gameId))
        .thenReturn(Optional.ofNullable(expectedCreatedGame));
  }

  // 경기 지원자,참가자 리스트 조회
  private void getParticipantPage(ParticipantGameStatus status,
      Long gameId, Pageable pageable, Page<ParticipantGameEntity> expectedPage) {

    when(participantGameRepository.findByStatusAndGameId(status, gameId, pageable))
        .thenReturn(expectedPage);
  }

  // 경기 지원,참가 정보 조회
  private void getParticipantGame(Long participantGameId,
      ParticipantGameStatus status,
      ParticipantGameEntity expectedParticipantGame) {

    when(participantGameRepository.findByIdAndStatus
        (participantGameId, status))
        .thenReturn(Optional.ofNullable(expectedParticipantGame));
  }


  // 경기에 참가한 인원수를 셈
  private void countsParticipantGame
  (ParticipantGameStatus status, Long gameId, int count) {
    when(participantGameRepository.countByStatusAndGameId
        (status, gameId)).thenReturn(count);
  }

}
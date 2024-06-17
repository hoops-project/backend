package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.DELETE;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.WITHDRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.chat.repository.ChatRoomRepository;
import com.zerobase.hoops.entity.ChatRoomEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.ParticipantUser;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateRequest;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
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
class GameServiceTest {

  @InjectMocks
  private GameService gameService;

  @Mock
  private ParticipantGameRepository participantGameRepository;

  @Mock
  private GameRepository gameRepository;

  @Mock
  private InviteRepository inviteRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Spy
  private Clock clock;

  private UserEntity requestUser;

  private UserEntity otherUser;

  private GameEntity expectedCreatedGame;

  private GameEntity expectedUpdatedGame;

  private GameEntity expectedOtherCreatedGame;

  private ParticipantGameEntity expectedCreatorParticipantGame;

  private ChatRoomEntity expectedCreatedChatRoom;

  private LocalDateTime fixedStartDateTime;
  private LocalDateTime fixedCreatedDateTime;
  private LocalDateTime fixedAcceptedDateTime;


  @BeforeEach
  void setUp() {
    fixedStartDateTime = LocalDateTime.now().plusHours(1L);
    fixedCreatedDateTime = LocalDateTime
        .of(2024, 6, 9, 0, 0, 0);
    fixedAcceptedDateTime = LocalDateTime
        .of(2024, 6, 9, 1, 0, 0);
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
    otherUser = UserEntity.builder()
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
    expectedCreatedGame = GameEntity.builder()
        .id(1L)
        .title("테스트제목")
        .content("테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .createdDateTime(fixedCreatedDateTime)
        .startDateTime(fixedStartDateTime)
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .placeName("서울 농구장")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .cityName(CityName.SEOUL)
        .user(requestUser)
        .build();
    expectedUpdatedGame = GameEntity.builder()
        .id(1L)
        .title("수정테스트제목")
        .content("수정테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .createdDateTime(fixedCreatedDateTime)
        .startDateTime(fixedStartDateTime)
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .placeName("서울 농구장")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .cityName(CityName.SEOUL)
        .user(requestUser)
        .build();
    expectedOtherCreatedGame = GameEntity.builder()
        .id(2L)
        .title("테스트제목")
        .content("테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .createdDateTime(fixedCreatedDateTime)
        .startDateTime(fixedStartDateTime)
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .placeName("서울 농구장")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .cityName(CityName.SEOUL)
        .user(otherUser)
        .build();
    expectedCreatorParticipantGame = ParticipantGameEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .createdDateTime(fixedCreatedDateTime)
        .acceptedDateTime(fixedAcceptedDateTime)
        .game(expectedCreatedGame)
        .user(requestUser)
        .build();
    expectedCreatedChatRoom = ChatRoomEntity.builder()
        .id(1L)
        .gameEntity(expectedCreatedGame)
        .build();

  }

  @Test
  @DisplayName("경기 생성 성공")
  public void testCreateGameSuccess() {
    // Given
    CreateRequest createRequest = CreateRequest.builder()
        .title("테스트제목")
        .content("테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(fixedStartDateTime)
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .placeName("서울 농구장")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .build();

    Instant fixedInstant = fixedAcceptedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    GameEntity game = CreateRequest.toEntity(createRequest, requestUser);

    ParticipantGameEntity participantGame =
        ParticipantGameEntity.toGameCreatorEntity(game, requestUser, clock);

    ChatRoomEntity createdChatRoom = ChatRoomEntity.builder()
        .gameEntity(game)
        .build();

    // 이미 예정된 게임이 없는 상황을 가정
    checkGame(createRequest.getStartDateTime(), createRequest.getAddress(),
        false);

    when(gameRepository.save(game)).thenReturn(expectedCreatedGame);

    when(participantGameRepository.save(participantGame)).thenAnswer(invocation -> {
      ParticipantGameEntity entity = invocation.getArgument(0);
      entity.setId(1L); // id 할당
      entity.setCreatedDateTime(fixedCreatedDateTime);
      return entity;
    });

    when(chatRoomRepository.save(createdChatRoom)).thenAnswer(invocation -> {
      ChatRoomEntity entity = invocation.getArgument(0);
      entity.setId(1L); // id 할당
      return entity;
    });

    // when
    gameService.validCreateGame(createRequest, requestUser);

    // Then

    // ArgumentCaptor를 사용하여 저장된 엔티티를 캡처합니다.
    ArgumentCaptor<GameEntity> gameEntityCaptor
        = ArgumentCaptor.forClass(GameEntity.class);
    ArgumentCaptor<ParticipantGameEntity> participantGameEntityCaptor
        = ArgumentCaptor.forClass(ParticipantGameEntity.class);
    ArgumentCaptor<ChatRoomEntity> chatRoomEntityCaptor
        = ArgumentCaptor.forClass(ChatRoomEntity.class);

    verify(gameRepository).save(gameEntityCaptor.capture());
    verify(participantGameRepository).save(participantGameEntityCaptor.capture());
    verify(chatRoomRepository).save(chatRoomEntityCaptor.capture());

    // 저장된 엔티티 캡처
    GameEntity savedGame = gameEntityCaptor.getValue();
    ParticipantGameEntity savedParticipantGame =
        participantGameEntityCaptor.getValue();
    ChatRoomEntity savedChatRoom = chatRoomEntityCaptor.getValue();

    savedGame.setId(1L);
    savedGame.setCreatedDateTime(fixedCreatedDateTime);

    assertEquals(expectedCreatedGame, savedGame);
    assertEquals(expectedCreatorParticipantGame, savedParticipantGame);
    assertEquals(expectedCreatedChatRoom, savedChatRoom);
  }

  @Test
  @DisplayName("경기 생성 실패: 해당 시간 범위에 이미 경기가 존재")
  public void testCreateGameFailIfGameExistsInTimeRange() {
    // given
    CreateRequest createRequest = CreateRequest.builder()
        .startDateTime(LocalDateTime.now().plusHours(1))
        .address("테스트 주소")
        .build();

    

    //해당 시간 범위에 이미 경기가 존재한다고 가정
    checkGame(createRequest.getStartDateTime(), createRequest.getAddress(),
        true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validCreateGame(createRequest, requestUser);
    });

    // then
    assertEquals(ErrorCode.ALREADY_GAME_CREATED, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 생성 실패: 경기 시작 시간은 현재 시간으로부터 최소 30분 이후여야 합니다.")
  public void testCreateGameFailIfStartTimeLessThan30MinutesAhead() {
    // Given
    CreateRequest createRequest = CreateRequest.builder()
        .startDateTime(LocalDateTime.now().plusMinutes(15))
        .address("테스트 주소")
        .build();

    

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    checkGame(createRequest.getStartDateTime(), createRequest.getAddress(),
        false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validCreateGame(createRequest, requestUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_AFTER_THIRTY_MINUTE, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 생성 실패: 3:3 경기에 6~9명 설정을 하지 않았을때")
  public void testCreateGameFailWhenThreeOnThreeHeadCountNotInRange() {
    // given
    CreateRequest createRequest = CreateRequest.builder()
        .startDateTime(LocalDateTime.now().plusHours(1))
        .headCount(5L)
        .matchFormat(MatchFormat.THREEONTHREE)
        .address("테스트 주소")
        .build();

    

    //해당 시간 범위에 이미 경기가 없다고 가정
    checkGame(createRequest.getStartDateTime(), createRequest.getAddress(),
        false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validCreateGame(createRequest, requestUser);
    });

    // then
    assertEquals(ErrorCode.NOT_CREATE_THREEONTHREEE, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 생성 실패: 5:5 경기에 10~15명 설정을 하지 않았을때")
  public void testCreateGameFailWhenFiveOnFiveHeadCountNotInRange() {
    // given
    CreateRequest createRequest = CreateRequest.builder()
        .startDateTime(LocalDateTime.now().plusHours(1))
        .headCount(5L)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .address("테스트 주소")
        .build();
    

    //해당 시간 범위에 이미 경기가 없다고 가정
    checkGame(createRequest.getStartDateTime(), createRequest.getAddress(),
        false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validCreateGame(createRequest, requestUser);
    });

    // then
    assertEquals(ErrorCode.NOT_CREATE_FIVEONFIVE, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 상세 조회 성공")
  void getGameDetailSuccess() {
    // Given
    Long gameId = 1L;

    List<ParticipantGameEntity> participantGameEntityList =
        List.of(expectedCreatorParticipantGame);

    List<ParticipantUser> participantUserList =
        participantGameEntityList.stream().map(ParticipantUser::toDto).toList();

    DetailResponse expectedDetailResponse = DetailResponse.toDto
        (expectedCreatedGame, participantUserList);

    when(gameRepository.findByIdAndDeletedDateTimeNull(gameId))
        .thenReturn(Optional.of(expectedCreatedGame));

    // 게임에 참가한 사람이 게임 개설자 밖에 없다고 가정
    when(participantGameRepository
        .findByGameIdAndStatusAndDeletedDateTimeNull
            (expectedCreatedGame.getId(), ACCEPT))
        .thenReturn(participantGameEntityList);

    // when
    DetailResponse detailResponse = gameService.validGetGameDetail(gameId);

    // Then
    assertEquals(expectedDetailResponse, detailResponse);
  }

  @Test
  @DisplayName("경기 수정 성공")
  void updateGameSuccess() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .title("수정테스트제목")
        .content("수정테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(fixedStartDateTime)
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .placeName("서울 농구장")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .build();

    GameEntity game = UpdateRequest.toEntity(updateRequest, expectedCreatedGame);
    
    // 경기 조회
    getGame(updateRequest.getGameId(), expectedCreatedGame);

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    checkGameNotSelf(updateRequest.getStartDateTime(),
        updateRequest.getAddress(), expectedCreatedGame.getId(), false);

    // 현재 경기에 참가한 인원수가 개설자 한명만 있다고 가정
    countsParticipantGame(expectedCreatedGame.getId(), 1);

    // 경기 수정
    when(gameRepository.save(game)).thenReturn(game);

    // when
    gameService.validUpdateGame(updateRequest, requestUser);

    // Then
    assertEquals(expectedUpdatedGame, game);
  }

  @Test
  @DisplayName("경기 수정 실패 : 자신이 만든 경기가 아닐때")
  void updateGameFailIfNotMyCreatedGame() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(2L)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();
    
    // 경기 조회
    getGame(updateRequest.getGameId(), expectedOtherCreatedGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validUpdateGame(updateRequest, requestUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_GAME_CREATOR, exception.getErrorCode());
  }


  @Test
  @DisplayName("경기 수정 실패 : 해당 시간 범위에 이미 경기가 존재")
  void updateGameFailIfGameExistsInTimeRange() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();

    // 경기 조회
    getGame(updateRequest.getGameId(), expectedCreatedGame);

    // 이미 예정된 게임이 있음.
    checkGameNotSelf(updateRequest.getStartDateTime(),
        updateRequest.getAddress(), expectedCreatedGame.getId(), true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validUpdateGame(updateRequest, requestUser);
    });

    // Then
    assertEquals(ErrorCode.ALREADY_GAME_CREATED, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 수정 실패 : 경기 시작 시간은 현재 시간으로부터 최소 30분 이후여야 합니다.")
  void updateGameFailIfStartTimeLessThan30MinutesAhead() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .startDateTime(LocalDateTime.now().plusMinutes(15))
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();

    // 경기 조회
    getGame(updateRequest.getGameId(), expectedCreatedGame);

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    checkGameNotSelf(updateRequest.getStartDateTime(),
        updateRequest.getAddress(), expectedCreatedGame.getId(), false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validUpdateGame(updateRequest, requestUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_AFTER_THIRTY_MINUTE, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 수정 실패 : 변경 하려는 인원수가 팀원 수보다 작게 설정")
  void updateGameFailWhenParticipantCountIsTooLow() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .headCount(6L)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();

    // 경기 조회
    getGame(updateRequest.getGameId(), expectedCreatedGame);

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    checkGameNotSelf(updateRequest.getStartDateTime(),
        updateRequest.getAddress(), expectedCreatedGame.getId(), false);

    // 현재 경기에 참가한 인원수가 8명 있다고 가정
    countsParticipantGame(expectedCreatedGame.getId(), 8);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validUpdateGame(updateRequest, requestUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_UPDATE_HEADCOUNT, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 수정 실패 : 팀원 중 남성이 있을 때 경기 성별을 여성으로 변경하려고 할 때")
  void updateGameFailWhenChangingGenderToFemaleWithMaleParticipants() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .headCount(10L)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .gender(Gender.FEMALEONLY)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();

    // 경기 조회
    getGame(updateRequest.getGameId(), expectedCreatedGame);

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    checkGameNotSelf(updateRequest.getStartDateTime(),
        updateRequest.getAddress(), expectedCreatedGame.getId(), false);

    // 현재 경기에 참가한 인원수가 8명 있다고 가정
    countsParticipantGame(expectedCreatedGame.getId(), 8);

    // 해당 경기에 남자가 이미 참가해 있다고 가정
    checkForParticipantByGender(updateRequest.getGameId(), GenderType.MALE,
        true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validUpdateGame(updateRequest, requestUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_UPDATE_WOMAN, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 수정 실패 : 팀원 중 여성이 있을 때 경기 성별을 남성으로 변경하려고 할 때")
  void updateGameFailWhenChangingGenderToMaleWithFemaleParticipants() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .headCount(10L)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .gender(Gender.MALEONLY)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();

    // 경기 조회
    getGame(updateRequest.getGameId(), expectedCreatedGame);

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    checkGameNotSelf(updateRequest.getStartDateTime(),
        updateRequest.getAddress(), updateRequest.getGameId(), false);

    // 현재 경기에 참가한 인원수가 8명 있다고 가정
    countsParticipantGame(updateRequest.getGameId(), 8);

    // 해당 경기에 여자가 이미 참가해 있다고 가정
    checkForParticipantByGender(updateRequest.getGameId(), GenderType.FEMALE,
        true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validUpdateGame(updateRequest, requestUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_UPDATE_MAN, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 삭제 성공 : 경기 개설자가 삭제")
  void deleteGame_successGameCreator() {
    //Given
    DeleteRequest deleteRequest = DeleteRequest.builder()
        .gameId(1L)
        .build();

    LocalDateTime fixedRequestedDateTime = LocalDateTime
        .of(2024, 6, 9, 1, 0, 0);

    LocalDateTime fixedCanceledDateTime = LocalDateTime
        .of(2024, 6, 9, 2, 0, 0);

    LocalDateTime fixedDeletedDateTime = LocalDateTime
        .of(2024, 6, 9, 2, 0, 0);

    ParticipantGameEntity expectedDeleteCreatorParticipantGame =
        ParticipantGameEntity.builder()
            .id(1L)
            .status(DELETE)
            .createdDateTime(fixedCreatedDateTime)
            .acceptedDateTime(fixedAcceptedDateTime)
            .deletedDateTime(fixedDeletedDateTime)
            .game(expectedCreatedGame)
            .user(requestUser)
            .build();

    InviteEntity requestInvite = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .requestedDateTime(fixedRequestedDateTime)
        .game(expectedCreatedGame)
        .senderUser(requestUser)
        .receiverUser(otherUser)
        .build();

    InviteEntity cancelInvite = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.CANCEL)
        .requestedDateTime(fixedRequestedDateTime)
        .canceledDateTime(fixedCanceledDateTime)
        .game(expectedCreatedGame)
        .senderUser(requestUser)
        .receiverUser(otherUser)
        .build();

    GameEntity expectedDeletedGame = GameEntity.builder()
        .id(1L)
        .title("테스트제목")
        .content("테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .createdDateTime(fixedCreatedDateTime)
        .startDateTime(fixedStartDateTime)
        .deletedDateTime(fixedDeletedDateTime)
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .placeName("서울 농구장")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .cityName(CityName.SEOUL)
        .user(requestUser)
        .build();

    Long gameId = deleteRequest.getGameId();

    List<ParticipantGameEntity> participantGameList =
        List.of(expectedCreatorParticipantGame);

    List<ParticipantGameEntity> deletedParticipantGameList = new ArrayList<>();

    List<ParticipantGameEntity> expectedDeletedParticipantGameList
        = List.of(expectedDeleteCreatorParticipantGame);

    List<InviteEntity> inviteEntityList = List.of(requestInvite);

    List<InviteEntity> canceledInviteList = new ArrayList<>();

    List<InviteEntity> expectedCanceledInviteList = List.of(cancelInvite);

    // 경기 조회
    getGame(gameId, expectedCreatedGame);

    // 경기 삭제 전에 기존에 경기에 ACCEPT, APPLY 멤버가 자기 자신만 있다고 가정
    when(participantGameRepository.findByStatusInAndGameId
        (List.of(ACCEPT, APPLY), gameId)).thenReturn(participantGameList);

    Instant fixedInstant = fixedDeletedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    participantGameList.forEach(participantGame -> {
      ParticipantGameEntity entity =
          ParticipantGameEntity.setDelete(participantGame, clock);
      deletedParticipantGameList.add(entity);
      when(participantGameRepository.save(entity)).thenReturn(entity);
    });

    // 해당 경기에 초대 신청된 것들 다 조회
    when(inviteRepository.findByInviteStatusAndGameId
        (InviteStatus.REQUEST, gameId)).thenReturn(inviteEntityList);

    inviteEntityList.forEach(invite -> {
      InviteEntity entity = InviteEntity.setCancel(invite, clock);
      canceledInviteList.add(entity);
      when(inviteRepository.save(entity)).thenReturn(entity);
    });

    fixedInstant = fixedDeletedDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    GameEntity game = DeleteRequest.toEntity(expectedCreatedGame, clock);

    when(gameRepository.save(game)).thenReturn(game);

    // when
    gameService.validDeleteGame(deleteRequest, requestUser);

    // Then
    assertEquals(expectedDeletedGame, game);
    assertEquals(expectedDeletedParticipantGameList,
        deletedParticipantGameList);
    assertEquals(expectedCanceledInviteList, canceledInviteList);
  }

  @Test
  @DisplayName("경기 삭제 성공 : 팀원이 삭제")
  void deleteGame_successGameUser() {
    //Given
    DeleteRequest deleteRequest = DeleteRequest.builder()
        .gameId(1L)
        .build();

    LocalDateTime fixedwithdrewDateTime = LocalDateTime
        .of(2024, 6, 9, 2, 0, 0);

    ParticipantGameEntity otherParticipantEntity =
        ParticipantGameEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(fixedCreatedDateTime)
        .acceptedDateTime(fixedAcceptedDateTime)
        .game(expectedCreatedGame)
        .user(otherUser)
        .build();

    ParticipantGameEntity expectedWithdrewParticipantGameEntity =
        ParticipantGameEntity.builder()
            .id(2L)
            .status(WITHDRAW)
            .createdDateTime(fixedCreatedDateTime)
            .acceptedDateTime(fixedAcceptedDateTime)
            .withdrewDateTime(fixedwithdrewDateTime)
            .game(expectedCreatedGame)
            .user(otherUser)
            .build();

    Instant fixedInstant = fixedwithdrewDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    ParticipantGameEntity withdrewPartEntity =
        ParticipantGameEntity.setWithdraw(otherParticipantEntity, clock);

    // 경기 조회
    getGame(deleteRequest.getGameId(), expectedCreatedGame);

    // 자기 자신 철회 처리
    when(participantGameRepository
        .findByStatusAndGameIdAndUserId
        (ACCEPT, expectedCreatedGame.getId(), otherUser.getId()))
        .thenReturn(Optional.of(otherParticipantEntity));

    when(participantGameRepository.save(withdrewPartEntity))
        .thenReturn(withdrewPartEntity);

    // when
    gameService.validDeleteGame(deleteRequest, otherUser);

    // Then
    assertEquals(expectedWithdrewParticipantGameEntity, withdrewPartEntity);
  }

  @Test
  @DisplayName("경기 삭제 실패 : 경기 시작 30분 전에만 삭제 가능")
  void deleteGame_fail() {
    //Given
    DeleteRequest deleteRequest = DeleteRequest.builder()
        .gameId(1L)
        .build();

    expectedCreatedGame.setStartDateTime(LocalDateTime.now().plusMinutes(15));

    // 경기 조회
    getGame(deleteRequest.getGameId(), expectedCreatedGame);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.validDeleteGame(deleteRequest, requestUser);
    });

    // Then
    assertEquals(ErrorCode.NOT_DELETE_STARTDATE, exception.getErrorCode());
  }

  // 시간 범위에 해당하는 경기가 존재하는지 체크 (경기 생성)
  private void checkGame(LocalDateTime startDateTime, String address,
      boolean flag) {

    LocalDateTime beforeDatetime = startDateTime.minusHours(1).plusSeconds(1);
    LocalDateTime afterDateTime = startDateTime.plusHours(1).minusSeconds(1);

    when(gameRepository.existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull
        (beforeDatetime, afterDateTime, address))
        .thenReturn(flag);
  }

  // 자기 자신 제외 시간 범위에 해당하는 경기가 존재하는지 체크 (경기 수정)
  private void checkGameNotSelf(LocalDateTime startDateTime, String address,
      Long gameId, boolean flag) {

    LocalDateTime beforeDatetime = startDateTime.minusHours(1).plusSeconds(1);
    LocalDateTime afterDateTime = startDateTime.plusHours(1).minusSeconds(1);

    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndIdNot
            (beforeDatetime, afterDateTime, address, gameId))
        .thenReturn(flag);
  }
  
  // 경기 조회
  private void getGame(Long gameId, GameEntity expectedCreatedGame) {
    when(gameRepository.findByIdAndDeletedDateTimeNull(gameId))
        .thenReturn(Optional.ofNullable(expectedCreatedGame));
  }

  // 경기에 참가한 인원수를 셈
  private void countsParticipantGame(Long gameId, int headCount) {
    when(participantGameRepository
        .countByStatusAndGameId(ACCEPT, gameId))
        .thenReturn(headCount);
  }

  // 특정 성별의 참가자가 있는지 체크
  private void checkForParticipantByGender(Long gameId, GenderType genderType
      , boolean flag) {

    when(participantGameRepository
        .existsByStatusAndGameIdAndUserGender
            (ACCEPT, gameId, genderType))
        .thenReturn(flag);

  }

}
package com.zerobase.hoops.gameUsers.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.MannerPointEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.dto.MannerPointDto;
import com.zerobase.hoops.gameUsers.dto.MannerPointListResponse;
import com.zerobase.hoops.gameUsers.dto.ParticipateGameDto;
import com.zerobase.hoops.gameUsers.repository.GameCheckOutRepository;
import com.zerobase.hoops.gameUsers.repository.GameUserRepository;
import com.zerobase.hoops.gameUsers.repository.MannerPointRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.GenderType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GameUserServiceTest {

  @InjectMocks
  private GameUserService gameUserService;

  @Mock
  private GameUserRepository gameUserRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MannerPointRepository mannerPointRepository;

  @Mock
  private GameCheckOutRepository gameCheckOutRepository;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  private UserEntity user;
  private UserEntity receiverUser;
  private GameEntity game;
  private GameEntity gameForManner;
  private ParticipantGameEntity participateGame;
  private ParticipantGameEntity participateGame2;
  private MannerPointDto mannerPointDto;

  @BeforeEach
  void setUp() {
    user = UserEntity.builder()
        .id(1L)
        .gender(GenderType.MALE)
        .build();

    receiverUser = UserEntity.builder()
        .id(2L)
        .gender(GenderType.MALE)
        .build();

    game = GameEntity.builder()
        .id(1L)
        .headCount(10L)
        .gender(Gender.MALEONLY)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .user(user)
        .build();

    gameForManner = GameEntity.builder()
        .id(2L)
        .headCount(10L)
        .gender(Gender.MALEONLY)
        .startDateTime(LocalDateTime.of(2023, 5, 8, 10, 0))
        .user(user)
        .build();

    participateGame = ParticipantGameEntity.builder()
        .game(game)
        .user(user)
        .status(ParticipantGameStatus.ACCEPT)
        .id(1L)
        .build();

    participateGame2 = ParticipantGameEntity.builder()
        .game(game)
        .user(receiverUser)
        .status(ParticipantGameStatus.ACCEPT)
        .id(2L)
        .build();

    mannerPointDto = MannerPointDto.builder()
        .receiverId(receiverUser.getId())
        .gameId(gameForManner.getId())
        .point(5)
        .build();

  }

  @DisplayName("매너점수 평가하기 성공 - 1")
  @Test
  void testSaveMannerPoint() {
    // Given
    given(jwtTokenExtract.currentUser()).willReturn(user);
    given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
    given(userRepository.findById(receiverUser.getId())).willReturn(Optional.of(receiverUser));
    given(gameUserRepository.findById(gameForManner.getId())).willReturn(Optional.of(gameForManner));
    given(gameCheckOutRepository.findById(participateGame.getId())).willReturn(Optional.of(participateGame));
    given(gameCheckOutRepository.findById(participateGame2.getId())).willReturn(Optional.of(participateGame2));
    given(gameUserRepository.findByIdAndStartDateTimeBefore(eq(gameForManner.getId()), any(LocalDateTime.class)))
        .willReturn(Optional.of(gameForManner));
    given(mannerPointRepository.existsByUser_IdAndReceiver_IdAndGame_Id(
        user.getId(), receiverUser.getId(), gameForManner.getId())).willReturn(false);

    // When
    gameUserService.saveMannerPoint(mannerPointDto);

    // Then
    ArgumentCaptor<MannerPointEntity> mannerPointEntityCaptor = ArgumentCaptor.forClass(
        MannerPointEntity.class);
    verify(mannerPointRepository).save(mannerPointEntityCaptor.capture());

  }

  @DisplayName("매너점수 평가 리스트 갖고 오기 - 본인제외")
  @Test
  void testGetMannerPoint_Success() {
    // Give
    String gameId = "1";

    // When
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        Optional.of(user));

    when(gameUserRepository.findByIdAndStartDateTimeBefore(
        eq(Long.valueOf(gameId)),
        any(LocalDateTime.class)))
        .thenAnswer(invocation -> Optional.of(
            Collections.singletonList(participateGame)));

    when(
        gameCheckOutRepository.existsByGame_IdAndUser_IdAndStatus(
            eq(Long.valueOf(gameId)), eq(user.getId()),
            eq(ParticipantGameStatus.ACCEPT)))
        .thenReturn(true);
    when(gameCheckOutRepository.findByStatusAndGame_Id(
        eq(ParticipantGameStatus.ACCEPT),
        eq(Long.valueOf(gameId))))
        .thenAnswer(invocation -> Optional.of(
            Collections.singletonList(participateGame)));

    List<MannerPointListResponse> result = gameUserService.getMannerPoint(
        "1");

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(0, result.size());
  }

  @DisplayName("매너점수 평가하기 - 실패 (평가하는 사람 Not Found)")
  @Test
  void testSaveMannerPointUserNotFound() {
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        Optional.empty());

    assertThrows(CustomException.class,
        () -> gameUserService.saveMannerPoint(mannerPointDto));
  }

  @DisplayName("매너점수 평가하기 - 실패 (평가당하는 사람 Not Found)")
  @Test
  void testSaveMannerPointReceiverNotFound() {
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        Optional.of(user));
    when(userRepository.findById(receiverUser.getId())).thenReturn(
        Optional.empty());

    assertThrows(CustomException.class,
        () -> gameUserService.saveMannerPoint(mannerPointDto));
  }

  @DisplayName("매너점수 평가하기 - 실패 (게임 Not Found)")
  @Test
  void testSaveMannerPointGameNotFound() {
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        Optional.of(user));
    when(userRepository.findById(receiverUser.getId())).thenReturn(
        Optional.of(receiverUser));
    when(gameUserRepository.findByIdAndStartDateTimeBefore(
        game.getId(), LocalDateTime.now())).thenReturn(
        Optional.empty());

    assertThrows(CustomException.class,
        () -> gameUserService.saveMannerPoint(mannerPointDto));
  }


  @DisplayName("매너점수 평가하기 - 실패 (이미 평가함)")
  @Test
  void testSaveMannerPointExistRate() {
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        Optional.of(user));
    when(userRepository.findById(receiverUser.getId())).thenReturn(
        Optional.of(receiverUser));
    when(gameUserRepository.findByIdAndStartDateTimeBefore(
        game.getId(), LocalDateTime.now())).thenReturn(
        Optional.of(game));
    when(
        mannerPointRepository.existsByUser_IdAndReceiver_IdAndGame_Id(
            user.getId(), receiverUser.getId(),
            game.getId())).thenReturn(true);

    assertThrows(CustomException.class,
        () -> gameUserService.saveMannerPoint(mannerPointDto));
  }



  @DisplayName("매너점수 평가 리스트 갖고오기 실패 (유저없음)")
  @Test
  void testGetMannerPointUserNotFound() {
    // Give When
    String gameId = "1";
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        Optional.empty());

    // Then
    Assertions.assertThrows(CustomException.class,
        () -> gameUserService.getMannerPoint(gameId));
  }

  @DisplayName("매너점수 평가리스트 갖고오기 실패 (게임 없음) ")
  @Test
  void testGetMannerPointGameNotFound() {
    // Give When
    String gameId = "1";
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        Optional.of(user));
    when(gameUserRepository.findByIdAndStartDateTimeBefore(
        game.getId(), LocalDateTime.now())).thenReturn(
        Optional.empty());

    // Then
    Assertions.assertThrows(CustomException.class,
        () -> gameUserService.getMannerPoint(gameId));
  }



  @Test
  @DisplayName("현제 참여중인 게임 리스트 불러오기 성공")
  void testMyCurrentGameList() {
    // Given
    List<ParticipantGameEntity> userGameList = new ArrayList<>();
    GameEntity futureGame = new GameEntity();
    futureGame.setStartDateTime(LocalDateTime.now().plusDays(1));
    futureGame.setUser(user);
    ParticipantGameEntity participantGameEntity = new ParticipantGameEntity();
    participantGameEntity.setGame(futureGame);
    userGameList.add(participantGameEntity);

    // When
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        java.util.Optional.of(user));
    when(gameCheckOutRepository.findByUser_IdAndStatus(
        user.getId(), ParticipantGameStatus.ACCEPT))
        .thenReturn(java.util.Optional.of(userGameList));
    Page<GameSearchResponse> result = gameUserService.myCurrentGameList(
        1,
        1);
    List<GameSearchResponse> result2 = result.getContent();

    // Then
    assertEquals(1, result2.size());
  }

  @Test
  @DisplayName("현제 참여중인 게임 리스트 불러오기 성공2")
  void testMyCurrentGameList2() {
    // Given
    UserEntity user = new UserEntity();
    user.setId(1L);

    JwtTokenExtract jwtTokenExtractMock = mock(JwtTokenExtract.class);
    UserRepository userRepositoryMock = mock(UserRepository.class);
    GameCheckOutRepository gameCheckOutRepositoryMock = mock(
        GameCheckOutRepository.class);
    GameUserRepository gameUserRepositoryMock = mock(
        GameUserRepository.class);
    MannerPointRepository mannerPointRepositoryMock = mock(
        MannerPointRepository.class);

    List<ParticipantGameEntity> userGameList = new ArrayList<>();
    GameEntity futureGame = new GameEntity();
    futureGame.setStartDateTime(LocalDateTime.now().plusDays(1));
    futureGame.setUser(user);
    ParticipantGameEntity participantGameEntity = new ParticipantGameEntity();
    participantGameEntity.setGame(futureGame);
    userGameList.add(participantGameEntity);

    // When
    when(jwtTokenExtractMock.currentUser()).thenReturn(user);
    when(userRepositoryMock.findById(user.getId())).thenReturn(
        Optional.of(user));
    when(gameCheckOutRepositoryMock.findByUser_IdAndStatus(
        user.getId(), ParticipantGameStatus.ACCEPT))
        .thenReturn(Optional.of(userGameList));

    GameUserService gameUserService = new GameUserService(
        gameCheckOutRepositoryMock, gameUserRepositoryMock,
        mannerPointRepositoryMock, userRepositoryMock,
        jwtTokenExtractMock);
    int pageSize = 10;
    Page<GameSearchResponse> resultPage = gameUserService.myCurrentGameList(
        1,
        pageSize);
    List<GameSearchResponse> result = resultPage.getContent();

    // Then
    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("과거 게임 리스트 불러오기 성공")
  void testMyLastGameList() {
    // Given
    List<ParticipantGameEntity> userGameList = new ArrayList<>();
    GameEntity pastGame = new GameEntity();
    pastGame.setStartDateTime(LocalDateTime.now().minusDays(1));
    pastGame.setUser(user);
    ParticipantGameEntity participantGameEntity = new ParticipantGameEntity();
    participantGameEntity.setGame(pastGame);
    userGameList.add(participantGameEntity);

    // When
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        java.util.Optional.of(user));
    when(gameCheckOutRepository.findByUser_IdAndStatus(
        user.getId(),
        ParticipantGameStatus.ACCEPT))
        .thenReturn(java.util.Optional.of(userGameList));
    Page<GameSearchResponse> result = gameUserService.myLastGameList(1,
        1);
    List<GameSearchResponse> result2 = result.getContent();
    // Then
    assertEquals(1, result2.size());
  }

  @Test
  @DisplayName("게임 참가 성공")
  void participateInGame_validGame_shouldSucceed() {
    // Given
    ParticipantGameEntity participantGameEntity = ParticipantGameEntity.builder()
        .id(1L)
        .status(ParticipantGameStatus.APPLY)
        .game(game)
        .user(user)
        .build();

    // When
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        Optional.of(user));
    when(gameUserRepository.findById(game.getId())).thenReturn(
        Optional.of(game));
    when(gameCheckOutRepository.countByStatusAndGameId(
        ParticipantGameStatus.ACCEPT, game.getId())).thenReturn(0);
    when(gameCheckOutRepository.save(
        any(ParticipantGameEntity.class))).thenReturn(
        participantGameEntity);

    ParticipateGameDto result = gameUserService.participateInGame(
        game.getId());

    // Then
    assertEquals(ParticipantGameStatus.APPLY, result.getStatus());
    assertEquals(participantGameEntity.getId(),
        result.getParticipantId());
    assertEquals(game.getId(), result.getGameEntity().getId());
    assertEquals(user.getId(), result.getUserEntity().getId());
  }

  @Test
  @DisplayName("게임 참가 실패 - 게임 찾을 수 없음")
  void participateInGame_gameNotFound_shouldThrowException() {
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(
        Optional.of(user));
    when(gameUserRepository.findById(game.getId())).thenReturn(
        Optional.empty());

    assertThrows(CustomException.class,
        () -> gameUserService.participateInGame(game.getId()));
  }

  @Test
  @DisplayName("GameUserService 필터 테스트 1")
  void findFilteredGames_whenAllFiltersAreNull_shouldReturnAllGames() {
    // Given
    UserEntity userEntity = new UserEntity();
    userEntity.setId(1L);

    GameEntity gameEntity1 = new GameEntity();
    gameEntity1.setUser(userEntity);
    GameEntity gameEntity2 = new GameEntity();
    gameEntity2.setUser(userEntity);

    List<GameEntity> gameEntities = Arrays.asList(gameEntity1,
        gameEntity2);

    // When
    when(
        gameUserRepository.findAll(any(Specification.class))).thenReturn(
        gameEntities);

    Page<GameSearchResponse> result = gameUserService.findFilteredGames(
        null, null, null, null, null
        , 1, 2);
    List<GameSearchResponse> result2 = result.getContent();
    // Then
    assertEquals(gameEntities.size(), result2.size());
  }

  @Test
  @DisplayName("GameUserService 필터 테스트 2")
  void findFilteredGames_whenSomeFiltersAreProvided_shouldReturnFilteredGames
      () {
    // Given
    LocalDate date = LocalDate.now();
    CityName cityName = CityName.SEOUL;
    FieldStatus fieldStatus = FieldStatus.INDOOR;
    Gender gender = Gender.ALL;
    MatchFormat matchFormat = MatchFormat.FIVEONFIVE;

    UserEntity userEntity = new UserEntity();
    userEntity.setId(1L);

    GameEntity gameEntity1 = new GameEntity();
    gameEntity1.setUser(userEntity);
    GameEntity gameEntity2 = new GameEntity();
    gameEntity2.setUser(userEntity);

    List<GameEntity> gameEntities = Arrays.asList(gameEntity1,
        gameEntity2);

    // When
    when(
        gameUserRepository.findAll(any(Specification.class))).thenReturn(
        gameEntities);

    Page<GameSearchResponse> result = gameUserService.findFilteredGames(
        date, cityName, fieldStatus, gender, matchFormat, 1, 2);
    List<GameSearchResponse> result2 = result.getContent();

    // Then
    assertEquals(gameEntities.size(), result2.size());
  }

  @Test
  @DisplayName("GameUserService 주소 찾기 테스트")
  void searchAddress_shouldReturnUpcomingGamesForGivenAddress() {
    // Given
    String address = "123 Example St";
    UserEntity userEntity = new UserEntity();
    userEntity.setId(1L);

    GameEntity gameEntity1 = GameEntity.builder()
        .user(userEntity)
        .id(1L)
        .address(address)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .build();

    GameEntity gameEntity2 = GameEntity.builder()
        .user(userEntity)
        .id(2L)
        .address(address)
        .startDateTime(LocalDateTime.now().plusDays(2))
        .build();

    List<GameEntity> upcomingGames = Arrays.asList(
        gameEntity1,
        gameEntity2
    );

    // When
    when(
        gameUserRepository.findByAddressContainingIgnoreCaseAndStartDateTimeAfterOrderByStartDateTimeAsc(
            eq(address), any(LocalDateTime.class))).thenReturn(
        upcomingGames);

    Page<GameSearchResponse> result = gameUserService.searchAddress(
        address, 1,2);
    List<GameSearchResponse> result2 = result.getContent();

    // Then
    assertEquals(upcomingGames.size(), result2.size());
  }
}
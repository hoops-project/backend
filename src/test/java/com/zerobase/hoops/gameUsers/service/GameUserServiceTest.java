package com.zerobase.hoops.gameUsers.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.dto.ParticipateGameDto;
import com.zerobase.hoops.gameUsers.repository.GameCheckOutRepository;
import com.zerobase.hoops.gameUsers.repository.GameUserRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.GenderType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
  private GameCheckOutRepository gameCheckOutRepository;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  private UserEntity user;
  private GameEntity game;


  @BeforeEach
  void setUp() {
    user = UserEntity.builder()
        .userId(1L)
        .gender(GenderType.MALE)
        .build();

    game = GameEntity.builder()
        .gameId(1L)
        .headCount(10L)
        .gender(Gender.MALEONLY)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .build();
  }

  @Test
  @DisplayName("게임 참가 성공")
  void participateInGame_validGame_shouldSucceed() {
    // Given
    ParticipantGameEntity participantGameEntity = ParticipantGameEntity.builder()
        .participantId(1L)
        .status(ParticipantGameStatus.APPLY)
        .gameEntity(game)
        .userEntity(user)
        .build();

    // When
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getUserId())).thenReturn(
        Optional.of(user));
    when(gameUserRepository.findById(game.getGameId())).thenReturn(
        Optional.of(game));
    when(gameCheckOutRepository.countByStatusAndGameEntityGameId(
        ParticipantGameStatus.ACCEPT, game.getGameId())).thenReturn(0);
    when(gameCheckOutRepository.save(
        any(ParticipantGameEntity.class))).thenReturn(
        participantGameEntity);

    ParticipateGameDto result = gameUserService.participateInGame(
        game.getGameId());

    // Then
    assertEquals(ParticipantGameStatus.APPLY, result.getStatus());
    assertEquals(participantGameEntity.getParticipantId(),
        result.getParticipantId());
    assertEquals(game.getGameId(), result.getGameEntity().getGameId());
    assertEquals(user.getUserId(), result.getUserEntity().getUserId());
  }

  @Test
  @DisplayName("게임 참가 실패 - 게임 찾을 수 없음")
  void participateInGame_gameNotFound_shouldThrowException() {
    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(user.getUserId())).thenReturn(
        Optional.of(user));
    when(gameUserRepository.findById(game.getGameId())).thenReturn(
        Optional.empty());

    assertThrows(CustomException.class,
        () -> gameUserService.participateInGame(game.getGameId()));
  }


  @DisplayName("GameUserService 필터 테스트 1")
  @Test
  void findFilteredGames_whenAllFiltersAreNull_shouldReturnAllGames() {
    List<GameEntity> gameEntities = Arrays.asList(new GameEntity(),
        new GameEntity());
    when(gameUserRepository.findAll(any(Specification.class))).thenReturn(
        gameEntities);

    List<GameSearchResponse> result = gameUserService.findFilteredGames(
        null,
        null, null, null, null);

    assertEquals(gameEntities.size(), result.size());
  }

  @DisplayName("GameUserService 필터 테스트 2")
  @Test
  void findFilteredGames_whenSomeFiltersAreProvided_shouldReturnFilteredGames() {
    LocalDate date = LocalDate.now();
    CityName cityName = CityName.SEOUL;
    FieldStatus fieldStatus = FieldStatus.INDOOR;
    Gender gender = Gender.ALL;
    MatchFormat matchFormat = MatchFormat.FIVEONFIVE;

    List<GameEntity> gameEntities = Arrays.asList(new GameEntity(),
        new GameEntity());
    when(gameUserRepository.findAll(any(Specification.class))).thenReturn(
        gameEntities);

    List<GameSearchResponse> result = gameUserService.findFilteredGames(
        date,
        cityName, fieldStatus, gender, matchFormat);

    assertEquals(gameEntities.size(), result.size());
  }

  @DisplayName("GameUserService 주소 찾기 테스트")
  @Test
  void searchAddress_shouldReturnUpcomingGamesForGivenAddress() {
    // Given
    String address = "123 Example St";
    List<GameEntity> upcomingGames = Arrays.asList(
        GameEntity.builder().gameId(1L).address(address)
            .startDateTime(LocalDateTime.now().plusHours(1)).build(),
        GameEntity.builder().gameId(2L).address(address)
            .startDateTime(LocalDateTime.now().plusDays(2)).build()
    );
    when(
        gameUserRepository.findByAddressContainingIgnoreCaseAndStartDateTimeAfterOrderByStartDateTimeAsc(
            eq(address), any(LocalDateTime.class))).thenReturn(
        upcomingGames);

    // When
    List<GameSearchResponse> result = gameUserService.searchAddress(
        address);

    // Then
    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).getGameId());
    assertEquals(2L, result.get(1).getGameId());
  }
}
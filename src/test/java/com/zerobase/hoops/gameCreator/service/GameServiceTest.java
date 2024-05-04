package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateRequest;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import io.jsonwebtoken.Jwts;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  @InjectMocks
  private GameService gameService;

  @Mock
  private TokenProvider tokenProvider;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ParticipantGameRepository participantGameRepository;

  @Mock
  private GameRepository gameRepository;

  private UserEntity requestUser;
  private UserEntity creatorUser;

  private GameEntity createGameEntity;

  private GameEntity updateGameEntity;

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
    creatorUser = UserEntity.builder()
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
        .roles(new ArrayList<>(List.of("ROLE_USER", "ROLE_CREATOR")))
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    createGameEntity = GameEntity.builder()
        .gameId(1L)
        .title("테스트제목")
        .content("테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .cityName(CityName.SEOUL)
        .userEntity(requestUser)
        .build();
    updateGameEntity = GameEntity.builder()
        .gameId(1L)
        .title("수정테스트제목")
        .content("수정테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .cityName(CityName.SEOUL)
        .userEntity(creatorUser)
        .build();
  }

  @Test
  @DisplayName("경기 생성 성공")
  public void testCreateGame_success() {
    // Given
    String token = "sampleToken";
    CreateRequest request = CreateRequest.builder()
        .title("테스트제목")
        .content("테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .build();

    when(tokenProvider.parseClaims(anyString()))
        .thenReturn(Jwts.claims().setSubject("test@example.com"));

    // 유저
    when(userRepository.findByEmail(anyString())).thenReturn(
        Optional.ofNullable(requestUser));

    // aroundGameCount를 0으로 설정하여 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository.countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull(any(), any(), anyString()))
        .thenReturn(OptionalLong.of(0L));

    // CREATOR 추가
    when(userRepository.save(any())).thenReturn(creatorUser);

    ArgumentCaptor<GameEntity> gameEntityArgumentCaptor = ArgumentCaptor.forClass(
        GameEntity.class);

    // when
    gameService.createGame(request, token);

    // Then
    verify(gameRepository).save(gameEntityArgumentCaptor.capture());

    GameEntity savedGameEntity = gameEntityArgumentCaptor.getValue();

    assertEquals(savedGameEntity.getTitle(), createGameEntity.getTitle());
    assertEquals(savedGameEntity.getContent(), createGameEntity.getContent());
    assertEquals(savedGameEntity.getHeadCount(), createGameEntity.getHeadCount());
    assertEquals(savedGameEntity.getFieldStatus(), createGameEntity.getFieldStatus());
    assertEquals(savedGameEntity.getGender(), createGameEntity.getGender());
    assertEquals(savedGameEntity.getStartDateTime(), createGameEntity.getStartDateTime());
    assertEquals(savedGameEntity.getInviteYn(), createGameEntity.getInviteYn());
    assertEquals(savedGameEntity.getAddress(), createGameEntity.getAddress());
    assertEquals(savedGameEntity.getLatitude(), createGameEntity.getLatitude());
    assertEquals(savedGameEntity.getLongitude(), createGameEntity.getLongitude());
    assertEquals(savedGameEntity.getCityName(), createGameEntity.getCityName());
    assertEquals(savedGameEntity.getMatchFormat(), createGameEntity.getMatchFormat());
    assertEquals(savedGameEntity.getUserEntity().getUserId(),
        createGameEntity.getUserEntity().getUserId());

  }

  @Test
  @DisplayName("경기 상세 조회 성공")
  void getGameDetail_success() {
    // Given
    Long gameId = 1L;


    // CREATOR 추가
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.of(createGameEntity));

    // when
    DetailResponse detailResponse = gameService.getGameDetail(gameId);

    // Then
    assertEquals(detailResponse.getGameId(), createGameEntity.getGameId());
    assertEquals(detailResponse.getTitle(), createGameEntity.getTitle());
    assertEquals(detailResponse.getContent(), createGameEntity.getContent());
    assertEquals(detailResponse.getHeadCount(), createGameEntity.getHeadCount());
    assertEquals(detailResponse.getFieldStatus(), createGameEntity.getFieldStatus());
    assertEquals(detailResponse.getGender(), createGameEntity.getGender());
    assertEquals(detailResponse.getStartDateTime(), createGameEntity.getStartDateTime());
    assertEquals(detailResponse.getInviteYn(), createGameEntity.getInviteYn());
    assertEquals(detailResponse.getAddress(), createGameEntity.getAddress());
    assertEquals(detailResponse.getLatitude(), createGameEntity.getLatitude());
    assertEquals(detailResponse.getLongitude(), createGameEntity.getLongitude());
    assertEquals(detailResponse.getCityName(), createGameEntity.getCityName());
    assertEquals(detailResponse.getMatchFormat(), createGameEntity.getMatchFormat());
    assertEquals(detailResponse.getUserId(),
        createGameEntity.getUserEntity().getUserId());
  }

  @Test
  @DisplayName("경기 수정 성공")
  void updateGame_success() {
    // Given
    String token = "sampleToken";
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .title("수정테스트제목")
        .content("수정테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .build();

    GameEntity gameEntity = UpdateRequest.toEntity(updateRequest, createGameEntity);

    when(tokenProvider.parseClaims(anyString()))
        .thenReturn(Jwts.claims().setSubject("test@example.com"));

    // 유저
    when(userRepository.findByEmail(anyString())).thenReturn(
        Optional.ofNullable(creatorUser));

    // 경기
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createGameEntity));

    // aroundGameCount를 0으로 설정하여 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(OptionalLong.of(0L));

    // 현재 경기에 수락된 인원수가 없다고 가정
    when(participantGameRepository.countByStatusAndGameEntityGameId
        (eq(ACCEPT), anyLong()))
        .thenReturn(OptionalLong.of(0L));

    ArgumentCaptor<GameEntity> gameEntityArgumentCaptor = ArgumentCaptor.forClass(
        GameEntity.class);

    // when
    gameService.updateGame(updateRequest, token);

    // Then
    verify(gameRepository).save(gameEntityArgumentCaptor.capture());

    GameEntity updatedGameEntity = gameEntityArgumentCaptor.getValue();

    assertEquals(updatedGameEntity.getGameId(), updateGameEntity.getGameId());
    assertEquals(updatedGameEntity.getTitle(), updateGameEntity.getTitle());
    assertEquals(updatedGameEntity.getContent(), updateGameEntity.getContent());
    assertEquals(updatedGameEntity.getHeadCount(), updateGameEntity.getHeadCount());
    assertEquals(updatedGameEntity.getFieldStatus(), updateGameEntity.getFieldStatus());
    assertEquals(updatedGameEntity.getGender(), updateGameEntity.getGender());
    assertEquals(updatedGameEntity.getStartDateTime(), updateGameEntity.getStartDateTime());
    assertEquals(updatedGameEntity.getInviteYn(), updateGameEntity.getInviteYn());
    assertEquals(updatedGameEntity.getAddress(), updateGameEntity.getAddress());
    assertEquals(updatedGameEntity.getLatitude(), updateGameEntity.getLatitude());
    assertEquals(updatedGameEntity.getLongitude(), updateGameEntity.getLongitude());
    assertEquals(updatedGameEntity.getCityName(), updateGameEntity.getCityName());
    assertEquals(updatedGameEntity.getMatchFormat(), updateGameEntity.getMatchFormat());
    assertEquals(updatedGameEntity.getUserEntity().getUserId(),
        updateGameEntity.getUserEntity().getUserId());
  }

  @Test
  @DisplayName("경기 삭제 성공")
  void deleteGame_success() {
    //Given
    String token = "sampleToken";
    DeleteRequest deleteRequest = DeleteRequest.builder()
        .gameId(1L)
        .build();

    when(tokenProvider.parseClaims(anyString()))
        .thenReturn(Jwts.claims().setSubject("test@example.com"));

    // 유저
    when(userRepository.findByEmail(anyString())).thenReturn(
        Optional.ofNullable(creatorUser));

    // 경기
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createGameEntity));

    // 경기 삭제 전에 기존에 경기에 ACCEPT 멤버가 없다고 가정
    when(participantGameRepository.findByStatusInAndGameEntityGameId
        (anyList(), anyLong())).thenReturn(new ArrayList<>());

    ArgumentCaptor<GameEntity> gameEntityArgumentCaptor = ArgumentCaptor.forClass(
        GameEntity.class);

    // when
    gameService.deleteGame(deleteRequest, token);

    // Then
    verify(gameRepository).save(gameEntityArgumentCaptor.capture());

    GameEntity deletedGameEntity = gameEntityArgumentCaptor.getValue();

    assertEquals(deletedGameEntity.getGameId(), updateGameEntity.getGameId());
    assertEquals(deletedGameEntity.getUserEntity().getUserId(),
        updateGameEntity.getUserEntity().getUserId());

  }
}
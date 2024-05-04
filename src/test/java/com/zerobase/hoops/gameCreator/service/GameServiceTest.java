package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.DELETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

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

  private GameEntity createdGameEntity;

  private GameEntity updatedGameEntity;

  private GameEntity deletedGameEntity;

  private ParticipantGameEntity creatorParticipantGameEntity;

  private ParticipantGameEntity deletedPartEntity;

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
    createdGameEntity = GameEntity.builder()
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
    updatedGameEntity = GameEntity.builder()
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
        .userEntity(requestUser)
        .build();
    deletedGameEntity = GameEntity.builder()
        .gameId(1L)
        .title("수정테스트제목")
        .content("수정테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .deletedDateTime(LocalDateTime.of(2024, 7, 10, 12, 0, 0))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
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
    deletedPartEntity = ParticipantGameEntity.builder()
        .participantId(1L)
        .status(DELETE)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .deletedDateTime(LocalDateTime.of(2025, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(requestUser)
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
        .thenReturn(0L);

    when(gameRepository.save(any())).thenReturn(createdGameEntity);

    when(participantGameRepository.save(any())).thenReturn(creatorParticipantGameEntity);

    ArgumentCaptor<GameEntity> gameEntityArgumentCaptor = ArgumentCaptor.forClass(
        GameEntity.class);

    // when
    gameService.createGame(request, token);

    // Then
    verify(gameRepository).save(gameEntityArgumentCaptor.capture());

    GameEntity savedGameEntity = gameEntityArgumentCaptor.getValue();

    assertEquals(savedGameEntity.getTitle(), createdGameEntity.getTitle());
    assertEquals(savedGameEntity.getContent(), createdGameEntity.getContent());
    assertEquals(savedGameEntity.getHeadCount(), createdGameEntity.getHeadCount());
    assertEquals(savedGameEntity.getFieldStatus(), createdGameEntity.getFieldStatus());
    assertEquals(savedGameEntity.getGender(), createdGameEntity.getGender());
    assertEquals(savedGameEntity.getStartDateTime(), createdGameEntity.getStartDateTime());
    assertEquals(savedGameEntity.getInviteYn(), createdGameEntity.getInviteYn());
    assertEquals(savedGameEntity.getAddress(), createdGameEntity.getAddress());
    assertEquals(savedGameEntity.getLatitude(), createdGameEntity.getLatitude());
    assertEquals(savedGameEntity.getLongitude(), createdGameEntity.getLongitude());
    assertEquals(savedGameEntity.getCityName(), createdGameEntity.getCityName());
    assertEquals(savedGameEntity.getMatchFormat(), createdGameEntity.getMatchFormat());
    assertEquals(savedGameEntity.getUserEntity().getUserId(),
        createdGameEntity.getUserEntity().getUserId());
  }

  @Test
  @DisplayName("경기 상세 조회 성공")
  void getGameDetail_success() {
    // Given
    Long gameId = 1L;

    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.of(createdGameEntity));

    // when
    DetailResponse detailResponse = gameService.getGameDetail(gameId);

    // Then
    assertEquals(detailResponse.getGameId(), createdGameEntity.getGameId());
    assertEquals(detailResponse.getTitle(), createdGameEntity.getTitle());
    assertEquals(detailResponse.getContent(), createdGameEntity.getContent());
    assertEquals(detailResponse.getHeadCount(), createdGameEntity.getHeadCount());
    assertEquals(detailResponse.getFieldStatus(), createdGameEntity.getFieldStatus());
    assertEquals(detailResponse.getGender(), createdGameEntity.getGender());
    assertEquals(detailResponse.getStartDateTime(), createdGameEntity.getStartDateTime());
    assertEquals(detailResponse.getInviteYn(), createdGameEntity.getInviteYn());
    assertEquals(detailResponse.getAddress(), createdGameEntity.getAddress());
    assertEquals(detailResponse.getLatitude(), createdGameEntity.getLatitude());
    assertEquals(detailResponse.getLongitude(), createdGameEntity.getLongitude());
    assertEquals(detailResponse.getCityName(), createdGameEntity.getCityName());
    assertEquals(detailResponse.getMatchFormat(), createdGameEntity.getMatchFormat());
    assertEquals(detailResponse.getUserId(),
        createdGameEntity.getUserEntity().getUserId());
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

    GameEntity gameEntity = UpdateRequest.toEntity(updateRequest,
        createdGameEntity);

    when(tokenProvider.parseClaims(anyString()))
        .thenReturn(Jwts.claims().setSubject("test@example.com"));

    // 유저
    when(userRepository.findByEmail(anyString())).thenReturn(
        Optional.ofNullable(requestUser));

    // 경기
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // aroundGameCount를 0으로 설정하여 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(0L);

    // 현재 경기에 수락된 인원수가 없다고 가정
    when(participantGameRepository.countByStatusAndGameEntityGameId
        (eq(ACCEPT), anyLong()))
        .thenReturn(0L);

    // 경기 수정
    when(gameRepository.save(any())).thenReturn(updatedGameEntity);

    ArgumentCaptor<GameEntity> gameEntityArgumentCaptor = ArgumentCaptor.forClass(
        GameEntity.class);

    // when
    gameService.updateGame(updateRequest, token);

    // Then
    verify(gameRepository).save(gameEntityArgumentCaptor.capture());

    GameEntity updatedGameEntity = gameEntityArgumentCaptor.getValue();

    assertEquals(updatedGameEntity.getGameId(), this.updatedGameEntity.getGameId());
    assertEquals(updatedGameEntity.getTitle(), this.updatedGameEntity.getTitle());
    assertEquals(updatedGameEntity.getContent(), this.updatedGameEntity.getContent());
    assertEquals(updatedGameEntity.getHeadCount(), this.updatedGameEntity.getHeadCount());
    assertEquals(updatedGameEntity.getFieldStatus(), this.updatedGameEntity.getFieldStatus());
    assertEquals(updatedGameEntity.getGender(), this.updatedGameEntity.getGender());
    assertEquals(updatedGameEntity.getStartDateTime(), this.updatedGameEntity.getStartDateTime());
    assertEquals(updatedGameEntity.getInviteYn(), this.updatedGameEntity.getInviteYn());
    assertEquals(updatedGameEntity.getAddress(), this.updatedGameEntity.getAddress());
    assertEquals(updatedGameEntity.getLatitude(), this.updatedGameEntity.getLatitude());
    assertEquals(updatedGameEntity.getLongitude(), this.updatedGameEntity.getLongitude());
    assertEquals(updatedGameEntity.getCityName(), this.updatedGameEntity.getCityName());
    assertEquals(updatedGameEntity.getMatchFormat(), this.updatedGameEntity.getMatchFormat());
    assertEquals(updatedGameEntity.getUserEntity().getUserId(),
        this.updatedGameEntity.getUserEntity().getUserId());
  }

  @Test
  @DisplayName("경기 삭제 성공")
  void deleteGame_success() {
    //Given
    String token = "sampleToken";
    DeleteRequest deleteRequest = DeleteRequest.builder()
        .gameId(1L)
        .build();

    List<ParticipantGameEntity> groupList = new ArrayList<>();
    groupList.add(creatorParticipantGameEntity);

    when(tokenProvider.parseClaims(anyString()))
        .thenReturn(Jwts.claims().setSubject("test@example.com"));

    // 유저
    when(userRepository.findByEmail(anyString())).thenReturn(
        Optional.ofNullable(requestUser));

    // 경기
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(updatedGameEntity));

    // 경기 삭제 전에 기존에 경기에 ACCEPT 멤버가 자기 자신만 있다고 가정
    when(participantGameRepository.findByStatusInAndGameEntityGameId
        (anyList(), anyLong())).thenReturn(groupList);

    when(participantGameRepository.save(any()))
        .thenReturn(deletedPartEntity);

    when(gameRepository.save(any())).thenReturn(deletedGameEntity);

    ArgumentCaptor<GameEntity> gameEntityArgumentCaptor = ArgumentCaptor.forClass(
        GameEntity.class);

    // when
    gameService.deleteGame(deleteRequest, token);

    // Then
    verify(gameRepository).save(gameEntityArgumentCaptor.capture());

    GameEntity captorEntity = gameEntityArgumentCaptor.getValue();

    assertEquals(captorEntity.getGameId(), updatedGameEntity.getGameId());
    assertEquals(captorEntity.getUserEntity().getUserId(),
        updatedGameEntity.getUserEntity().getUserId());

  }
}
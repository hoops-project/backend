package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.DELETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateResponse;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.invite.repository.InviteRepository;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  @InjectMocks
  private GameService gameService;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ParticipantGameRepository participantGameRepository;

  @Mock
  private GameRepository gameRepository;

  @Mock
  private InviteRepository inviteRepository;

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
        .placeName("서울 농구장")
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
        .placeName("서울 농구장")
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
        .placeName("서울 농구장")
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
    CreateRequest request = CreateRequest.builder()
        .title("테스트제목")
        .content("테스트내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.INDOOR)
        .gender(Gender.ALL)
        .startDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .placeName("서울 농구장")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .build();

    getCurrentUser();

    // aroundGameCount를 0으로 설정하여 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository.existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull(any(), any(), anyString()))
        .thenReturn(false);

    when(gameRepository.save(any())).thenReturn(createdGameEntity);

    when(participantGameRepository.save(any())).thenReturn(creatorParticipantGameEntity);

    // when
    CreateResponse result = gameService.createGame(request);

    // Then
    assertEquals(result.getTitle(), createdGameEntity.getTitle());
    assertEquals(result.getContent(), createdGameEntity.getContent());
    assertEquals(result.getHeadCount(), createdGameEntity.getHeadCount());
    assertEquals(result.getFieldStatus(), createdGameEntity.getFieldStatus());
    assertEquals(result.getGender(), createdGameEntity.getGender());
    assertEquals(result.getStartDateTime(), createdGameEntity.getStartDateTime());
    assertEquals(result.getInviteYn(), createdGameEntity.getInviteYn());
    assertEquals(result.getAddress(), createdGameEntity.getAddress());
    assertEquals(result.getPlaceName(), createdGameEntity.getPlaceName());
    assertEquals(result.getLatitude(), createdGameEntity.getLatitude());
    assertEquals(result.getLongitude(), createdGameEntity.getLongitude());
    assertEquals(result.getCityName(), createdGameEntity.getCityName());
    assertEquals(result.getMatchFormat(), createdGameEntity.getMatchFormat());
  }

  @Test
  @DisplayName("경기 생성 실패: 경기 시작 시간은 현재 시간으로부터 최소 30분 이후여야 합니다.")
  public void testCreateGame_failIfStartTimeLessThan30MinutesAhead() {
    // Given
    CreateRequest request = CreateRequest.builder()
        .startDateTime(LocalDateTime.now().plusMinutes(15))
        .address("테스트 주소")
        .build();

    getCurrentUser();

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository.existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull
        (any(LocalDateTime.class), any(LocalDateTime.class), eq("테스트 주소")))
        .thenReturn(false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.createGame(request);
    });

    // Then
    assertEquals(ErrorCode.NOT_AFTER_THIRTY_MINUTE, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 생성 실패: 해당 시간 범위에 이미 경기가 존재")
  public void testCreateGame_failIfGameExistsInTimeRange() {
    // given
    CreateRequest request = CreateRequest.builder()
        .startDateTime(LocalDateTime.now().plusHours(1))
        .address("테스트 주소")
        .build();

    getCurrentUser();

    when(gameRepository.existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull
        (any(LocalDateTime.class), any(LocalDateTime.class), eq("테스트 주소")))
        .thenReturn(true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.createGame(request);
    });

    // then
    assertEquals(ErrorCode.ALREADY_GAME_CREATED, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 상세 조회 성공")
  void getGameDetail_success() {
    // Given
    Long gameId = 1L;

    List<ParticipantGameEntity> participantGameEntityList =
        List.of(creatorParticipantGameEntity);

    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.of(createdGameEntity));

    // 게임에 참가한 사람이 게임 개설자 밖에 없다고 가정
    when(participantGameRepository
        .findByGameEntityGameIdAndStatusAndDeletedDateTimeNull(anyLong(),
            eq(ACCEPT))).thenReturn(participantGameEntityList);

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
    assertEquals(detailResponse.getNickName(),
        createdGameEntity.getUserEntity().getNickName());
    assertEquals(detailResponse.getUserId(),
        createdGameEntity.getUserEntity().getUserId());
  }

  @Test
  @DisplayName("경기 수정 성공")
  void updateGame_success() {
    // Given
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
        .placeName("서울 농구장")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .build();

    GameEntity gameEntity = UpdateRequest.toEntity(updateRequest,
        createdGameEntity);

    getCurrentUser();

    // 경기
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(false);

    // 현재 경기에 수락된 인원수가 개설자 한명만 있다고 가정
    when(participantGameRepository.countByStatusAndGameEntityGameId
        (eq(ACCEPT), anyLong()))
        .thenReturn(1L);

    // 경기 수정
    when(gameRepository.save(any())).thenReturn(updatedGameEntity);


    // when
    UpdateResponse result = gameService.updateGame(updateRequest);

    // Then
    assertEquals(result.getGameId(), this.updatedGameEntity.getGameId());
    assertEquals(result.getTitle(), this.updatedGameEntity.getTitle());
    assertEquals(result.getContent(), this.updatedGameEntity.getContent());
    assertEquals(result.getHeadCount(), this.updatedGameEntity.getHeadCount());
    assertEquals(result.getFieldStatus(), this.updatedGameEntity.getFieldStatus());
    assertEquals(result.getGender(), this.updatedGameEntity.getGender());
    assertEquals(result.getStartDateTime(), this.updatedGameEntity.getStartDateTime());
    assertEquals(result.getInviteYn(), this.updatedGameEntity.getInviteYn());
    assertEquals(result.getAddress(), this.updatedGameEntity.getAddress());
    assertEquals(result.getLatitude(), this.updatedGameEntity.getLatitude());
    assertEquals(result.getLongitude(), this.updatedGameEntity.getLongitude());
    assertEquals(result.getCityName(), this.updatedGameEntity.getCityName());
    assertEquals(result.getMatchFormat(), this.updatedGameEntity.getMatchFormat());
  }

  @Test
  @DisplayName("경기 수정 실패 : 경기 시작 시간은 현재 시간으로부터 최소 30분 이후여야 합니다.")
  void updateGame_failIfStartTimeLessThan30MinutesAhead() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .startDateTime(LocalDateTime.now().plusMinutes(15))
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();

    GameEntity gameEntity = UpdateRequest.toEntity(updateRequest,
        createdGameEntity);

    getCurrentUser();

    // 경기
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(false);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.updateGame(updateRequest);
    });

    // Then
    assertEquals(ErrorCode.NOT_AFTER_THIRTY_MINUTE, exception.getErrorCode());
  }


  @Test
  @DisplayName("경기 수정 실패 : 해당 시간 범위에 이미 경기가 존재")
  void updateGame_failIfGameExistsInTimeRange() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();

    GameEntity gameEntity = UpdateRequest.toEntity(updateRequest,
        createdGameEntity);

    getCurrentUser();

    // 경기
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 있음.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.updateGame(updateRequest);
    });

    // Then
    assertEquals(ErrorCode.ALREADY_GAME_CREATED, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 수정 실패 : 변경 하려는 인원수가 팀원 수보다 작게 설정")
  void updateGame_failWhenParticipantCountIsTooLow() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .headCount(6L)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();

    GameEntity gameEntity = UpdateRequest.toEntity(updateRequest,
        createdGameEntity);

    getCurrentUser();

    // 경기
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(false);

    when(participantGameRepository
        .countByStatusAndGameEntityGameId(eq(ACCEPT), anyLong()))
        .thenReturn(8L);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.updateGame(updateRequest);
    });

    // Then
    assertEquals(ErrorCode.NOT_UPDATE_HEADCOUNT, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 수정 실패 : 팀원 중 남성이 있을 때 경기 성별을 여성으로 변경하려고 할 때")
  void updateGame_failWhenChangingGenderToFemaleWithMaleParticipants() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .headCount(10L)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .gender(Gender.FEMALEONLY)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();

    GameEntity gameEntity = UpdateRequest.toEntity(updateRequest,
        createdGameEntity);

    getCurrentUser();

    // 경기
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(false);

    when(participantGameRepository
        .countByStatusAndGameEntityGameId(eq(ACCEPT), anyLong()))
        .thenReturn(8L);

    when(participantGameRepository
        .existsByStatusAndGameEntityGameIdAndUserEntityGender
            (eq(ACCEPT), anyLong(), eq(GenderType.MALE)))
        .thenReturn(true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.updateGame(updateRequest);
    });

    // Then
    assertEquals(ErrorCode.NOT_UPDATE_WOMAN, exception.getErrorCode());
  }


  @Test
  @DisplayName("경기 수정 실패 : 팀원 중 여성이 있을 때 경기 성별을 남성으로 변경하려고 할 때")
  void updateGame_failWhenChangingGenderToMaleWithFemaleParticipants() {
    // Given
    UpdateRequest updateRequest = UpdateRequest.builder()
        .gameId(1L)
        .headCount(10L)
        .startDateTime(LocalDateTime.now().plusHours(1))
        .gender(Gender.MALEONLY)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .build();

    GameEntity gameEntity = UpdateRequest.toEntity(updateRequest,
        createdGameEntity);

    getCurrentUser();

    // 경기
    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(false);

    when(participantGameRepository
        .countByStatusAndGameEntityGameId(eq(ACCEPT), anyLong()))
        .thenReturn(8L);

    when(participantGameRepository
        .existsByStatusAndGameEntityGameIdAndUserEntityGender
            (eq(ACCEPT), anyLong(), eq(GenderType.FEMALE)))
        .thenReturn(true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.updateGame(updateRequest);
    });

    // Then
    assertEquals(ErrorCode.NOT_UPDATE_MAN, exception.getErrorCode());
  }






  @Test
  @DisplayName("경기 삭제 성공")
  void deleteGame_success() {
    //Given
    DeleteRequest deleteRequest = DeleteRequest.builder()
        .gameId(1L)
        .build();

    List<ParticipantGameEntity> groupList = new ArrayList<>();
    groupList.add(creatorParticipantGameEntity);

    getCurrentUser();

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
    gameService.delete(deleteRequest);

    // Then
    verify(gameRepository).save(gameEntityArgumentCaptor.capture());

    GameEntity captorEntity = gameEntityArgumentCaptor.getValue();

    assertEquals(captorEntity.getGameId(), updatedGameEntity.getGameId());
    assertEquals(captorEntity.getUserEntity().getUserId(),
        updatedGameEntity.getUserEntity().getUserId());

  }

  private void getCurrentUser() {
    when(jwtTokenExtract.currentUser()).thenReturn(requestUser);

    when(userRepository.findById(anyLong())).thenReturn(
        Optional.ofNullable(requestUser));
  }

}
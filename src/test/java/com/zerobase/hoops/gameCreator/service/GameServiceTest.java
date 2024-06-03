package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.DELETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.chat.repository.ChatRoomRepository;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteGameResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.WithDrawGameResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

  @Mock
  private ChatRoomRepository chatRoomRepository;

  private UserEntity requestUser;

  private UserEntity receiveUser;

  private GameEntity createdGameEntity;

  private GameEntity updatedGameEntity;

  private GameEntity deletedGameEntity;

  private ParticipantGameEntity creatorParticipantGameEntity;

  @BeforeEach
  void setUp() {
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
    receiveUser = UserEntity.builder()
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
    createdGameEntity = GameEntity.builder()
        .id(1L)
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
        .id(1L)
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
        .id(1L)
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
        .id(1L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
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

    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.of(createdGameEntity));

    // 게임에 참가한 사람이 게임 개설자 밖에 없다고 가정
    when(participantGameRepository
        .findByGameEntityIdAndStatusAndDeletedDateTimeNull(anyLong(),
            eq(ACCEPT))).thenReturn(participantGameEntityList);

    // when
    DetailResponse detailResponse = gameService.getGameDetail(gameId);

    // Then
    assertEquals(detailResponse.getGameId(), createdGameEntity.getId());
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
        createdGameEntity.getUserEntity().getId());
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
    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(false);

    // 현재 경기에 수락된 인원수가 개설자 한명만 있다고 가정
    when(participantGameRepository.countByStatusAndGameEntityId
        (eq(ACCEPT), anyLong()))
        .thenReturn(1L);

    // 경기 수정
    when(gameRepository.save(any())).thenReturn(updatedGameEntity);


    // when
    UpdateResponse result = gameService.updateGame(updateRequest);

    // Then
    assertEquals(result.getGameId(), this.updatedGameEntity.getId());
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
    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndIdNot
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
    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 있음.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndIdNot
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
    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(false);

    when(participantGameRepository
        .countByStatusAndGameEntityId(eq(ACCEPT), anyLong()))
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
    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(false);

    when(participantGameRepository
        .countByStatusAndGameEntityId(eq(ACCEPT), anyLong()))
        .thenReturn(8L);

    when(participantGameRepository
        .existsByStatusAndGameEntityIdAndUserEntityGender
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
    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 이미 예정된 게임이 없는 상황을 가정합니다.
    when(gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndIdNot
            (any(), any(), anyString(), anyLong()))
        .thenReturn(false);

    when(participantGameRepository
        .countByStatusAndGameEntityId(eq(ACCEPT), anyLong()))
        .thenReturn(8L);

    when(participantGameRepository
        .existsByStatusAndGameEntityIdAndUserEntityGender
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
  @DisplayName("경기 삭제 성공 : 경기 개설자가 삭제")
  void deleteGame_successGameCreator() {
    //Given
    DeleteRequest deleteRequest = DeleteRequest.builder()
        .gameId(1L)
        .build();

    InviteEntity requestInvite = InviteEntity.builder()
        .id(1L)
        .inviteStatus(InviteStatus.REQUEST)
        .requestedDateTime(LocalDateTime.now())
        .gameEntity(createdGameEntity)
        .senderUserEntity(requestUser)
        .receiverUserEntity(receiveUser)
        .build();

    List<ParticipantGameEntity> groupList = new ArrayList<>();
    groupList.add(creatorParticipantGameEntity);

    List<InviteEntity> inviteEntityList = new ArrayList<>();
    inviteEntityList.add(requestInvite);

    DeleteGameResponse response = DeleteGameResponse.toDto(deletedGameEntity);

    getCurrentUser();

    // 경기
    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(updatedGameEntity));

    // 경기 삭제 전에 기존에 경기에 ACCEPT 멤버가 자기 자신만 있다고 가정
    when(participantGameRepository.findByStatusInAndGameEntityId
        (anyList(), anyLong())).thenReturn(groupList);

    // 해당 경기에 초대 신청된 것들 다 CANCEL
    when(inviteRepository.findByInviteStatusAndGameEntityId
        (eq(InviteStatus.REQUEST), anyLong()))
        .thenReturn(inviteEntityList);

    when(gameRepository.save(any())).thenReturn(deletedGameEntity);

    // when
    Object object = gameService.delete(deleteRequest);
    DeleteGameResponse result = (DeleteGameResponse) object;

    // Then
    groupList.forEach(participant -> {
      verify(participantGameRepository).save(participant);
      assertEquals(DELETE, participant.getStatus());
      assertNotNull(participant.getDeletedDateTime());
    });

    inviteEntityList.forEach(invite -> {
      verify(inviteRepository).save(invite);
      assertEquals(InviteStatus.DELETE, invite.getInviteStatus());
      assertNotNull(invite.getDeletedDateTime());
    });

    assertEquals(response.getGameId(), result.getGameId());
    assertNotNull(result.getDeletedDateTime());
  }

  @Test
  @DisplayName("경기 삭제 성공 : 팀원이 삭제")
  void deleteGame_successGameUser() {
    //Given
    DeleteRequest deleteRequest = DeleteRequest.builder()
        .gameId(1L)
        .build();

    ParticipantGameEntity receivePartEntity = ParticipantGameEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .acceptedDateTime(LocalDateTime.of(2025, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(receiveUser)
        .build();

    ParticipantGameEntity deletedPartEntity =
        ParticipantGameEntity.setWithdraw(receivePartEntity);

    WithDrawGameResponse response =
        WithDrawGameResponse.toDto(deletedPartEntity);

    getReceiveUser();

    // 경기
    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(updatedGameEntity));

    // 자기 자신 CANCEL로 업데이트
    when(participantGameRepository
        .findByStatusAndGameEntityIdAndUserEntityId
        (eq(ACCEPT), anyLong(), anyLong()))
        .thenReturn(Optional.ofNullable(receivePartEntity));

    when(participantGameRepository.save(any())).thenReturn(deletedPartEntity);

    // when
    Object object = gameService.delete(deleteRequest);
    WithDrawGameResponse result = (WithDrawGameResponse) object;

    // Then
    assertEquals(response.getGameId(), result.getGameId());
    assertEquals(response.getStatus(), result.getStatus());
    assertEquals(response.getUserId(), result.getUserId());
    assertNotNull(result.getWithdrewDateTime());
  }

  @Test
  @DisplayName("경기 삭제 실패 : 경기 시작 30분 전에만 삭제 가능")
  void deleteGame_fail() {
    //Given
    DeleteRequest deleteRequest = DeleteRequest.builder()
        .gameId(1L)
        .build();

    GameEntity requestGameEntity = GameEntity.builder()
        .id(1L)
        .startDateTime(LocalDateTime.now().plusMinutes(15))
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .placeName("서울 농구장")
        .userEntity(requestUser)
        .build();

    getCurrentUser();

    // 경기
    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(requestGameEntity));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      gameService.delete(deleteRequest);
    });

    // Then
    assertEquals(ErrorCode.NOT_DELETE_STARTDATE, exception.getErrorCode());
  }

  private void getCurrentUser() {
    when(jwtTokenExtract.currentUser()).thenReturn(requestUser);

    when(userRepository.findById(anyLong())).thenReturn(
        Optional.ofNullable(requestUser));
  }

  private void getReceiveUser() {
    when(jwtTokenExtract.currentUser()).thenReturn(receiveUser);

    when(userRepository.findById(anyLong())).thenReturn(
        Optional.ofNullable(receiveUser));
  }

}
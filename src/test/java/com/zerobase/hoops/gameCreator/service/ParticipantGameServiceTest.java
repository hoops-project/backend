package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.KICKOUT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.alarm.repository.EmitterRepository;
import com.zerobase.hoops.alarm.repository.NotificationRepository;
import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.NotificationEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.AcceptRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.AcceptResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.KickoutRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.KickoutResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.RejectRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.RejectResponse;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
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
class ParticipantGameServiceTest {

  @InjectMocks
  private ParticipantGameService participantGameService;

  @Mock
  private NotificationService notificationService;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ParticipantGameRepository participantGameRepository;

  @Mock
  private GameRepository gameRepository;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private EmitterRepository emitterRepository;

  private UserEntity createdUser;
  private UserEntity applyedUser;

  private GameEntity createdGameEntity;

  private ParticipantGameEntity creatorParticipantGameEntity;

  private NotificationEntity notificationEntity;

  @BeforeEach
  void setUp() {
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
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    applyedUser = UserEntity.builder()
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
        .startDateTime(LocalDateTime.now().plusHours(1))
        .inviteYn(true)
        .address("서울 마포구 와우산로13길 6 지하1,2층 (서교동)")
        .latitude(32.13123)
        .longitude(123.13123)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .cityName(CityName.SEOUL)
        .user(createdUser)
        .build();
    creatorParticipantGameEntity = ParticipantGameEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(createdUser)
        .build();
    notificationEntity = NotificationEntity.builder()
        .receiver(applyedUser)
        .content("테스트내용")
        .createdDateTime(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("경기 참가자 리스트 조회 성공")
  void getParticipantList_success() {
    // Given
    Long gameId = 1L;

    List<ParticipantGameEntity> participantList = List.of(creatorParticipantGameEntity);

    List<DetailResponse> detailResponseList = participantList.stream()
        .map(DetailResponse::toDto)
        .toList();

    getCurrentUser();

    when(gameRepository.findByIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    when(participantGameRepository.findByStatusAndGameEntityId
        (eq(APPLY), eq(gameId))).thenReturn(participantList);

    // when
    List<DetailResponse> result = participantGameService
        .getApplyParticipantList(gameId);

    // Then
    assertThat(result).containsExactlyElementsOf(detailResponseList);
  }

  @Test
  @DisplayName("경기 참가 희망자 수락 성공")
  void acceptParticipant_success() {
    // Given
    AcceptRequest request = AcceptRequest.builder()
        .participantId(2L)
        .build();

    ParticipantGameEntity applyPartEntity = ParticipantGameEntity.builder()
        .id(2L)
        .status(APPLY)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    ParticipantGameEntity acceptPartEntity = ParticipantGameEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .acceptedDateTime(LocalDateTime.of(2024, 10, 10, 12, 30, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    getCurrentUser();

    when(participantGameRepository.findByIdAndStatus
        (eq(request.getParticipantId()), eq(APPLY)))
        .thenReturn(Optional.ofNullable(applyPartEntity));

    assert applyPartEntity != null;
    when(gameRepository.findByIdAndDeletedDateTimeNull
        (eq(applyPartEntity.getGameEntity().getId())))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // 경기에 참가자가 1명만 있다고 가정
    when(participantGameRepository.countByStatusAndGameEntityId
        (eq(ACCEPT), eq(createdGameEntity.getId()))).thenReturn(1L);

    when(participantGameRepository.save(any()))
        .thenReturn(acceptPartEntity);

    // when
    AcceptResponse response = participantGameService.acceptParticipant(request);

    // Then
    assertEquals(acceptPartEntity.getId(),
        response.getParticipantId());
    assertEquals(acceptPartEntity.getStatus(), response.getStatus());
    assertEquals(acceptPartEntity.getUserEntity().getId(),
        response.getUserId());
  }

  @Test
  @DisplayName("경기 참가 희망자 수락 실패 : 경기가 이미 시작 함")
  void acceptParticipant_failIfGameAlreadyStarted() {
    // Given
    AcceptRequest request = AcceptRequest.builder()
        .participantId(2L)
        .build();

    createdGameEntity = GameEntity.builder()
        .id(1L)
        .startDateTime(LocalDateTime.now().minusHours(1))
        .user(createdUser)
        .build();

    ParticipantGameEntity applyPartEntity = ParticipantGameEntity.builder()
        .id(2L)
        .status(APPLY)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    getCurrentUser();

    when(participantGameRepository.findByIdAndStatus
        (eq(request.getParticipantId()), eq(APPLY)))
        .thenReturn(Optional.ofNullable(applyPartEntity));

    assert applyPartEntity != null;
    when(gameRepository.findByIdAndDeletedDateTimeNull
        (eq(applyPartEntity.getGameEntity().getId())))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.acceptParticipant(request);
    });

    // Then
    assertEquals(ErrorCode.ALREADY_GAME_START, exception.getErrorCode());
  }

  @Test
  @DisplayName("경기 참가 희망자 거절 성공")
  void rejectParticipant_success() {
    // Given
    RejectRequest request = RejectRequest.builder()
        .participantId(2L)
        .build();

    ParticipantGameEntity applyPartEntity = ParticipantGameEntity.builder()
        .id(2L)
        .status(APPLY)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    ParticipantGameEntity rejectPartEntity = ParticipantGameEntity.builder()
        .id(2L)
        .status(REJECT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .rejectedDateTime(LocalDateTime.of(2024, 10, 10, 12, 30, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    getCurrentUser();

    when(participantGameRepository.findByIdAndStatus(anyLong(), eq(APPLY)))
        .thenReturn(Optional.ofNullable(applyPartEntity));

    when(gameRepository.findByIdAndDeletedDateTimeNull
        (anyLong())).thenReturn(Optional.ofNullable(createdGameEntity));

    when(participantGameRepository.save(any()))
        .thenReturn(rejectPartEntity);

    // when
    RejectResponse response = participantGameService.rejectParticipant(request);

    // Then
    assertEquals(rejectPartEntity.getId(),
        response.getParticipantId());
    assertEquals(rejectPartEntity.getStatus(), response.getStatus());
    assertEquals(rejectPartEntity.getUserEntity().getId(),
        response.getUserId());
  }

  @Test
  @DisplayName("경기 참가자 강퇴 성공")
  void kickoutParticipant_success() {
    // Given
    Long gameId = 1L;

    KickoutRequest request = KickoutRequest.builder()
        .participantId(2L)
        .build();

    ParticipantGameEntity acceptPartEntity = ParticipantGameEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    ParticipantGameEntity kickoutPartEntity = ParticipantGameEntity.builder()
        .id(2L)
        .status(KICKOUT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .acceptedDateTime(LocalDateTime.of(2024, 10, 10, 12, 30, 0))
        .kickoutDateTime(LocalDateTime.of(2024, 10, 10, 12, 40, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    getCurrentUser();

    when(participantGameRepository.findByIdAndStatus(anyLong(), eq(ACCEPT)))
        .thenReturn(Optional.ofNullable(acceptPartEntity));

    when(gameRepository.findByIdAndDeletedDateTimeNull
        (anyLong())).thenReturn(Optional.ofNullable(createdGameEntity));

    when(participantGameRepository.save(any()))
        .thenReturn(kickoutPartEntity);

    // when
    KickoutResponse response =
        participantGameService.kickoutParticipant(request);

    // Then
    assertEquals(kickoutPartEntity.getId(),
        response.getParticipantId());
    assertEquals(kickoutPartEntity.getStatus(), response.getStatus());
    assertEquals(kickoutPartEntity.getUserEntity().getId(),
        response.getUserId());
  }


  @Test
  @DisplayName("경기 참가자 강퇴 실패 : 경기가 이미 시작 함")
  void kickoutParticipant_failIfGameAlreadyStarted() {
    // Given
    KickoutRequest request = KickoutRequest.builder()
        .participantId(2L)
        .build();

    createdGameEntity = GameEntity.builder()
        .id(1L)
        .startDateTime(LocalDateTime.now().minusHours(1))
        .user(createdUser)
        .build();

    ParticipantGameEntity acceptPartEntity = ParticipantGameEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    getCurrentUser();

    getCurrentUser();

    when(participantGameRepository.findByIdAndStatus(anyLong(), eq(ACCEPT)))
        .thenReturn(Optional.ofNullable(acceptPartEntity));

    when(gameRepository.findByIdAndDeletedDateTimeNull
        (anyLong())).thenReturn(Optional.ofNullable(createdGameEntity));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      participantGameService.kickoutParticipant(request);
    });

    // Then
    assertEquals(ErrorCode.ALREADY_GAME_START, exception.getErrorCode());
  }

  private void getCurrentUser() {
    when(jwtTokenExtract.currentUser()).thenReturn(createdUser);

    when(userRepository.findById(anyLong())).thenReturn(
        Optional.ofNullable(createdUser));
  }

}
package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.KICKOUT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.REJECT;
import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.AcceptRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.KickoutRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.RejectRequest;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.security.JwtTokenExtract;
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

@ExtendWith(MockitoExtension.class)
class ParticipantGameServiceTest {

  @InjectMocks
  private ParticipantGameService participantGameService;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ParticipantGameRepository participantGameRepository;

  @Mock
  private GameRepository gameRepository;

  private UserEntity createdUser;
  private UserEntity applyedUser;

  private GameEntity createdGameEntity;

  private ParticipantGameEntity creatorParticipantGameEntity;

  @BeforeEach
  void setUp() {
    createdUser = UserEntity.builder()
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
    applyedUser = UserEntity.builder()
        .userId(2L)
        .id("test1")
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
        .userEntity(createdUser)
        .build();
    creatorParticipantGameEntity = ParticipantGameEntity.builder()
        .participantId(1L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(createdUser)
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

    when(gameRepository.findByGameIdAndDeletedDateTimeNull(anyLong()))
        .thenReturn(Optional.ofNullable(createdGameEntity));

    when(participantGameRepository.findByStatusAndGameEntityGameId
        (eq(ACCEPT), anyLong())).thenReturn(participantList);

    // when
    List<DetailResponse> result = participantGameService
        .getParticipantList(gameId);

    // Then
    assertThat(result).containsExactlyElementsOf(detailResponseList);
  }

  @Test
  @DisplayName("경기 참가 희망자 수락")
  void acceptParticipant_success() {
    // Given
    Long gameId = 1L;

    AcceptRequest request = AcceptRequest.builder()
        .participantId(2L)
        .build();

    ParticipantGameEntity applyPartEntity = ParticipantGameEntity.builder()
        .participantId(2L)
        .status(APPLY)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    ParticipantGameEntity acceptPartEntity = ParticipantGameEntity.builder()
        .participantId(2L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .acceptedDateTime(LocalDateTime.of(2024, 10, 10, 12, 30, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    Long count = 1L;

    getCurrentUser();

    when(participantGameRepository.findByParticipantIdAndStatus(anyLong(), eq(APPLY)))
        .thenReturn(Optional.ofNullable(applyPartEntity));

    when(gameRepository.findByGameIdAndDeletedDateTimeNull
        (anyLong())).thenReturn(Optional.ofNullable(createdGameEntity));

    when(participantGameRepository.countByStatusAndGameEntityGameId
        (eq(ACCEPT), anyLong())).thenReturn(count);

    when(participantGameRepository.save(any()))
        .thenReturn(acceptPartEntity);

    ArgumentCaptor<ParticipantGameEntity> participantGameEntityArgumentCaptor
        = ArgumentCaptor.forClass(ParticipantGameEntity.class);

    // when
    participantGameService.acceptParticipant(request);

    // Then
    verify(participantGameRepository)
        .save(participantGameEntityArgumentCaptor.capture());

    ParticipantGameEntity result
        = participantGameEntityArgumentCaptor.getValue();

    assertEquals(acceptPartEntity.getParticipantId(),
        result.getParticipantId());
    assertEquals(acceptPartEntity.getStatus(), result.getStatus());
    assertEquals(acceptPartEntity.getCreatedDateTime(),
        result.getCreatedDateTime());
    assertEquals(acceptPartEntity.getGameEntity().getGameId(),
        result.getGameEntity().getGameId());
    assertEquals(acceptPartEntity.getUserEntity().getUserId(),
        result.getUserEntity().getUserId());
  }

  @Test
  @DisplayName("경기 참가 희망자 거절")
  void rejectParticipant_success() {
    // Given
    Long gameId = 1L;

    RejectRequest request = RejectRequest.builder()
        .participantId(2L)
        .build();

    ParticipantGameEntity applyPartEntity = ParticipantGameEntity.builder()
        .participantId(2L)
        .status(APPLY)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    ParticipantGameEntity rejectPartEntity = ParticipantGameEntity.builder()
        .participantId(2L)
        .status(REJECT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .rejectedDateTime(LocalDateTime.of(2024, 10, 10, 12, 30, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    getCurrentUser();

    when(participantGameRepository.findByParticipantIdAndStatus(anyLong(), eq(APPLY)))
        .thenReturn(Optional.ofNullable(applyPartEntity));

    when(gameRepository.findByGameIdAndDeletedDateTimeNull
        (anyLong())).thenReturn(Optional.ofNullable(createdGameEntity));

    when(participantGameRepository.save(any()))
        .thenReturn(rejectPartEntity);

    ArgumentCaptor<ParticipantGameEntity> participantGameEntityArgumentCaptor
        = ArgumentCaptor.forClass(ParticipantGameEntity.class);

    // when
    participantGameService.rejectParticipant(request);

    // Then
    verify(participantGameRepository)
        .save(participantGameEntityArgumentCaptor.capture());

    ParticipantGameEntity result
        = participantGameEntityArgumentCaptor.getValue();

    assertEquals(rejectPartEntity.getParticipantId(),
        result.getParticipantId());
    assertEquals(rejectPartEntity.getStatus(), result.getStatus());
    assertEquals(rejectPartEntity.getCreatedDateTime(),
        result.getCreatedDateTime());
    assertEquals(rejectPartEntity.getGameEntity().getGameId(),
        result.getGameEntity().getGameId());
    assertEquals(rejectPartEntity.getUserEntity().getUserId(),
        result.getUserEntity().getUserId());
  }

  @Test
  @DisplayName("경기 참가자 강퇴")
  void kickoutParticipant_success() {
    // Given
    Long gameId = 1L;

    KickoutRequest request = KickoutRequest.builder()
        .participantId(2L)
        .build();

    ParticipantGameEntity applyPartEntity = ParticipantGameEntity.builder()
        .participantId(2L)
        .status(APPLY)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    ParticipantGameEntity rejectPartEntity = ParticipantGameEntity.builder()
        .participantId(2L)
        .status(KICKOUT)
        .createdDateTime(LocalDateTime.of(2024, 10, 10, 12, 0, 0))
        .acceptedDateTime(LocalDateTime.of(2024, 10, 10, 12, 30, 0))
        .kickoutDateTime(LocalDateTime.of(2024, 10, 10, 12, 40, 0))
        .gameEntity(createdGameEntity)
        .userEntity(applyedUser)
        .build();

    getCurrentUser();

    when(participantGameRepository.findByParticipantIdAndStatus(anyLong(), eq(ACCEPT)))
        .thenReturn(Optional.ofNullable(applyPartEntity));

    when(gameRepository.findByGameIdAndDeletedDateTimeNull
        (anyLong())).thenReturn(Optional.ofNullable(createdGameEntity));

    when(participantGameRepository.save(any()))
        .thenReturn(rejectPartEntity);

    ArgumentCaptor<ParticipantGameEntity> participantGameEntityArgumentCaptor
        = ArgumentCaptor.forClass(ParticipantGameEntity.class);

    // when
    participantGameService.kickoutParticipant(request);

    // Then
    verify(participantGameRepository)
        .save(participantGameEntityArgumentCaptor.capture());

    ParticipantGameEntity result
        = participantGameEntityArgumentCaptor.getValue();

    assertEquals(rejectPartEntity.getParticipantId(),
        result.getParticipantId());
    assertEquals(rejectPartEntity.getStatus(), result.getStatus());
    assertEquals(rejectPartEntity.getCreatedDateTime(),
        result.getCreatedDateTime());
    assertEquals(rejectPartEntity.getGameEntity().getGameId(),
        result.getGameEntity().getGameId());
    assertEquals(rejectPartEntity.getUserEntity().getUserId(),
        result.getUserEntity().getUserId());
  }

  private void getCurrentUser() {
    when(jwtTokenExtract.currentUser()).thenReturn(createdUser);

    when(userRepository.findById(anyLong())).thenReturn(
        Optional.ofNullable(createdUser));
  }

}
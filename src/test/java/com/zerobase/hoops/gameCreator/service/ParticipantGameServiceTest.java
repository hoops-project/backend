package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.DetailResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private TokenProvider tokenProvider;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ParticipantGameRepository participantGameRepository;

  @Mock
  private GameRepository gameRepository;

  private UserEntity createdUser;

  private GameEntity createdGameEntity;

  private ParticipantGameEntity creatorParticipantGameEntity;

  private ParticipantGameEntity deletedPartEntity;

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

    when(participantGameRepository.findByStatusAndGameEntityGameId
        (eq(ACCEPT), anyLong())).thenReturn(participantList);

    // when
    List<DetailResponse> result = participantGameService
        .getParticipantList(gameId);

    // Then
    assertThat(result).containsExactlyElementsOf(detailResponseList);

  }

}
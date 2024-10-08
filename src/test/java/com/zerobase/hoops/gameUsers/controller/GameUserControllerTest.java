package com.zerobase.hoops.gameUsers.controller;

import static com.zerobase.hoops.gameCreator.type.CityName.SEOUL;
import static com.zerobase.hoops.gameCreator.type.FieldStatus.INDOOR;
import static com.zerobase.hoops.gameCreator.type.Gender.ALL;
import static com.zerobase.hoops.gameCreator.type.MatchFormat.THREEONTHREE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.commonResponse.ApiResponseFactory;
import com.zerobase.hoops.commonResponse.CustomApiResponse;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.MannerPointEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.dto.MannerPointDto;
import com.zerobase.hoops.gameUsers.dto.ParticipateGameDto;
import com.zerobase.hoops.gameUsers.dto.UserJoinsGameDto;
import com.zerobase.hoops.gameUsers.repository.GameCheckOutRepository;
import com.zerobase.hoops.gameUsers.repository.GameUserRepository;
import com.zerobase.hoops.gameUsers.repository.MannerPointRepository;
import com.zerobase.hoops.gameUsers.service.GameUserService;
import com.zerobase.hoops.manager.service.ManagerService;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.service.UserService;
import com.zerobase.hoops.users.type.GenderType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(GameUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameUserControllerTest {

  @MockBean
  private UserService userService;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GameUserService gameUserService;

  @MockBean
  private TokenProvider tokenProvider;

  @MockBean
  private JwtTokenExtract jwtTokenExtract;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private GameUserRepository gameUserRepository;

  @MockBean
  private MannerPointRepository mannerPointRepository;

  @MockBean
  private ApiResponseFactory apiResponseFactory;

  @MockBean
  private GameCheckOutRepository gameCheckOutRepository;

  @MockBean
  private ManagerService managerService;

  @Autowired
  private ObjectMapper objectMapper;

  @DisplayName("매너점수 평가하기")
  @WithMockUser
  @Test
  void testSaveMannerPointList() throws Exception {
    // Given
    LocalDateTime time = LocalDateTime.now();
    GameEntity gameEntity = new GameEntity();
    gameEntity.setId(1L);
    gameEntity.setTitle("Test Game");
    gameEntity.setStartDateTime(time.minusDays(1));
    gameEntity.setAddress("Test Address");

    UserEntity user = UserEntity.builder()
        .id(1L)
        .gender(GenderType.MALE)
        .build();

    UserEntity receiverUser = UserEntity.builder()
        .id(2L)
        .gender(GenderType.MALE)
        .build();

    MannerPointDto gameForManner = MannerPointDto.builder()
        .receiverId(receiverUser.getId())
        .gameId(gameEntity.getId())
        .point(5)
        .build();

    given(userRepository.findById(1L)).willReturn(Optional.of(user));
    given(userRepository.findById(2L)).willReturn(
        Optional.of(receiverUser));
    given(gameUserRepository.findByIdAndStartDateTimeBefore(1L,
        time.minusDays(1)))
        .willReturn(Optional.of(gameEntity));
    given(mannerPointRepository.existsByUser_IdAndReceiver_IdAndGame_Id(1L,
        2L, 1L))
        .willReturn(false);
    given(mannerPointRepository.save(
        gameForManner.toEntity(user, receiverUser,
            gameEntity))).willReturn(any(MannerPointEntity.class));

    // When
    gameUserService.saveMannerPoint(gameForManner);
    when(apiResponseFactory.createSuccessResponse("매너점수평가"))
        .thenReturn(new CustomApiResponse("매너점수평가", "Success"));

    // Then
    mockMvc.perform(post("/api/game-user/manner-point")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(gameForManner)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.detail").value("Success"));

  }

  @DisplayName("현재 게임 목록 테스트")
  @WithMockUser
  @Test
  void testMyCurrentGameList() throws Exception {

    // Given
    CityName cityName = CityName.SEOUL;
    FieldStatus fieldStatus = FieldStatus.INDOOR;
    Gender gender = Gender.ALL;
    MatchFormat matchFormat = MatchFormat.THREEONTHREE;

    LocalDateTime time = LocalDateTime.now()
        .plusDays(10);
    GameEntity gameEntity = new GameEntity();
    gameEntity.setId(1L);
    gameEntity.setTitle("Test Game");
    gameEntity.setContent("Test Game Content");
    gameEntity.setHeadCount(6L);
    gameEntity.setFieldStatus(fieldStatus);
    gameEntity.setGender(gender);
    gameEntity.setStartDateTime(time.plusDays(1));
    gameEntity.setCreatedDateTime(time);
    gameEntity.setDeletedDateTime(null);
    gameEntity.setInviteYn(true);
    gameEntity.setAddress("Test Address");
    gameEntity.setLatitude(37.5665);
    gameEntity.setLongitude(126.9780);
    gameEntity.setCityName(cityName);
    gameEntity.setMatchFormat(matchFormat);
    UserEntity userEntity = new UserEntity();
    userEntity.setId(1L);
    gameEntity.setUser(userEntity);

    List<GameSearchResponse> gameSearchResponses = Arrays.asList(
        GameSearchResponse.of(gameEntity, userEntity.getId()));
    Page<GameSearchResponse> expectedPage = new PageImpl<>(
        gameSearchResponses);

    // When
    when(gameUserService.myCurrentGameList(1, 1)).thenReturn(
        (expectedPage));

    // Then
    mockMvc.perform(get("/api/game-user/my-current-game-list")
            .param("page", "1")
            .param("size", "1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.content[0].gameId").value(gameEntity.getId()))
        .andExpect(jsonPath("$.content").isArray());
  }


  @DisplayName("과거 게임 목록 테스트")
  @WithMockUser
  @Test
  void testMyLastGameList() throws Exception {
    // Given

    CityName cityName = CityName.SEOUL;
    FieldStatus fieldStatus = FieldStatus.INDOOR;
    Gender gender = Gender.ALL;
    MatchFormat matchFormat = MatchFormat.THREEONTHREE;

    LocalDateTime time = LocalDateTime.of(2024, 5, 6, 23, 54, 32, 8229099);
    GameEntity gameEntity = new GameEntity();
    gameEntity.setId(1L);
    gameEntity.setTitle("Test Game");
    gameEntity.setContent("Test Game Content");
    gameEntity.setHeadCount(6L);
    gameEntity.setFieldStatus(fieldStatus);
    gameEntity.setGender(gender);
    gameEntity.setStartDateTime(time.plusDays(1));
    gameEntity.setCreatedDateTime(time);
    gameEntity.setDeletedDateTime(null);
    gameEntity.setInviteYn(true);
    gameEntity.setAddress("Test Address");
    gameEntity.setLatitude(37.5665);
    gameEntity.setLongitude(126.9780);
    gameEntity.setCityName(cityName);
    gameEntity.setMatchFormat(matchFormat);
    UserEntity userEntity = new UserEntity();
    userEntity.setId(1L);
    gameEntity.setUser(userEntity);
    List<GameSearchResponse> gameSearchResponses = Arrays.asList(
        GameSearchResponse.of(gameEntity, userEntity.getId()));
    Page<GameSearchResponse> expectedPage = new PageImpl<>(
        gameSearchResponses);
    // When
    when(gameUserService.myLastGameList(1, 1)).thenReturn(expectedPage);

    // Then
    mockMvc.perform(get("/api/game-user/my-last-game-list")
            .param("page", "1")
            .param("size", "1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.content[0].gameId").value(gameEntity.getId()))
        .andExpect(jsonPath("$.content").isArray());
  }

  @DisplayName("게임 참가 요청 성공")
  @WithMockUser
  @Test
  void participateInGame_validRequest_shouldSucceed() throws Exception {
    // Given
    Long gameId = 1L;
    UserJoinsGameDto.Request request = new UserJoinsGameDto.Request(
        gameId);
    ParticipateGameDto participateGameDto = ParticipateGameDto.builder()
        .participantId(1L)
        .status(ParticipantGameStatus.APPLY)
        .gameEntity(mock(GameEntity.class))
        .userEntity(mock(UserEntity.class))
        .build();

    // When
    when(gameUserService.participateInGame(gameId)).thenReturn(
        participateGameDto);

    // then
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/game-user/game-in-out")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantGameStatus")
            .value("APPLY"));

  }


  @DisplayName("필터 검색 테스트")
  @WithMockUser
  @Test
  public void testFindFilteredGames() throws Exception {
    // Given
    LocalDate localDate = LocalDate.now();
    CityName cityName = SEOUL;
    FieldStatus fieldStatus = INDOOR;
    Gender gender = ALL;
    MatchFormat matchFormat = THREEONTHREE;
    UserEntity userEntity = new UserEntity();
    userEntity.setId(1L);
    GameEntity gameEntity = new GameEntity();
    gameEntity.setUser(userEntity);

    List<GameSearchResponse> gameSearchResponses = Arrays.asList(
        GameSearchResponse.of(gameEntity, userEntity.getId()));
    Page<GameSearchResponse> expectedPage = new PageImpl<>(
        gameSearchResponses);
    // When
    when(gameUserService.findFilteredGames(any(), any(), any(),
        any(), any(), eq(1), eq(1))).thenReturn(expectedPage);

    // Then
    mockMvc.perform(get("/api/game-user/search")
            .param("localDate", localDate.toString())
            .param("cityName", cityName.toString())
            .param("fieldStatus", fieldStatus.toString())
            .param("gender", gender.toString())
            .param("matchFormat", matchFormat.toString())
            .param("page", "1")
            .param("size", "1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray());
  }

  @DisplayName("필터 검색 데이터 테스트")
  @WithMockUser
  @Test
  public void findFilteredGames_withFilters_shouldReturnFilteredGames()
      throws Exception {
    // Given
    LocalDate localDate = LocalDate.now();
    CityName cityName = CityName.SEOUL;
    FieldStatus fieldStatus = FieldStatus.INDOOR;
    Gender gender = Gender.ALL;
    MatchFormat matchFormat = MatchFormat.THREEONTHREE;

    LocalDateTime time = LocalDateTime.of(2024, 5, 6, 23, 54, 32, 8229099);
    GameEntity gameEntity = new GameEntity();
    gameEntity.setId(1L);
    gameEntity.setTitle("Test Game");
    gameEntity.setContent("Test Game Content");
    gameEntity.setHeadCount(6L);
    gameEntity.setFieldStatus(fieldStatus);
    gameEntity.setGender(gender);
    gameEntity.setStartDateTime(time.plusDays(1));
    gameEntity.setCreatedDateTime(time);
    gameEntity.setDeletedDateTime(null);
    gameEntity.setInviteYn(true);
    gameEntity.setAddress("Test Address");
    gameEntity.setLatitude(37.5665);
    gameEntity.setLongitude(126.9780);
    gameEntity.setCityName(cityName);
    gameEntity.setMatchFormat(matchFormat);
    UserEntity userEntity = new UserEntity();
    userEntity.setId(1L);
    gameEntity.setUser(userEntity);
    List<GameSearchResponse> expectedGames = Arrays.asList(
        GameSearchResponse.of(gameEntity, userEntity.getId()));
    Page<GameSearchResponse> expectedPage = new PageImpl<>(expectedGames);

    // When
    when(gameUserService.findFilteredGames(eq(localDate), eq(cityName),
        eq(fieldStatus), eq(gender), eq(matchFormat), eq(1), eq(1)))
        .thenReturn(expectedPage);

    // Then
    mockMvc.perform(get("/api/game-user/search")
            .param("localDate", localDate.toString())
            .param("cityName", cityName.name())
            .param("fieldStatus", fieldStatus.name())
            .param("gender", gender.name())
            .param("matchFormat", matchFormat.name())
            .param("page", "1")
            .param("size", "1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.content[0].gameId").value(gameEntity.getId()))
        .andExpect(
            jsonPath("$.content[0].title").value(gameEntity.getTitle()))
        .andExpect(jsonPath("$.content[0].content").value(
            gameEntity.getContent()))
        .andExpect(jsonPath("$.content[0].headCount").value(
            gameEntity.getHeadCount()))
        .andExpect(jsonPath("$.content[0].fieldStatus").value(
            gameEntity.getFieldStatus().name()))
        .andExpect(jsonPath("$.content[0].gender").value(
            gameEntity.getGender().name()))
        .andExpect(jsonPath("$.content[0].startDateTime").value(
            gameEntity.getStartDateTime().toString()))
        .andExpect(jsonPath("$.content[0].inviteYn").value(
            gameEntity.getInviteYn()))
        .andExpect(jsonPath("$.content[0].address").value(
            gameEntity.getAddress()))
        .andExpect(jsonPath("$.content[0].latitude").value(
            gameEntity.getLatitude()))
        .andExpect(jsonPath("$.content[0].longitude").value(
            gameEntity.getLongitude()))
        .andExpect(jsonPath("$.content[0].cityName").value(
            gameEntity.getCityName().name()))
        .andExpect(jsonPath("$.content[0].matchFormat").value(
            gameEntity.getMatchFormat().name()));
  }

  @DisplayName("주소 검색 테스트")
  @WithMockUser
  @Test
  void searchAddress_shouldReturnUpcomingGamesForGivenAddress()
      throws Exception {
    // Given
    String address = "123 Example St";
    List<GameSearchResponse> upcomingGames = Arrays.asList(
        GameSearchResponse.builder().gameId(1L).address(address).build(),
        GameSearchResponse.builder().gameId(2L).address(address).build()
    );
    Page<GameSearchResponse> expectedPage = new PageImpl<>(upcomingGames);
    when(gameUserService.searchAddress(eq(address),eq(0),eq(5))).thenReturn(
        expectedPage);

    // When, Then
    mockMvc.perform(get("/api/game-user/search-address")
            .param("address", address)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].gameId").value(1L))
        .andExpect(jsonPath("$.content[0].address").value(address))
        .andExpect(jsonPath("$.content[1].gameId").value(2L))
        .andExpect(jsonPath("$.content[1].address").value(address));
  }
}
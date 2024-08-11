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
import com.zerobase.hoops.commonResponse.BasicApiResponse;
import com.zerobase.hoops.commonResponse.CustomApiResponse;
import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.document.MannerPointDocument;
import com.zerobase.hoops.document.UserDocument;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(GameUserController.class)
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
    OffsetDateTime time = OffsetDateTime.now(ZoneOffset.ofHours(9));
    GameDocument gameDocument = new GameDocument();
    gameDocument.setId("1");
    gameDocument.setTitle("Test Game");
    gameDocument.setStartDateTime(time.minusDays(1));
    gameDocument.setAddress("Test Address");

    UserDocument user = UserDocument.builder()
        .id("1")
        .gender(GenderType.MALE)
        .build();

    UserDocument receiverUser = UserDocument.builder()
        .id("2")
        .gender(GenderType.MALE)
        .build();

    MannerPointDto gameForManner = MannerPointDto.builder()
        .receiverId(receiverUser.getId())
        .gameId(gameDocument.getId())
        .point(5)
        .build();

    given(userRepository.findById("1")).willReturn(Optional.of(user));
    given(userRepository.findById("2")).willReturn(
        Optional.of(receiverUser));
    given(gameUserRepository.findByIdAndStartDateTimeBefore("1",
        time.minusDays(1)))
        .willReturn(Optional.of(gameDocument));
    given(mannerPointRepository.existsByUser_IdAndReceiver_IdAndGame_Id("1",
        "2", "1"))
        .willReturn(false);
    given(mannerPointRepository.save(
        gameForManner.toDocument(user, receiverUser,
            gameDocument, 1))).willReturn(any(MannerPointDocument.class));

    // When
    gameUserService.saveMannerPoint(gameForManner);
    when(apiResponseFactory.createSuccessResponse("매너점수평가"))
        .thenReturn(new CustomApiResponse("매너점수평가", "Success"));

    // Then
    mockMvc.perform(post("/api/game-user/manner-point")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(gameForManner)))
        //.andDo(print())
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

    OffsetDateTime time = OffsetDateTime.now(ZoneOffset.ofHours(9)).plusDays(10);

    GameDocument gameDocument = new GameDocument();
    gameDocument.setId("1");
    gameDocument.setTitle("Test Game");
    gameDocument.setContent("Test Game Content");
    gameDocument.setHeadCount(6L);
    gameDocument.setFieldStatus(fieldStatus);
    gameDocument.setGender(gender);
    gameDocument.setStartDateTime(time.plusDays(1));
    gameDocument.setCreatedDateTime(time);
    gameDocument.setDeletedDateTime(null);
    gameDocument.setInviteYn(true);
    gameDocument.setAddress("Test Address");
    gameDocument.setLatitude(37.5665);
    gameDocument.setLongitude(126.9780);
    gameDocument.setCityName(cityName);
    gameDocument.setMatchFormat(matchFormat);
    UserDocument userDocument = new UserDocument();
    userDocument.setId("1");
    gameDocument.setUser(userDocument);

    List<GameSearchResponse> gameSearchResponses = Arrays.asList(
        GameSearchResponse.of(gameDocument, userDocument.getId()));
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
            jsonPath("$.content[0].gameId").value(gameDocument.getId()))
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

    OffsetDateTime time = OffsetDateTime
        .of(LocalDateTime.of(2024, 5, 6, 23, 54, 32, 8229099), ZoneOffset.ofHours(9));

    GameDocument gameDocument = new GameDocument();
    gameDocument.setId("1");
    gameDocument.setTitle("Test Game");
    gameDocument.setContent("Test Game Content");
    gameDocument.setHeadCount(6L);
    gameDocument.setFieldStatus(fieldStatus);
    gameDocument.setGender(gender);
    gameDocument.setStartDateTime(time.plusDays(1));
    gameDocument.setCreatedDateTime(time);
    gameDocument.setDeletedDateTime(null);
    gameDocument.setInviteYn(true);
    gameDocument.setAddress("Test Address");
    gameDocument.setLatitude(37.5665);
    gameDocument.setLongitude(126.9780);
    gameDocument.setCityName(cityName);
    gameDocument.setMatchFormat(matchFormat);
    UserDocument userDocument = new UserDocument();
    userDocument.setId("1");
    gameDocument.setUser(userDocument);
    List<GameSearchResponse> gameSearchResponses = Arrays.asList(
        GameSearchResponse.of(gameDocument, userDocument.getId()));
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
            jsonPath("$.content[0].gameId").value(gameDocument.getId()))
        .andExpect(jsonPath("$.content").isArray());
  }

  @DisplayName("게임 참가 요청 성공")
  @WithMockUser
  @Test
  void participateInGame_validRequest_shouldSucceed() throws Exception {
    // Given
    String gameId = "1";
    UserJoinsGameDto.Request request = new UserJoinsGameDto.Request(
        gameId);
    ParticipateGameDto participateGameDto = ParticipateGameDto.builder()
        .participantId("1")
        .status(ParticipantGameStatus.APPLY)
        .gameDocument(mock(GameDocument.class))
        .userDocument(mock(UserDocument.class))
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
    UserDocument userDocument = new UserDocument();
    userDocument.setId("1");
    GameDocument gameDocument = new GameDocument();
    gameDocument.setUser(userDocument);

    List<GameSearchResponse> gameSearchResponses = Arrays.asList(
        GameSearchResponse.of(gameDocument, userDocument.getId()));
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

    OffsetDateTime time = OffsetDateTime
        .of(LocalDateTime.of(2024, 5, 6, 23, 54, 32, 8229099), ZoneOffset.ofHours(9));
    GameDocument gameDocument = new GameDocument();
    gameDocument.setId("1");
    gameDocument.setTitle("Test Game");
    gameDocument.setContent("Test Game Content");
    gameDocument.setHeadCount(6L);
    gameDocument.setFieldStatus(fieldStatus);
    gameDocument.setGender(gender);
    gameDocument.setStartDateTime(time.plusDays(1));
    gameDocument.setCreatedDateTime(time);
    gameDocument.setDeletedDateTime(null);
    gameDocument.setInviteYn(true);
    gameDocument.setAddress("Test Address");
    gameDocument.setLatitude(37.5665);
    gameDocument.setLongitude(126.9780);
    gameDocument.setCityName(cityName);
    gameDocument.setMatchFormat(matchFormat);
    UserDocument userDocument = new UserDocument();
    userDocument.setId("1");
    gameDocument.setUser(userDocument);
    List<GameSearchResponse> expectedGames = Arrays.asList(
        GameSearchResponse.of(gameDocument, userDocument.getId()));
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
            jsonPath("$.content[0].gameId").value(gameDocument.getId()))
        .andExpect(
            jsonPath("$.content[0].title").value(gameDocument.getTitle()))
        .andExpect(jsonPath("$.content[0].content").value(
            gameDocument.getContent()))
        .andExpect(jsonPath("$.content[0].headCount").value(
            gameDocument.getHeadCount()))
        .andExpect(jsonPath("$.content[0].fieldStatus").value(
            gameDocument.getFieldStatus().name()))
        .andExpect(jsonPath("$.content[0].gender").value(
            gameDocument.getGender().name()))
        .andExpect(jsonPath("$.content[0].startDateTime").value(
            gameDocument.getStartDateTime().toString()))
        .andExpect(jsonPath("$.content[0].inviteYn").value(
            gameDocument.getInviteYn()))
        .andExpect(jsonPath("$.content[0].address").value(
            gameDocument.getAddress()))
        .andExpect(jsonPath("$.content[0].latitude").value(
            gameDocument.getLatitude()))
        .andExpect(jsonPath("$.content[0].longitude").value(
            gameDocument.getLongitude()))
        .andExpect(jsonPath("$.content[0].cityName").value(
            gameDocument.getCityName().name()))
        .andExpect(jsonPath("$.content[0].matchFormat").value(
            gameDocument.getMatchFormat().name()));
  }

  @DisplayName("주소 검색 테스트")
  @WithMockUser
  @Test
  void searchAddress_shouldReturnUpcomingGamesForGivenAddress()
      throws Exception {
    // Given
    String address = "123 Example St";
    List<GameSearchResponse> upcomingGames = Arrays.asList(
        GameSearchResponse.builder().gameId("1").address(address).build(),
        GameSearchResponse.builder().gameId("2").address(address).build()
    );
    when(gameUserService.searchAddress(address)).thenReturn(upcomingGames);

    // When, Then
    mockMvc.perform(get("/api/game-user/search-address")
            .param("address", address)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].gameId").value("1"))
        .andExpect(jsonPath("$[0].address").value(address))
        .andExpect(jsonPath("$[1].gameId").value("2"))
        .andExpect(jsonPath("$[1].address").value(address));
  }
}
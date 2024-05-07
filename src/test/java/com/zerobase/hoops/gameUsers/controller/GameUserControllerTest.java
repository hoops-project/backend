package com.zerobase.hoops.gameUsers.controller;

import static com.zerobase.hoops.gameCreator.type.CityName.SEOUL;
import static com.zerobase.hoops.gameCreator.type.FieldStatus.INDOOR;
import static com.zerobase.hoops.gameCreator.type.Gender.ALL;
import static com.zerobase.hoops.gameCreator.type.MatchFormat.THREEONTHREE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.dto.ParticipateGameDto;
import com.zerobase.hoops.gameUsers.dto.UserJoinsGameDto;
import com.zerobase.hoops.gameUsers.service.GameUserService;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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

  @Autowired
  private ObjectMapper objectMapper;

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
    userEntity.setUserId(1L);
    GameEntity gameEntity = new GameEntity();
    gameEntity.setUserEntity(userEntity);

    List<GameSearchResponse> gameSearchResponses = Arrays.asList(
        GameSearchResponse.of(gameEntity,userEntity.getUserId()));

    // When
    when(gameUserService.findFilteredGames(any(), any(), any(),
        any(), any())).thenReturn(gameSearchResponses);

    // Then
    mockMvc.perform(get("/api/game-user/search")
            .param("localDate", localDate.toString())
            .param("cityName", cityName.toString())
            .param("fieldStatus", fieldStatus.toString())
            .param("gender", gender.toString())
            .param("matchFormat", matchFormat.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray());
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
    gameEntity.setGameId(1L);
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
    userEntity.setUserId(1L);
    gameEntity.setUserEntity(userEntity);
    List<GameSearchResponse> expectedGames = Arrays.asList(
        GameSearchResponse.of(gameEntity, userEntity.getUserId()));

    // When
    when(gameUserService.findFilteredGames(eq(localDate), eq(cityName),
        eq(fieldStatus), eq(gender), eq(matchFormat)))
        .thenReturn(expectedGames);

    // Then
    mockMvc.perform(get("/api/game-user/search")
            .param("localDate", localDate.toString())
            .param("cityName", cityName.name())
            .param("fieldStatus", fieldStatus.name())
            .param("gender", gender.name())
            .param("matchFormat", matchFormat.name())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(expectedGames.size()))
        .andExpect(jsonPath("$[0].gameId").value(gameEntity.getGameId()))
        .andExpect(jsonPath("$[0].title").value(gameEntity.getTitle()))
        .andExpect(jsonPath("$[0].content").value(gameEntity.getContent()))
        .andExpect(
            jsonPath("$[0].headCount").value(gameEntity.getHeadCount()))
        .andExpect(jsonPath("$[0].fieldStatus").value(
            gameEntity.getFieldStatus().name()))
        .andExpect(
            jsonPath("$[0].gender").value(gameEntity.getGender().name()))
        .andExpect(jsonPath("$[0].startDateTime").value(
            gameEntity.getStartDateTime().toString()))
        .andExpect(
            jsonPath("$[0].inviteYn").value(gameEntity.getInviteYn()))
        .andExpect(jsonPath("$[0].address").value(gameEntity.getAddress()))
        .andExpect(
            jsonPath("$[0].latitude").value(gameEntity.getLatitude()))
        .andExpect(
            jsonPath("$[0].longitude").value(gameEntity.getLongitude()))
        .andExpect(jsonPath("$[0].cityName").value(
            gameEntity.getCityName().name()))
        .andExpect(jsonPath("$[0].matchFormat").value(
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
    when(gameUserService.searchAddress(address)).thenReturn(upcomingGames);

    // When, Then
    mockMvc.perform(get("/api/game-user/search-address")
            .param("address", address)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].gameId").value(1L))
        .andExpect(jsonPath("$[0].address").value(address))
        .andExpect(jsonPath("$[1].gameId").value(2L))
        .andExpect(jsonPath("$[1].address").value(address));
  }
}
package com.zerobase.hoops.gameUsers.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.service.GameUserService;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

  @WithMockUser
  @Test
  void searchMain_shouldReturnAllUpcomingGames() throws Exception {
    // Given
    List<GameSearchResponse> upcomingGames = Arrays.asList(
        GameSearchResponse.builder().gameId(1L).build(),
        GameSearchResponse.builder().gameId(2L).build()
    );
    when(gameUserService.searchMain()).thenReturn(upcomingGames);

    // When, Then
    mockMvc.perform(get("/api/game-user/search").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].gameId").value(1L))
        .andExpect(jsonPath("$[1].gameId").value(2L))
        .andExpect(header().doesNotExist("Location"));
  }

  @WithMockUser
  @Test
  void searchCity_shouldReturnUpcomingGamesForGivenCity()
      throws Exception {
    // Given
    CityName cityName = CityName.SEOUL;
    List<GameSearchResponse> upcomingGames = Arrays.asList(
        GameSearchResponse.builder().gameId(1L).cityName(cityName).build(),
        GameSearchResponse.builder().gameId(2L).cityName(cityName).build()
    );
    when(gameUserService.searchCity(cityName)).thenReturn(upcomingGames);

    // When, Then
    mockMvc.perform(get("/api/game-user/search-city")
            .param("cityName", cityName.name())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].gameId").value(1L))
        .andExpect(jsonPath("$[0].cityName").value(cityName.name()))
        .andExpect(jsonPath("$[1].gameId").value(2L))
        .andExpect(jsonPath("$[1].cityName").value(cityName.name()));
  }

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
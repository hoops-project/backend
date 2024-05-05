package com.zerobase.hoops.gameUsers.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.repository.GameUserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GameUserServiceTest {

  @Mock
  private GameUserRepository gameUserRepository;

  @InjectMocks
  private GameUserService gameUserService;

  @Test
  void searchMain_shouldReturnAllUpcomingGames() {
    // Given
    List<GameEntity> upcomingGames = Arrays.asList(
        GameEntity.builder().gameId(1L)
            .startDateTime(LocalDateTime.now().plusHours(1)).build(),
        GameEntity.builder().gameId(2L)
            .startDateTime(LocalDateTime.now().plusDays(2)).build()
    );
    when(gameUserRepository.findAllFromDateToday()).thenReturn(
        upcomingGames);

    // When
    List<GameSearchResponse> result = gameUserService.searchMain();

    // Then
    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).getGameId());
    assertEquals(2L, result.get(1).getGameId());
  }

  @Test
  void searchCity_shouldReturnUpcomingGamesForGivenCity() {
    // Given
    CityName cityName = CityName.SEOUL;
    LocalDateTime now = LocalDateTime.now();
    List<GameEntity> upcomingGames = Arrays.asList(
        GameEntity.builder().gameId(1L).cityName(cityName)
            .startDateTime(now.plusDays(1)).build(),
        GameEntity.builder().gameId(2L).cityName(cityName)
            .startDateTime(now.plusDays(7)).build()
    );
    when(gameUserRepository.findByCityNameAndStartDateTimeAfterOrderByStartDateTimeAsc(eq(cityName), any(LocalDateTime.class)))
        .thenReturn(upcomingGames);

    // When
    List<GameSearchResponse> result = gameUserService.searchCity(cityName);

    // Then
    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).getGameId());
    assertEquals(2L, result.get(1).getGameId());
  }

  @Test
  void searchAddress_shouldReturnUpcomingGamesForGivenAddress() {
    // Given
    String address = "123 Example St";
    List<GameEntity> upcomingGames = Arrays.asList(
        GameEntity.builder().gameId(1L).address(address)
            .startDateTime(LocalDateTime.now().plusHours(1)).build(),
        GameEntity.builder().gameId(2L).address(address)
            .startDateTime(LocalDateTime.now().plusDays(2)).build()
    );
    when(
        gameUserRepository.findByAddressContainingAndStartDateTimeAfterOrderByStartDateTimeAsc(
            eq(address), any(LocalDateTime.class))).thenReturn(upcomingGames);

    // When
    List<GameSearchResponse> result = gameUserService.searchAddress(
        address);

    // Then
    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).getGameId());
    assertEquals(2L, result.get(1).getGameId());
  }
}
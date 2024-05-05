package com.zerobase.hoops.gameUsers.service;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.repository.GameUserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GameUserService {

  private final GameUserRepository gameUserRepository;


  public List<GameSearchResponse> searchMain() {
    List<GameEntity> allFromDateToday = gameUserRepository.findAllFromDateToday();
    List<GameSearchResponse> gameList = new ArrayList<>();
    allFromDateToday
        .forEach((e) -> gameList.add(GameSearchResponse.of(e)));
    return gameList;
  }

  public List<GameSearchResponse> searchCity(CityName cityName) {
    List<GameEntity> allFromDateToday =
        gameUserRepository.findByCityNameAndStartDateTimeAfterOrderByStartDateTimeAsc(
            cityName, LocalDateTime.now());
    List<GameSearchResponse> gameList = new ArrayList<>();
    allFromDateToday
        .forEach((e) -> gameList.add(GameSearchResponse.of(e)));
    return gameList;
  }

  public List<GameSearchResponse> searchAddress(String address) {
    List<GameEntity> allFromDateToday =
        gameUserRepository.findByAddressContainingAndStartDateTimeAfterOrderByStartDateTimeAsc(
            address, LocalDateTime.now());
    List<GameSearchResponse> gameList = new ArrayList<>();
    allFromDateToday
        .forEach((e) -> gameList.add(GameSearchResponse.of(e)));

    return gameList;
  }
}

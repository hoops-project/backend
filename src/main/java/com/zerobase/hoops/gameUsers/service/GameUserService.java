package com.zerobase.hoops.gameUsers.service;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.repository.GameSpecifications;
import com.zerobase.hoops.gameUsers.repository.GameUserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GameUserService {

  private final GameUserRepository gameUserRepository;

  public List<GameSearchResponse> findFilteredGames(
      LocalDate localDate,
      CityName cityName,
      FieldStatus fieldStatus,
      Gender gender,
      MatchFormat matchFormat) {
    Specification<GameEntity> spec = getGameEntitySpecification(
        localDate, cityName, fieldStatus, gender, matchFormat);

    List<GameEntity> gameListNow = gameUserRepository.findAll(spec);

    return getGameSearchResponses(gameListNow);
  }

  private static Specification<GameEntity> getGameEntitySpecification(
      LocalDate localDate, CityName cityName, FieldStatus fieldStatus,
      Gender gender, MatchFormat matchFormat) {
    Specification<GameEntity> spec = Specification.where(
        GameSpecifications.startDate(LocalDate.now())
            .and(GameSpecifications.notDeleted()));

    if (localDate != null) {
      spec = Specification.where(
          GameSpecifications.withDate(localDate)
              .and(GameSpecifications.notDeleted()));
    }
    if (cityName != null) {
      spec = spec.and(GameSpecifications.withCityName(cityName));
    }
    if (fieldStatus != null) {
      spec = spec.and(GameSpecifications.withFieldStatus(fieldStatus));
    }
    if (gender != null) {
      spec = spec.and(GameSpecifications.withGender(gender));
    }
    if (matchFormat != null) {
      spec = spec.and(GameSpecifications.withMatchFormat(matchFormat));
    }
    return spec;
  }

  public List<GameSearchResponse> searchAddress(String address) {
    List<GameEntity> allFromDateToday =
        gameUserRepository.findByAddressContainingIgnoreCaseAndStartDateTimeAfterOrderByStartDateTimeAsc(
            address, LocalDateTime.now());

    return getGameSearchResponses(allFromDateToday);
  }

  private static List<GameSearchResponse> getGameSearchResponses(
      List<GameEntity> gameListNow) {
    List<GameSearchResponse> gameList = new ArrayList<>();
    gameListNow
        .forEach((e) -> gameList.add(GameSearchResponse.of(e)));
    return gameList;
  }
}

package com.zerobase.hoops.gameUsers.service;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.dto.ParticipateGameDto;
import com.zerobase.hoops.gameUsers.repository.GameCheckOutRepository;
import com.zerobase.hoops.gameUsers.repository.GameUserRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.GenderType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class GameUserService {

  private final GameCheckOutRepository gameCheckOutRepository;
  private final GameUserRepository gameUserRepository;
  private final UserRepository userRepository;
  private final JwtTokenExtract jwtTokenExtract;

  public Page<GameSearchResponse> myCurrentGameList(int size) {
    List<ParticipantGameEntity> userGameList = checkMyGameList();

    List<GameEntity> games = userGameList.stream()
        .map(ParticipantGameEntity::getGameEntity)
        .filter(
            game -> game.getStartDateTime().isAfter(LocalDateTime.now()))
        .toList();

    Long userId = jwtTokenExtract.currentUser().getUserId();
    return getPageGameSearchResponses(games, userId, size);
  }

  public List<GameSearchResponse> myLastGameList() {
    List<ParticipantGameEntity> userGameList = checkMyGameList();

    List<GameEntity> games = userGameList.stream()
        .map(ParticipantGameEntity::getGameEntity)
        .filter(
            game -> game.getStartDateTime().isBefore(LocalDateTime.now()))
        .toList();

    Long userId = jwtTokenExtract.currentUser().getUserId();

    return getGameSearchResponses(games, userId);
  }

  public List<GameSearchResponse> findFilteredGames(
      LocalDate localDate,
      CityName cityName,
      FieldStatus fieldStatus,
      Gender gender,
      MatchFormat matchFormat) {

    Specification<GameEntity> spec = getGameEntitySpecification(
        localDate, cityName, fieldStatus, gender, matchFormat);

    List<GameEntity> gameListNow = gameUserRepository.findAll(spec);

    Long userId = null;

    return getGameSearchResponses(gameListNow, userId);
  }

  public List<GameSearchResponse> searchAddress(String address) {
    List<GameEntity> allFromDateToday =
        gameUserRepository.findByAddressContainingIgnoreCaseAndStartDateTimeAfterOrderByStartDateTimeAsc(
            address, LocalDateTime.now());

    Long userId = null;

    return getGameSearchResponses(allFromDateToday, userId);
  }

  private static Page<GameSearchResponse> getPageGameSearchResponses(
      List<GameEntity> gameListNow, Long userId, int size) {
    List<GameSearchResponse> gameList = new ArrayList<>();
    gameListNow.forEach(
        (e) -> gameList.add(GameSearchResponse.of(e, userId)));

    int start = 0;
    int end = Math.min(size, gameList.size());

    List<GameSearchResponse> pageContent = gameList.subList(0, end);
    PageRequest pageable = PageRequest.of(start, end);
    return new PageImpl<>(pageContent, pageable, gameList.size());
  }

  private static List<GameSearchResponse> getGameSearchResponses(
      List<GameEntity> gameListNow, Long userId) {
    List<GameSearchResponse> gameList = new ArrayList<>();
    gameListNow.forEach(
        (e) -> gameList.add(GameSearchResponse.of(e, userId)));
    return gameList;
  }

  @Transactional
  public ParticipateGameDto participateInGame(Long gameId) {
    Long userId = jwtTokenExtract.currentUser().getUserId();

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    GameEntity game = gameUserRepository.findById(gameId)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    checkValidated(gameId, game, user);

    return ParticipateGameDto.fromEntity(gameCheckOutRepository.save(
        ParticipantGameEntity.builder()
            .status(ParticipantGameStatus.APPLY)
            .gameEntity(game)
            .userEntity(user)
            .build()));
  }

  private void checkValidated(Long gameId, GameEntity game,
      UserEntity user) {
    if (gameCheckOutRepository.existsByGameEntity_GameIdAndUserEntity_UserId(
        gameId,
        user.getUserId())) {
      throw new CustomException(ErrorCode.DUPLICATED_TRY_TO_JOIN_GAME);
    }
    if (gameCheckOutRepository.countByStatusAndGameEntityGameId(
        ParticipantGameStatus.ACCEPT, gameId) >= game.getHeadCount()) {
      throw new CustomException(ErrorCode.FULL_PEOPLE_GAME);
    }
    if (game.getStartDateTime().isBefore(LocalDateTime.now())) {
      throw new CustomException(ErrorCode.OVER_TIME_GAME);
    }
    if (game.getGender().equals(Gender.FEMALEONLY) && user.getGender()
        .equals(GenderType.MALE)) {
      throw new CustomException(ErrorCode.ONLY_FEMALE_GAME);
    } else if (game.getGender().equals(Gender.MALEONLY) && user.getGender()
        .equals(GenderType.FEMALE)) {
      throw new CustomException(ErrorCode.ONLY_MALE_GAME);
    }
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

  private List<ParticipantGameEntity> checkMyGameList() {
    Long userId = jwtTokenExtract.currentUser().getUserId();

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    return gameCheckOutRepository.findByUserEntity_UserIdAndStatus(
            user.getUserId(), ParticipantGameStatus.ACCEPT)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
  }

}


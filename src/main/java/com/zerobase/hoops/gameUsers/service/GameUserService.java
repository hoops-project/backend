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
import com.zerobase.hoops.gameUsers.dto.MannerPointDto;
import com.zerobase.hoops.gameUsers.dto.MannerPointListResponse;
import com.zerobase.hoops.gameUsers.dto.ParticipateGameDto;
import com.zerobase.hoops.gameUsers.repository.GameCheckOutRepository;
import com.zerobase.hoops.gameUsers.repository.GameCheckOutSpecifications;
import com.zerobase.hoops.gameUsers.repository.GameUserRepository;
import com.zerobase.hoops.gameUsers.repository.MannerPointRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.GenderType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  private final MannerPointRepository mannerPointRepository;
  private final UserRepository userRepository;
  private final JwtTokenExtract jwtTokenExtract;

  @Transactional
  public void saveMannerPoint(MannerPointDto request) {

    Long userId = jwtTokenExtract.currentUser().getId();
    Long receiverId = request.getReceiverId();
    Long gameId = request.getGameId();

    UserEntity userEntity = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    UserEntity receiverEntity = userRepository.findById(
            receiverId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    GameEntity gameEntity = gameUserRepository.findByIdAndStartDateTimeBefore(
            gameId, LocalDateTime.now())
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    checkExistRate(request, userId, gameId);
    receiverEntity.saveMannerPoint(request.getPoint());
    mannerPointRepository.save(
        request.toEntity(userEntity, receiverEntity, gameEntity));
  }

  private void checkExistRate(MannerPointDto request, Long userId,
      Long gameId) {
    boolean checking = mannerPointRepository.existsByUser_IdAndReceiver_IdAndGame_Id(
        userId, request.getReceiverId(), gameId);

    if (checking) {
      throw new CustomException(ErrorCode.EXIST_RATE);
    }
  }


  public List<MannerPointListResponse> getMannerPoint(
      String gameId) {

    List<ParticipantGameEntity> userList = checkMannerPointList(gameId);
    List<MannerPointListResponse> mannerPointUserList = new ArrayList<>();

    Long currentUserId = jwtTokenExtract.currentUser().getId();
    
    userList.stream()
        .filter(user -> !user.getUserEntity().getId().equals(currentUserId))
        .forEach(user -> mannerPointUserList.add(MannerPointListResponse.of(user)));

    return mannerPointUserList;
  }

  private List<ParticipantGameEntity> checkMannerPointList(
      String gameId) {
    Long userId = jwtTokenExtract.currentUser().getId();
    userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    Long gameLongId = Long.valueOf(gameId);

    gameUserRepository.findByIdAndStartDateTimeBefore(
            gameLongId, LocalDateTime.now())
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    boolean finalCheck = gameCheckOutRepository.existsByGameEntity_IdAndUserEntity_IdAndStatus(
        gameLongId, userId, ParticipantGameStatus.ACCEPT);

    if (!finalCheck) {
      throw new CustomException(ErrorCode.GAME_NOT_FOUND);
    }
    return gameCheckOutRepository.findByStatusAndGameEntity_Id(
            ParticipantGameStatus.ACCEPT, gameLongId)
        .orElseThrow(
            () -> new CustomException(ErrorCode.GAME_NOT_FOUND));
  }


  public Page<GameSearchResponse> myCurrentGameList(int page, int size) {
    List<ParticipantGameEntity> userGameList = checkMyGameList();

    List<GameEntity> games = userGameList.stream()
        .map(ParticipantGameEntity::getGameEntity)
        .filter(
            game -> game.getStartDateTime().isAfter(LocalDateTime.now()))
        .toList();

    Long userId = jwtTokenExtract.currentUser().getId();
    return getPageGameSearchResponses(games, userId, page, size);
  }

  public Page<GameSearchResponse> myLastGameList(int page, int size) {
    List<ParticipantGameEntity> userGameList = checkMyGameList();

    List<GameEntity> games = userGameList.stream()
        .map(ParticipantGameEntity::getGameEntity)
        .filter(
            game -> game.getStartDateTime().isBefore(LocalDateTime.now()))
        .toList();

    Long userId = jwtTokenExtract.currentUser().getId();

    return getPageGameSearchResponses(games, userId, page, size);
  }

  public Page<GameSearchResponse> findFilteredGames(
      LocalDate localDate, CityName cityName, FieldStatus fieldStatus,
      Gender gender, MatchFormat matchFormat, int page, int size) {

    Specification<GameEntity> spec = getGameEntitySpecification(
        localDate, cityName, fieldStatus, gender, matchFormat);

    List<GameEntity> gameListNow = gameUserRepository.findAll(spec);

    Long userId = null;

    return getPageGameSearchResponses(gameListNow, userId, page, size);
  }

  public List<GameSearchResponse> searchAddress(String address) {
    List<GameEntity> allFromDateToday =
        gameUserRepository.findByAddressContainingIgnoreCaseAndStartDateTimeAfterOrderByStartDateTimeAsc(
            address, LocalDateTime.now());

    Long userId = null;

    return getGameSearchResponses(allFromDateToday, userId);
  }

  private static Page<GameSearchResponse> getPageGameSearchResponses(
      List<GameEntity> gameListNow, Long userId, int page, int size) {
    List<GameSearchResponse> gameList = new ArrayList<>();
    gameListNow.forEach(
        (e) -> gameList.add(GameSearchResponse.of(e, userId)));

    log.info("game list : " + gameList);
    log.info("gameListNow : " + gameListNow);
    int totalSize = gameList.size();
    int totalPages = (int) Math.ceil((double) totalSize / size);
    int lastPage = totalPages == 0 ? 1 : totalPages;

    page = Math.min(page, lastPage);

    int start = (page - 1) * size;
    int end = Math.min(page * size, totalSize);

    List<GameSearchResponse> pageContent = gameList.subList(start, end);
    PageRequest pageable = PageRequest.of(page - 1, size);
    return new PageImpl<>(pageContent, pageable, totalSize);
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
    Long userId = jwtTokenExtract.currentUser().getId();

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
    if (gameCheckOutRepository.existsByGameEntity_IdAndUserEntity_Id(
        gameId,
        user.getId())) {
      throw new CustomException(ErrorCode.DUPLICATED_TRY_TO_JOIN_GAME);
    }
    if (gameCheckOutRepository.countByStatusAndGameEntityId(
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
        GameCheckOutSpecifications.notDeleted());

    spec = spec.and(GameCheckOutSpecifications.startDate(localDate));

    if (cityName != null) {
      spec = spec.and(GameCheckOutSpecifications.withCityName(cityName));
    }
    if (fieldStatus != null) {
      spec = spec.and(
          GameCheckOutSpecifications.withFieldStatus(fieldStatus));
    }
    if (gender != null) {
      spec = spec.and(GameCheckOutSpecifications.withGender(gender));
    }
    if (matchFormat != null) {
      spec = spec.and(
          GameCheckOutSpecifications.withMatchFormat(matchFormat));
    }
    return spec;
  }

  private List<ParticipantGameEntity> checkMyGameList() {
    Long userId = jwtTokenExtract.currentUser().getId();

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    return gameCheckOutRepository.findByUserEntity_IdAndStatus(
            user.getId(), ParticipantGameStatus.ACCEPT)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
  }

}


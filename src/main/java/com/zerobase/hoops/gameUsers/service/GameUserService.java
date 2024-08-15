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
    log.info("saveMannerPoint 시작");
    Long userId = jwtTokenExtract.currentUser().getId();
    Long receiverId = request.getReceiverId();
    Long gameId = request.getGameId();
    log.info(
        String.format("[user_pk] = %s -> [receiverId] = %s / [gameId] = %s",
            userId,
            receiverId,
            gameId));
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
    log.info("saveMannerPoint 종료");
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
    log.info("getMannerPoint 시작");
    Long currentUserId = jwtTokenExtract.currentUser().getId();
    List<ParticipantGameEntity> userList = checkMannerPointList(gameId);
    List<MannerPointListResponse> mannerPointUserList = new ArrayList<>();

    userList.stream()
        .filter(user -> !user.getUser().getId().equals(currentUserId))
        .forEach(user -> mannerPointUserList.add(
            MannerPointListResponse.of(user)));

    log.info(
        String.format("[user_pk] = %s ->  [gameId] = %s / [list] = [%s]",
            currentUserId,
            gameId,
            mannerPointUserList
        ));

    log.info("getMannerPoint 종료");
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

    boolean finalCheck = gameCheckOutRepository.existsByGame_IdAndUser_IdAndStatus(
        gameLongId, userId, ParticipantGameStatus.ACCEPT);

    if (!finalCheck) {
      throw new CustomException(ErrorCode.GAME_NOT_FOUND);
    }
    return gameCheckOutRepository.findByStatusAndGame_Id(
            ParticipantGameStatus.ACCEPT, gameLongId)
        .orElseThrow(
            () -> new CustomException(ErrorCode.GAME_NOT_FOUND));
  }


  public Page<GameSearchResponse> myCurrentGameList(int page, int size) {
    log.info("myCurrentGameList 시작");
    List<ParticipantGameEntity> userGameList = checkMyGameList();

    List<GameEntity> games = userGameList.stream()
        .map(ParticipantGameEntity::getGame)
        .filter(
            game -> game.getStartDateTime().isAfter(LocalDateTime.now()))
        .toList();

    Long userId = jwtTokenExtract.currentUser().getId();

    log.info(
        String.format("[user_pk] = %s ->  [gameList] = [%s]",
            userId,
            games
        ));

    log.info("myCurrentGameList 종료");
    return getPageGameSearchResponses(games, userId, page, size);
  }

  public Page<GameSearchResponse> myLastGameList(int page, int size) {
    log.info("myLastGameList 시작");
    List<ParticipantGameEntity> userGameList = checkMyGameList();

    List<GameEntity> games = userGameList.stream()
        .map(ParticipantGameEntity::getGame)
        .filter(
            game -> game.getStartDateTime().isBefore(LocalDateTime.now()))
        .toList();

    Long userId = jwtTokenExtract.currentUser().getId();

    log.info(
        String.format("[user_pk] = %s ->  [gameList] = [%s]",
            userId,
            games
        ));

    log.info("myLastGameList 종료");
    return getPageGameSearchResponses(games, userId, page, size);
  }

  public Page<GameSearchResponse> findFilteredGames(
      LocalDate localDate, CityName cityName, FieldStatus fieldStatus,
      Gender gender, MatchFormat matchFormat, int page, int size) {
    log.info("findFilteredGames 시작");
    log.info(
        "사용자 필터링 입력 조건 저장 -> [날짜 = {}, 도시 조건 = {}, 경기장 조건 = {}, 성별 조건 = {}, 경기 스타일 = {}]",
        localDate,
        cityName,
        fieldStatus,
        gender,
        matchFormat
    );
    Specification<GameEntity> spec = getGameEntitySpecification(
        localDate, cityName, fieldStatus, gender, matchFormat);

    List<GameEntity> gameListNow = gameUserRepository.findAll(spec);

    Long userId = null;
    log.info("findFilteredGames 종료");
    return getPageGameSearchResponses(gameListNow, userId, page, size);
  }

  public Page<GameSearchResponse> searchAddress(String address, int page, int size) {
    log.info("searchAddress 시작");
    List<GameEntity> allFromDateToday =
        gameUserRepository.findByAddressContainingIgnoreCaseAndStartDateTimeAfterOrderByStartDateTimeAsc(
            address, LocalDateTime.now());
    log.info(
        String.format("[사용자 주소 입력값] = %s / [실제 존재하는 경기값 조회 결과] = %s",
            address,
            allFromDateToday
        ));
    Long userId = null;
    log.info("searchAddress 종료");
    return getPageGameSearchResponses(allFromDateToday, userId, page, size);
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

    page = Math.max(1, Math.min(page, lastPage));

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
    log.info("participateInGame 시작");
    Long userId = jwtTokenExtract.currentUser().getId();

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    GameEntity game = gameUserRepository.findById(gameId)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    log.info(
        String.format("[user_pk] = %s = / [game] = %s",
            userId,
            game
        ));
    checkValidated(gameId, game, user);
    log.info("participateInGame 종료");
    return ParticipateGameDto.fromEntity(gameCheckOutRepository.save(
        ParticipantGameEntity.builder()
            .status(ParticipantGameStatus.APPLY)
            .game(game)
            .user(user)
            .build()));
  }

  private void checkValidated(Long gameId, GameEntity game,
      UserEntity user) {
    if (gameCheckOutRepository.existsByGame_IdAndUser_Id(
        gameId,
        user.getId())) {
      throw new CustomException(ErrorCode.DUPLICATED_TRY_TO_JOIN_GAME);
    }
    if (gameCheckOutRepository.countByStatusAndGameId(
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

    return gameCheckOutRepository.findByUser_IdAndStatus(
            user.getId(), ParticipantGameStatus.ACCEPT)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
  }

}


package com.zerobase.hoops.gameUsers.service;

import static com.zerobase.hoops.util.Common.getNowDateTime;

import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.document.ParticipantGameDocument;
import com.zerobase.hoops.document.UserDocument;
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
import com.zerobase.hoops.gameUsers.repository.GameUserRepository;
import com.zerobase.hoops.gameUsers.repository.MannerPointRepository;
import com.zerobase.hoops.gameUsers.repository.impl.GameCustomRepositoryImpl;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.GenderType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class GameUserService {

  private final GameCheckOutRepository gameCheckOutRepository;
  private final GameUserRepository gameUserRepository;
  private final GameCustomRepositoryImpl gameCustomRepository;
  private final MannerPointRepository mannerPointRepository;
  private final UserRepository userRepository;
  private final JwtTokenExtract jwtTokenExtract;

  @Transactional
  public void saveMannerPoint(MannerPointDto request) {
    log.info("saveMannerPoint 시작");
    String userId = jwtTokenExtract.currentUser().getId();
    String receiverId = request.getReceiverId();
    String gameId = request.getGameId();
    log.info(
        String.format("[user_pk] = %s -> [receiverId] = %s / [gameId] = %s",
            userId,
            receiverId,
            gameId));
    UserDocument userDocument = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    UserDocument receiverDocument = userRepository.findById(
            receiverId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    GameDocument gameDocument = gameUserRepository.findByIdAndStartDateTimeBefore(
            gameId, getNowDateTime())
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    checkExistRate(request, userId, gameId);
    receiverDocument.saveMannerPoint(request.getPoint());

    long mannerPointId = mannerPointRepository.count() + 1;

    mannerPointRepository.save(
        request.toDocument(userDocument, receiverDocument, gameDocument, mannerPointId));
    log.info("saveMannerPoint 종료");
  }

  private void checkExistRate(MannerPointDto request, String userId,
      String gameId) {
    boolean checking = mannerPointRepository.existsByUser_IdAndReceiver_IdAndGame_Id(
        userId, request.getReceiverId(), gameId);

    if (checking) {
      throw new CustomException(ErrorCode.EXIST_RATE);
    }
  }

  public List<MannerPointListResponse> getMannerPoint(
      String gameId) {
    log.info("getMannerPoint 시작");
    String currentUserId = jwtTokenExtract.currentUser().getId();
    List<ParticipantGameDocument> userList = checkMannerPointList(gameId);
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

  private List<ParticipantGameDocument> checkMannerPointList(
      String gameId) {
    String userId = jwtTokenExtract.currentUser().getId();
    userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    gameUserRepository.findByIdAndStartDateTimeBefore(
            gameId, getNowDateTime())
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    boolean finalCheck = gameCheckOutRepository.existsByGame_IdAndUser_IdAndStatus(
        gameId, userId, ParticipantGameStatus.ACCEPT);

    if (!finalCheck) {
      throw new CustomException(ErrorCode.GAME_NOT_FOUND);
    }
    return gameCheckOutRepository.findByStatusAndGame_Id(
            ParticipantGameStatus.ACCEPT, gameId)
        .orElseThrow(
            () -> new CustomException(ErrorCode.GAME_NOT_FOUND));
  }


  public Page<GameSearchResponse> myCurrentGameList(int page, int size) {
    log.info("myCurrentGameList 시작");
    List<ParticipantGameDocument> userGameList = checkMyGameList();

    List<GameDocument> games = userGameList.stream()
        .map(ParticipantGameDocument::getGame)
        .filter(
            game -> game.getStartDateTime().isAfter(getNowDateTime()))
        .toList();

    String userId = jwtTokenExtract.currentUser().getId();

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
    List<ParticipantGameDocument> userGameList = checkMyGameList();

    List<GameDocument> games = userGameList.stream()
        .map(ParticipantGameDocument::getGame)
        .filter(
            game -> game.getStartDateTime().isBefore(getNowDateTime()))
        .toList();

    String userId = jwtTokenExtract.currentUser().getId();

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

    List<GameDocument> gameListNow =
        gameCustomRepository
        .findAllGameDocuments(localDate, cityName, fieldStatus, gender, matchFormat);

    String userId = null;
    log.info("findFilteredGames 종료");
    return getPageGameSearchResponses(gameListNow, userId, page, size);
  }

  public List<GameSearchResponse> searchAddress(String address) {
    log.info("searchAddress 시작");
    List<GameDocument> allFromDateToday =
        gameUserRepository.findByAddressContainingIgnoreCaseAndStartDateTimeAfterOrderByStartDateTimeAsc(
            address, getNowDateTime());
    log.info(
        String.format("[사용자 주소 입력값] = %s / [실제 존재하는 경기값 조회 결과] = %s",
            address,
            allFromDateToday
        ));
    String userId = null;
    log.info("searchAddress 종료");
    return getGameSearchResponses(allFromDateToday, userId);
  }

  private static Page<GameSearchResponse> getPageGameSearchResponses(
      List<GameDocument> gameListNow, String userId, int page, int size) {
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
      List<GameDocument> gameListNow, String userId) {
    List<GameSearchResponse> gameList = new ArrayList<>();
    gameListNow.forEach(
        (e) -> gameList.add(GameSearchResponse.of(e, userId)));
    return gameList;
  }

  @Transactional
  public ParticipateGameDto participateInGame(String gameId) {
    log.info("participateInGame 시작");
    String userId = jwtTokenExtract.currentUser().getId();

    UserDocument user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    GameDocument game = gameUserRepository.findById(gameId)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    log.info(
        String.format("[user_pk] = %s = / [game] = %s",
            userId,
            game
        ));
    checkValidated(gameId, game, user);

    long participantId = gameCheckOutRepository.count() + 1;

    log.info("participateInGame 종료");
    return ParticipateGameDto.fromDocument(gameCheckOutRepository.save(
        ParticipantGameDocument.builder()
            .id(Long.toString(participantId))
            .status(ParticipantGameStatus.APPLY)
            .createdDateTime(getNowDateTime())
            .game(game)
            .user(user)
            .build()));
  }

  private void checkValidated(String gameId, GameDocument game,
      UserDocument user) {
    if (gameCheckOutRepository.existsByGame_IdAndUser_Id(
        gameId,
        user.getId())) {
      throw new CustomException(ErrorCode.DUPLICATED_TRY_TO_JOIN_GAME);
    }
    if (gameCheckOutRepository.countByStatusAndGameId(
        ParticipantGameStatus.ACCEPT, gameId) >= game.getHeadCount()) {
      throw new CustomException(ErrorCode.FULL_PEOPLE_GAME);
    }
    if (game.getStartDateTime().isBefore(getNowDateTime())) {
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

  private List<ParticipantGameDocument> checkMyGameList() {
    String userId = jwtTokenExtract.currentUser().getId();

    UserDocument user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    return gameCheckOutRepository.findByUser_IdAndStatus(
            user.getId(), ParticipantGameStatus.ACCEPT)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
  }

}


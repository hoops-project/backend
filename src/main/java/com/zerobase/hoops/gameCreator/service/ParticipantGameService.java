package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.exception.ErrorCode.ALREADY_GAME_START;
import static com.zerobase.hoops.exception.ErrorCode.FULL_PARTICIPANT;
import static com.zerobase.hoops.exception.ErrorCode.GAME_NOT_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_GAME_CREATOR;
import static com.zerobase.hoops.exception.ErrorCode.NOT_PARTICIPANT_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_UPDATE_CREATOR;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;

import com.zerobase.hoops.alarm.domain.NotificationType;
import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.CommonRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.ListResponse;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ParticipantGameService {

  private final ParticipantGameRepository participantGameRepository;

  private final GameRepository gameRepository;

  private final NotificationService notificationService;

  private final Clock clock;

  /**
   * 경기 참가 지원자 리스트 조회 전 validation
   */
  public List<ListResponse> validApplyParticipantList(Long gameId,
      Pageable pageable, UserEntity user) {
    log.info("loginId = {} validApplyParticipantList start", user.getLoginId());

    List<ListResponse> result = null;

    try {
      GameEntity game = getGame(gameId);

      checkCreator(user, game);

      result = getApplyParticipantList(game.getId(), pageable, user);

    } catch (CustomException e) {
      log.warn("loginId = {} validApplyParticipantList CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validApplyParticipantList Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validApplyParticipantList end", user.getLoginId());
    return result;
  }

  /**
   * 경기 지원자 리스트 조회
   */
  private List<ListResponse> getApplyParticipantList(Long gameId,
      Pageable pageable, UserEntity user) {
    return getParticipantList(APPLY, gameId, pageable, user);
  }

  /**
   * 경기 지원자 리스트 조회 전 validation
   */
  public List<ListResponse> validAcceptParticipantList(Long gameId,
      Pageable pageable, UserEntity user) {
    log.info("loginId = {} validAcceptParticipantList start", user.getLoginId());

    List<ListResponse> result = null;

    try {
      GameEntity game = getGame(gameId);

      result = getAcceptParticipantList(game.getId(), pageable, user);

    } catch (CustomException e) {
      log.warn("loginId = {} validAcceptParticipantList CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validAcceptParticipantList Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validAcceptParticipantList end", user.getLoginId());
    return result;
  }

  /**
   * 경기 참가자 리스트 조회
   */
  public List<ListResponse> getAcceptParticipantList(Long gameId,
      Pageable pageable, UserEntity user) {
    return getParticipantList(ACCEPT, gameId, pageable, user);
  }

  /**
   * 경기 지원자 수락 전 validation
   */
  public String validAcceptParticipant(CommonRequest request, UserEntity user) {
    log.info("loginId = {} validAcceptParticipant start", user.getLoginId());

    String message = "";

    try {
      ParticipantGameEntity participantGame =
          getParticipantGame(request.getParticipantId(), APPLY);

      checkIsNotCreatorParticipantGame(user, participantGame);

      GameEntity game = getGame(participantGame.getGame().getId());

      checkCreator(user, game);

      checkGameStart(game);

      int count = participantGameRepository.countByStatusAndGameId
          (ACCEPT, game.getId());

      // 경기에 참가자가 다 찼을때 수락 못함
      if (game.getHeadCount() <= count) {
        throw new CustomException(FULL_PARTICIPANT);
      }

      message = acceptParticipant(participantGame, user);

    } catch (CustomException e) {
      log.warn("loginId = {} validAcceptParticipant CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validAcceptParticipant Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validAcceptParticipant end", user.getLoginId());
    return message;
  }

  /**
   * 경기 지원자 수락
   */
  private String acceptParticipant(ParticipantGameEntity participantGame,
      UserEntity user) {

    ParticipantGameEntity result =
        ParticipantGameEntity.setAccept(participantGame, clock);

    participantGameRepository.save(result);
    log.info("loginId = {} participantGame accepted", user.getLoginId());

    notificationService.send(NotificationType.ACCEPTED_GAME, result.getUser(),
        result.getGame().getTitle() + "에 참가가 수락되었습니다.");

    return result.getUser().getNickName() + "을 경기에 수락 완료 했습니다.";
  }

  /**
   * 경기 지원자 거절 전 validation
   */
  public String validRejectParticipant(CommonRequest request, UserEntity user) {
    log.info("loginId = {} validRejectParticipant start", user.getLoginId());

    String message = "";

    try {
      ParticipantGameEntity participantGame =
          getParticipantGame(request.getParticipantId(), APPLY);

      checkIsNotCreatorParticipantGame(user, participantGame);

      GameEntity game = getGame(participantGame.getGame().getId());

      checkCreator(user, game);

      message = rejectParticipant(participantGame, user);

    } catch (CustomException e) {
      log.warn("loginId = {} validRejectParticipant CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validRejectParticipant Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validRejectParticipant end", user.getLoginId());
    return message;
  }

  /**
   * 경기 지원자 거절
   */
  private String rejectParticipant(ParticipantGameEntity participantGame,
      UserEntity user) {
    ParticipantGameEntity result =
        ParticipantGameEntity.setReject(participantGame, clock);

    participantGameRepository.save(result);
    log.info("loginId = {} participantGame rejected", user.getLoginId());

    notificationService.send(NotificationType.REJECTED_GAME, result.getUser(),
        result.getGame().getTitle() + "에 참가가 거절되었습니다.");

    log.info("kickoutParticipant end");
    return result.getUser().getNickName() + "을 경기에 거절 완료 했습니다.";
  }


  /**
   * 경기 참가자 강퇴 전 validation
   */
  public String validKickoutParticipant(CommonRequest request,
      UserEntity user) {
    log.info("loginId = {} validKickoutParticipant start", user.getLoginId());

    String message = "";

    try {
      ParticipantGameEntity participantGame =
          getParticipantGame(request.getParticipantId(), ACCEPT);

      checkIsNotCreatorParticipantGame(user, participantGame);

      GameEntity game = getGame(participantGame.getGame().getId());

      checkCreator(user, game);

      checkGameStart(game);

      message = kickoutParticipant(participantGame, user);

    } catch (CustomException e) {
      log.warn("loginId = {} validKickoutParticipant CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validKickoutParticipant Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validKickoutParticipant end", user.getLoginId());
    return message;
  }

  /**
   * 경기 참가자 강퇴
   */
  private String kickoutParticipant(ParticipantGameEntity participantGame,
      UserEntity user) {
    ParticipantGameEntity result =
        ParticipantGameEntity.setKickout(participantGame, clock);

    participantGameRepository.save(result);
    log.info("loginId = {} participantGame kickouted", user.getLoginId());

    notificationService.send(NotificationType.KICKED_OUT, result.getUser()
        , result.getGame().getTitle() + "에서 강퇴당하였습니다.");

    return result.getUser().getNickName() + "을 경기에 강퇴 완료 했습니다.";
  }

  // 리스트 조회
  private List<ListResponse> getParticipantList
  (ParticipantGameStatus status, Long gameId, Pageable pageable,
      UserEntity user) {
    Page<ParticipantGameEntity> participantGameEntityPage =
        participantGameRepository.findByStatusAndGameId(status, gameId, pageable);
    log.info("loginId = {} ParticipantList got", user.getLoginId());

    return participantGameEntityPage.stream()
        .map(ListResponse::toDto)
        .toList();
  }
  
  // 경기 조회
  private GameEntity getGame(Long gameId) {
    return gameRepository.findByIdAndDeletedDateTimeNull(gameId)
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));
  }

  // 경기 개설자만 참가 희망자 리스트 조회, 경기 (수락,거절,강퇴) 가능
  public void checkCreator(UserEntity user, GameEntity game) {
    if (!Objects.equals(user.getId(), game.getUser().getId())) {
      throw new CustomException(NOT_GAME_CREATOR);
    }
  }

  // 경기가 이미 시작하면 수락,강퇴 불가능
  private void checkGameStart(GameEntity game) {

    LocalDateTime nowDateTime = LocalDateTime.now();
    if (game.getStartDateTime().isBefore(nowDateTime)) {
      throw new CustomException(ALREADY_GAME_START);
    }
  }

  // 경기 참가 조회
  private ParticipantGameEntity getParticipantGame(Long participantId,
      ParticipantGameStatus status) {
    return participantGameRepository
        .findByIdAndStatus(participantId, status)
        .orElseThrow(() -> new CustomException(NOT_PARTICIPANT_FOUND));
  }

  // 경기 개설자는 ACCEPT 상태로 나둬야함
  private void checkIsNotCreatorParticipantGame(UserEntity user,
      ParticipantGameEntity participantGame) {

    if (Objects.equals(user.getId(),
        participantGame.getUser().getId())) {
      throw new CustomException(NOT_UPDATE_CREATOR);
    }
  }
}

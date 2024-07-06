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
import com.zerobase.hoops.gameCreator.dto.AcceptParticipantDto;
import com.zerobase.hoops.gameCreator.dto.ApplyParticipantListDto;
import com.zerobase.hoops.gameCreator.dto.AcceptParticipantListDto;
import com.zerobase.hoops.gameCreator.dto.KickoutParticipantDto;
import com.zerobase.hoops.gameCreator.dto.RejectParticipantDto;
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
   * 경기 지원자 리스트 조회 전 validation
   */
  public List<ApplyParticipantListDto.Response> validApplyParticipantList(Long gameId,
      Pageable pageable, UserEntity user) {
    log.info("loginId = {} validApplyParticipantList start", user.getLoginId());

    List<ApplyParticipantListDto.Response> result = null;

    try {
      GameEntity game = getGame(gameId);

      checkCreator(user, game);

      result = getApplyParticipantList(game.getId(), pageable, user);
      log.info("loginId = {} ApplyParticipantList got", user.getLoginId());

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
  private List<ApplyParticipantListDto.Response> getApplyParticipantList(Long gameId,
      Pageable pageable, UserEntity user) {
    Page<ParticipantGameEntity> page =
        getParticipantList(APPLY, gameId, pageable, user);

    return page.stream()
        .map(ApplyParticipantListDto.Response::toDto)
        .toList();
  }

  /**
   * 경기 참가자 리스트 조회 전 validation
   */
  public List<AcceptParticipantListDto.Response> validAcceptParticipantList(Long gameId,
      Pageable pageable, UserEntity user) {
    log.info("loginId = {} validAcceptParticipantList start", user.getLoginId());

    List<AcceptParticipantListDto.Response> result = null;

    try {
      GameEntity game = getGame(gameId);

      result = getAcceptParticipantList(game.getId(), pageable, user);
      log.info("loginId = {} AcceptParticipantList got", user.getLoginId());

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
  public List<AcceptParticipantListDto.Response> getAcceptParticipantList(Long gameId,
      Pageable pageable, UserEntity user) {
    Page<ParticipantGameEntity> page =
        getParticipantList(ACCEPT, gameId, pageable, user);

    return page.stream()
        .map(AcceptParticipantListDto.Response::toDto)
        .toList();
  }

  /**
   * 경기 지원자 수락 전 validation
   */
  public AcceptParticipantDto.Response validAcceptParticipant(
      AcceptParticipantDto.Request request, UserEntity user) {
    log.info("loginId = {} validAcceptParticipant start", user.getLoginId());

    AcceptParticipantDto.Response response = null;

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

      response = acceptParticipant(participantGame, user);

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
    return response;
  }

  /**
   * 경기 지원자 수락
   */
  private AcceptParticipantDto.Response acceptParticipant(ParticipantGameEntity participantGame,
      UserEntity user) {

    ParticipantGameEntity result =
        new ParticipantGameEntity().setAccept(participantGame, clock);

    participantGameRepository.save(result);
    log.info("loginId = {} participantGame accepted", user.getLoginId());

    notificationService.send(NotificationType.ACCEPTED_GAME, result.getUser(),
        result.getGame().getTitle() + "에 참가가 수락되었습니다.");

    return new AcceptParticipantDto.Response().toDto(
        result.getUser().getNickName() + "을(를) 경기에 수락 했습니다.");

  }

  /**
   * 경기 지원자 거절 전 validation
   */
  public RejectParticipantDto.Response validRejectParticipant(
      RejectParticipantDto.Request request, UserEntity user) {
    log.info("loginId = {} validRejectParticipant start", user.getLoginId());

    RejectParticipantDto.Response response = null;

    try {
      ParticipantGameEntity participantGame =
          getParticipantGame(request.getParticipantId(), APPLY);

      checkIsNotCreatorParticipantGame(user, participantGame);

      GameEntity game = getGame(participantGame.getGame().getId());

      checkCreator(user, game);

      response = rejectParticipant(participantGame, user);

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
    return response;
  }

  /**
   * 경기 지원자 거절
   */
  private RejectParticipantDto.Response rejectParticipant(ParticipantGameEntity participantGame,
      UserEntity user) {
    ParticipantGameEntity result =
        new ParticipantGameEntity().setReject(participantGame, clock);

    participantGameRepository.save(result);
    log.info("loginId = {} participantGame rejected", user.getLoginId());

    notificationService.send(NotificationType.REJECTED_GAME, result.getUser(),
        result.getGame().getTitle() + "에 참가가 거절되었습니다.");

    log.info("kickoutParticipant end");

    return new RejectParticipantDto.Response().toDto(
        result.getUser().getNickName() + "을(를) 경기에 거절 했습니다.");
  }


  /**
   * 경기 참가자 강퇴 전 validation
   */
  public KickoutParticipantDto.Response validKickoutParticipant(
      KickoutParticipantDto.Request request, UserEntity user) {
    log.info("loginId = {} validKickoutParticipant start", user.getLoginId());

    KickoutParticipantDto.Response response = null;

    try {
      ParticipantGameEntity participantGame =
          getParticipantGame(request.getParticipantId(), ACCEPT);

      checkIsNotCreatorParticipantGame(user, participantGame);

      GameEntity game = getGame(participantGame.getGame().getId());

      checkCreator(user, game);

      checkGameStart(game);

      response = kickoutParticipant(participantGame, user);

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
    return response;
  }

  /**
   * 경기 참가자 강퇴
   */
  private KickoutParticipantDto.Response kickoutParticipant(ParticipantGameEntity participantGame,
      UserEntity user) {
    ParticipantGameEntity result =
        new ParticipantGameEntity().setKickout(participantGame, clock);

    participantGameRepository.save(result);
    log.info("loginId = {} participantGame kickouted", user.getLoginId());

    notificationService.send(NotificationType.KICKED_OUT, result.getUser()
        , result.getGame().getTitle() + "에서 강퇴당하였습니다.");

    return new KickoutParticipantDto.Response().toDto(
        result.getUser().getNickName() + "을(를) 경기에 강퇴 했습니다.");
  }

  // 리스트 조회
  private Page<ParticipantGameEntity> getParticipantList(
      ParticipantGameStatus status, Long gameId, Pageable pageable,
      UserEntity user) {
    return participantGameRepository.findByStatusAndGameId(status, gameId, pageable);
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

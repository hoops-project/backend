package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.exception.ErrorCode.ALREADY_GAME_START;
import static com.zerobase.hoops.exception.ErrorCode.FULL_PARTICIPANT;
import static com.zerobase.hoops.exception.ErrorCode.GAME_NOT_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_GAME_CREATOR;
import static com.zerobase.hoops.exception.ErrorCode.NOT_PARTICIPANT_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_UPDATE_CREATOR;
import static com.zerobase.hoops.exception.ErrorCode.USER_NOT_FOUND;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;

import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.AcceptRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.AcceptResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.KickoutRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.KickoutResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.RejectRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.RejectResponse;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ParticipantGameService {

  private final ParticipantGameRepository participantGameRepository;

  private final GameRepository gameRepository;

  private final UserRepository userRepository;

  private final JwtTokenExtract jwtTokenExtract;

  private static UserEntity user;

  private static GameEntity gameEntity;

  private static ParticipantGameEntity participantGameEntity;
  private final NotificationService notificationService;

  /**
   * 경기 참가 희망자 리스트 조회
   */
  public List<DetailResponse> getApplyParticipantList(Long gameId) {
    log.info("getParticipantList start");

    setUpUser();

    GameEntity game =
        gameRepository.findByGameIdAndDeletedDateTimeNull(gameId)
            .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    validationCreatorCheck(user, game);

    List<ParticipantGameEntity> list = participantGameRepository
        .findByStatusAndGameEntityGameId(APPLY, gameId);

    List<DetailResponse> detailResponseList = list.stream()
        .map(DetailResponse::toDto)
        .toList();

    log.info("getParticipantList end");
    return detailResponseList;
  }

  /**
   * 경기 참가자 리스트 조회
   */
  public List<DetailResponse> getAcceptParticipantList(Long gameId) {

    setUpUser();

    GameEntity game =
        gameRepository.findByGameIdAndDeletedDateTimeNull(gameId)
            .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    List<ParticipantGameEntity> list = participantGameRepository
        .findByStatusAndGameEntityGameId(ACCEPT, gameId);

    List<DetailResponse> detailResponseList = list.stream()
        .map(DetailResponse::toDto)
        .toList();

    return detailResponseList;
  }

  /**
   * 경기 참가 희망자 수락
   */
  public AcceptResponse acceptParticipant(AcceptRequest request) {
    log.info("acceptParticipant start");

    setUpUser();

    setUpParticipant(request.getParticipantId());

    validationCreatorCheck(user, gameEntity);

    validationGameStart(gameEntity);

    validateIsNotCreator(user);

    long count = participantGameRepository.countByStatusAndGameEntityGameId
        (ACCEPT, gameEntity.getGameId());

    // 경기에 참가자가 다 찼을때 수락 못함
    if (gameEntity.getHeadCount() <= count) {
      throw new CustomException(FULL_PARTICIPANT);
    }

    ParticipantGameEntity result =
        ParticipantGameEntity.setAccept(participantGameEntity);

    notificationService.send(result.getUserEntity(), "경기참가에 수락되었습니다.");
    participantGameRepository.save(result);

    log.info("acceptParticipant end");
    return AcceptResponse.toDto(result);
  }

  /**
   * 경기 참가 희망자 거절
   */
  public RejectResponse rejectParticipant(RejectRequest request) {
    log.info("rejectParticipant start");

    setUpUser();

    setUpParticipant(request.getParticipantId());

    validationCreatorCheck(user, gameEntity);

    validateIsNotCreator(user);

    ParticipantGameEntity result =
        ParticipantGameEntity.setReject(participantGameEntity);

    notificationService.send(result.getUserEntity(), "경기참가에 거절되었습니다.");
    participantGameRepository.save(result);

    log.info("rejectParticipant end");
    return RejectResponse.toDto(result);
  }

  /**
   * 경기 참가자 강퇴
   */
  public KickoutResponse kickoutParticipant(KickoutRequest request) {
    log.info("kickoutParticipant start");

    setUpUser();

    participantGameEntity = participantGameRepository
        .findByParticipantIdAndStatus(request.getParticipantId(), ACCEPT)
        .orElseThrow(() -> new CustomException(NOT_PARTICIPANT_FOUND));

    gameEntity = gameRepository.findByGameIdAndDeletedDateTimeNull
            (participantGameEntity.getGameEntity().getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    validationCreatorCheck(user, gameEntity);

    validationGameStart(gameEntity);

    validateIsNotCreator(user);

    ParticipantGameEntity result =
        ParticipantGameEntity.setKickout(participantGameEntity);

    participantGameRepository.save(result);
    notificationService.send(result.getUserEntity(), "경기에서 강퇴당하였습니다.");

    log.info("kickoutParticipant end");
    return KickoutResponse.toDto(result);
  }



  public void setUpUser() {
    Long userId = jwtTokenExtract.currentUser().getUserId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }

  public void setUpParticipant(Long participantId) {
    participantGameEntity = participantGameRepository
        .findByParticipantIdAndStatus(participantId, APPLY)
        .orElseThrow(() -> new CustomException(NOT_PARTICIPANT_FOUND));

    gameEntity = gameRepository.findByGameIdAndDeletedDateTimeNull
            (participantGameEntity.getGameEntity().getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));
  }

  // 경기 개설자만 수락,거절,강퇴 가능
  public void validationCreatorCheck(UserEntity user, GameEntity game) {

    if (!Objects.equals(user.getUserId(), game.getUserEntity().getUserId())) {
      throw new CustomException(NOT_GAME_CREATOR);
    }
  }

  // 경기가 이미 시작하면 수락,강퇴 불가능
  public void validationGameStart(GameEntity game) {

    LocalDateTime nowDateTime = LocalDateTime.now();
    if (game.getStartDateTime().isBefore(nowDateTime)) {
      throw new CustomException(ALREADY_GAME_START);
    }
  }

  // 경기 개설자는 ACCEPT 상태로 나둬야함
  public void validateIsNotCreator(UserEntity user) {

    if (Objects.equals(user.getUserId(),
        participantGameEntity.getUserEntity().getUserId())) {
      throw new CustomException(NOT_UPDATE_CREATOR);
    }
  }


}

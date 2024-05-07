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

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.AcceptRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.KickoutRequest;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.RejectRequest;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.security.TokenProvider;
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

  private final TokenProvider tokenProvider;

  private static UserEntity userEntity;

  private static GameEntity gameEntity;

  private static ParticipantGameEntity participantGameEntity;

  /**
   * 경기 참가 희망자 리스트 조회
   */
  public List<DetailResponse> getParticipantList(Long gameId, String token) {
    log.info("getParticipantList start");

    setUpUser(token);

    var game =
        gameRepository.findByGameIdAndDeletedDateTimeNull(gameId)
            .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    validationCreatorCheck(userEntity, game);

    List<ParticipantGameEntity> list = participantGameRepository
        .findByStatusAndGameEntityGameId(ACCEPT, gameId);

    List<DetailResponse> detailResponseList = list.stream()
        .map(DetailResponse::toDto)
        .toList();

    log.info("getParticipantList end");
    return detailResponseList;
  }

  /**
   * 경기 참가 희망자 수락
   */
  public void acceptParticipant(AcceptRequest request, String token) {
    log.info("acceptParticipant start");

    setUpUser(token);

    setUpParticipant(request.getParticipantId());

    validationCreatorCheck(userEntity, gameEntity);

    validationCheck(userEntity, gameEntity);

    long count = participantGameRepository.countByStatusAndGameEntityGameId
        (ACCEPT, request.getParticipantId());

    // 경기에 참가자가 다 찼을때 수락 못함
    if(gameEntity.getHeadCount() <= count){
      throw new CustomException(FULL_PARTICIPANT);
    }

    ParticipantGameEntity result =
        ParticipantGameEntity.setAccept(participantGameEntity);

    participantGameRepository.save(result);

    log.info("acceptParticipant end");
  }

  /**
   * 경기 참가 희망자 거절
   */
  public void rejectParticipant(RejectRequest request, String token) {
    log.info("rejectParticipant start");

    setUpUser(token);

    setUpParticipant(request.getParticipantId());

    validationCreatorCheck(userEntity, gameEntity);

    validationCheck(userEntity, gameEntity);

    ParticipantGameEntity result =
        ParticipantGameEntity.setReject(participantGameEntity);

    participantGameRepository.save(result);

    log.info("rejectParticipant end");
  }

  /**
   * 경기 참가자 강퇴
   */
  public void kickoutParticipant(KickoutRequest request, String token) {
    log.info("kickoutParticipant start");

    setUpUser(token);

    participantGameEntity = participantGameRepository
        .findByParticipantIdAndStatus(request.getParticipantId(), ACCEPT)
        .orElseThrow(() -> new CustomException(NOT_PARTICIPANT_FOUND));

    gameEntity = gameRepository.findByGameIdAndDeletedDateTimeNull
            (participantGameEntity.getGameEntity().getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    validationCreatorCheck(userEntity, gameEntity);

    validationCheck(userEntity, gameEntity);

    ParticipantGameEntity result =
        ParticipantGameEntity.setKickout(participantGameEntity);

    participantGameRepository.save(result);

    log.info("kickoutParticipant end");
  }

  public void setUpUser(String token) {
    String email = tokenProvider.parseClaims(token.substring(7)).getSubject();

    userEntity = userRepository.findByEmail(email)
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

  public void validationCreatorCheck(UserEntity user, GameEntity game) {
    // 경기 개설자만 수락,거절,강퇴 가능
    if(!Objects.equals(user.getUserId(), game.getUserEntity().getUserId())) {
      throw new CustomException(NOT_GAME_CREATOR);
    }
  }

  public void validationCheck(UserEntity user, GameEntity game) {
    // 경기가 이미 시작하면 수락,거절,강퇴 불가능
    LocalDateTime nowDateTime = LocalDateTime.now();
    if(game.getStartDateTime().isBefore(nowDateTime)) {
      throw new CustomException(ALREADY_GAME_START);
    }

    // 경기 개설자는 ACCEPT 상태로 나둬야함
    if(Objects.equals(user.getUserId(),
        participantGameEntity.getUserEntity().getUserId())) {
      throw new CustomException(NOT_UPDATE_CREATOR);
    }
  }

}

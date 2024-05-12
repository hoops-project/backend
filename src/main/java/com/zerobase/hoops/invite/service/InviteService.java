package com.zerobase.hoops.invite.service;

import static com.zerobase.hoops.exception.ErrorCode.ALREADY_GAME_START;
import static com.zerobase.hoops.exception.ErrorCode.ALREADY_INVITE_GAME;
import static com.zerobase.hoops.exception.ErrorCode.ALREADY_PARTICIPANT_GAME;
import static com.zerobase.hoops.exception.ErrorCode.FULL_PARTICIPANT;
import static com.zerobase.hoops.exception.ErrorCode.GAME_NOT_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_INVITE_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_PARTICIPANT_GAME;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_REQUEST;
import static com.zerobase.hoops.exception.ErrorCode.USER_NOT_FOUND;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.invite.dto.InviteDto.CancelRequest;
import com.zerobase.hoops.invite.dto.InviteDto.CancelResponse;
import com.zerobase.hoops.invite.dto.InviteDto.CreateRequest;
import com.zerobase.hoops.invite.dto.InviteDto.CreateResponse;
import com.zerobase.hoops.invite.repository.InviteRepository;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class InviteService {

  private final GameRepository gameRepository;

  private final ParticipantGameRepository participantGameRepository;

  private final InviteRepository inviteRepository;

  private final UserRepository userRepository;

  private final JwtTokenExtract jwtTokenExtract;

  private static UserEntity user;

  /**
   * 경기 초대 요청
   */
  public CreateResponse requestInviteGame(CreateRequest request) {
    setUpUser();

    GameEntity game = gameRepository.findByGameIdAndDeletedDateTimeNull
            (request.getGameId()).orElseThrow
        (() -> new CustomException(GAME_NOT_FOUND));

    UserEntity receiverUser = userRepository
        .findById(request.getReceiverUserId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    LocalDateTime nowDatetime = LocalDateTime.now();

    // 경기 시작이 되면 경기 초대 막음
    if(game.getStartDateTime().isBefore(nowDatetime)) {
      throw new CustomException(ALREADY_GAME_START);
    }

    // 해당 경기에 이미 초대 요청 되어 있으면 막음
    boolean inviteRequestFlag =
        inviteRepository.existsByInviteStatusAndGameEntityGameIdAndReceiverUserEntityUserId
        (InviteStatus.REQUEST, request.getGameId(),
            request.getReceiverUserId());

    if(inviteRequestFlag) {
      throw new CustomException(ALREADY_INVITE_GAME);
    }

    // 해당 경기에 참가해 있지 않은 사람이 초대를 할경우 막음
    boolean gameExistFlag = participantGameRepository
        .existsByStatusAndGameEntityGameIdAndUserEntityUserId
            (ParticipantGameStatus.ACCEPT, request.getGameId(),
                user.getUserId());

    if(!gameExistFlag) {
      throw new CustomException(NOT_PARTICIPANT_GAME);
    }

    // 해당 경기에 이미 참가해 있을 경우 막음
    boolean participantGameFlag = participantGameRepository
        .existsByStatusAndGameEntityGameIdAndUserEntityUserId
            (ParticipantGameStatus.ACCEPT, request.getGameId(),
                request.getReceiverUserId());

    if(participantGameFlag) {
      throw new CustomException(ALREADY_PARTICIPANT_GAME);
    }

    // 경기 인원이 다 차면 초대 막음
    long headCount = participantGameRepository.countByStatusAndGameEntityGameId(
        ParticipantGameStatus.ACCEPT, request.getGameId());

    if(headCount >= game.getHeadCount()) {
      throw new CustomException(FULL_PARTICIPANT);
    }

    InviteEntity inviteEntity = CreateRequest
        .toEntity(user, receiverUser, game);

    inviteRepository.save(inviteEntity);

    return CreateResponse.toDto(inviteEntity);
  }

  public CancelResponse cancelInviteGame(CancelRequest request) {
    setUpUser();

    InviteEntity inviteEntity = inviteRepository
        .findByInviteIdAndInviteStatus(request.getInviteId(),
            InviteStatus.REQUEST)
        .orElseThrow(() -> new CustomException(NOT_INVITE_FOUND));

    // 본인이 경기 초대 요청한 것만 취소 가능
    if(!Objects.equals(inviteEntity.getSenderUserEntity().getUserId(),
        user.getUserId())) {
      throw new CustomException(NOT_SELF_REQUEST);
    }

    // 다른 경기 초대 요청 인지 체크
    if(!Objects.equals(inviteEntity.getGameEntity().getGameId(),
        request.getGameId())) {
      throw new CustomException(NOT_INVITE_FOUND);
    }

    InviteEntity result = InviteEntity.cancelEntity(inviteEntity);

    inviteRepository.save(result);

    return CancelResponse.toDto(result);
  }

  public void setUpUser() {
    Long userId = jwtTokenExtract.currentUser().getUserId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }



}

package com.zerobase.hoops.invite.service;

import static com.zerobase.hoops.exception.ErrorCode.ALREADY_GAME_START;
import static com.zerobase.hoops.exception.ErrorCode.ALREADY_INVITE_GAME;
import static com.zerobase.hoops.exception.ErrorCode.ALREADY_PARTICIPANT_GAME;
import static com.zerobase.hoops.exception.ErrorCode.FULL_PARTICIPANT;
import static com.zerobase.hoops.exception.ErrorCode.GAME_NOT_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_FOUND_ACCEPT_FRIEND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_GAME_INVITE;
import static com.zerobase.hoops.exception.ErrorCode.NOT_INVITE_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_PARTICIPANT_GAME;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_INVITE_REQUEST;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_REQUEST;
import static com.zerobase.hoops.exception.ErrorCode.USER_NOT_FOUND;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.invite.dto.InviteDto.CancelRequest;
import com.zerobase.hoops.invite.dto.InviteDto.CancelResponse;
import com.zerobase.hoops.invite.dto.InviteDto.CreateRequest;
import com.zerobase.hoops.invite.dto.InviteDto.CreateResponse;
import com.zerobase.hoops.invite.dto.InviteDto.InviteMyListResponse;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveAcceptRequest;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveAcceptResponse;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveRejectRequest;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveRejectResponse;
import com.zerobase.hoops.invite.repository.InviteRepository;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

  private final FriendRepository friendRepository;

  private final JwtTokenExtract jwtTokenExtract;

  private static UserEntity user;

  private static UserEntity receiverUser;

  /**
   * 경기 초대 요청
   */
  public CreateResponse requestInviteGame(CreateRequest request) {
    setUpUser();

    GameEntity game = gameRepository.findByIdAndDeletedDateTimeNull
            (request.getGameId()).orElseThrow
        (() -> new CustomException(GAME_NOT_FOUND));

    UserEntity receiverUser = userRepository
        .findById(request.getReceiverUserId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    validFriendUser(receiverUser.getId());

    //해당 경기가 초대 불가능이면 막음
    if(!game.getInviteYn()) {
      throw new CustomException(NOT_GAME_INVITE);
    }

    LocalDateTime nowDatetime = LocalDateTime.now();

    // 경기 시작이 되면 경기 초대 막음
    if(game.getStartDateTime().isBefore(nowDatetime)) {
      throw new CustomException(ALREADY_GAME_START);
    }

    // 해당 경기에 이미 초대 요청 되어 있으면 막음
    boolean inviteRequestFlag =
        inviteRepository.existsByInviteStatusAndGameIdAndReceiverUserId
        (InviteStatus.REQUEST, request.getGameId(),
            request.getReceiverUserId());

    if(inviteRequestFlag) {
      throw new CustomException(ALREADY_INVITE_GAME);
    }

    // 해당 경기에 참가해 있지 않은 사람이 초대를 할경우 막음
    boolean gameExistFlag = participantGameRepository
        .existsByStatusAndGameIdAndUserId
            (ParticipantGameStatus.ACCEPT, request.getGameId(),
                user.getId());

    if(!gameExistFlag) {
      throw new CustomException(NOT_PARTICIPANT_GAME);
    }

    // 해당 경기에 이미 참가 하거나 요청한 경우 막음
    boolean participantGameFlag = participantGameRepository
        .existsByStatusInAndGameIdAndUserId
            (List.of(ParticipantGameStatus.ACCEPT, ParticipantGameStatus.APPLY)
                ,request.getGameId(), request.getReceiverUserId());

    if(participantGameFlag) {
      throw new CustomException(ALREADY_PARTICIPANT_GAME);
    }

    // 경기 인원이 다 차면 초대 막음
    long headCount = participantGameRepository.countByStatusAndGameId(
        ParticipantGameStatus.ACCEPT, request.getGameId());

    if(headCount >= game.getHeadCount()) {
      throw new CustomException(FULL_PARTICIPANT);
    }

    InviteEntity inviteEntity = CreateRequest
        .toEntity(user, receiverUser, game);

    inviteRepository.save(inviteEntity);

    return CreateResponse.toDto(inviteEntity);
  }

  /**
   * 경기 초대 요청 취소
   */
  public CancelResponse cancelInviteGame(CancelRequest request) {
    setUpUser();

    InviteEntity inviteEntity = inviteRepository
        .findByIdAndInviteStatus(request.getInviteId(),
            InviteStatus.REQUEST)
        .orElseThrow(() -> new CustomException(NOT_INVITE_FOUND));

    // 본인이 경기 초대 요청한 것만 취소 가능
    if(!Objects.equals(inviteEntity.getSenderUser().getId(),
        user.getId())) {
      throw new CustomException(NOT_SELF_REQUEST);
    }

    // 다른 경기 초대 요청 인지 체크
    if(!Objects.equals(inviteEntity.getGame().getId(),
        request.getGameId())) {
      throw new CustomException(NOT_INVITE_FOUND);
    }

    InviteEntity result = InviteEntity.toCancelEntity(inviteEntity);

    inviteRepository.save(result);

    return CancelResponse.toDto(result);
  }

  /**
   * 경기 초대 요청 상대방 수락
   */
  public ReceiveAcceptResponse receiveAcceptInviteGame(ReceiveAcceptRequest request) {
    setUpUser();

    InviteEntity inviteEntity = inviteRepository
        .findByIdAndInviteStatus(request.getInviteId(),
            InviteStatus.REQUEST)
        .orElseThrow(() -> new CustomException(NOT_INVITE_FOUND));

    validFriendUser(inviteEntity.getSenderUser().getId());

    // 본인이 받은 초대 요청만 수락 가능
    if(!Objects.equals(inviteEntity.getReceiverUser().getId(),
        user.getId())) {
      throw new CustomException(NOT_SELF_INVITE_REQUEST);
    }

    // 해당 경기에 인원이 다차면 수락 불가능
    long count = participantGameRepository
        .countByStatusAndGameId(ParticipantGameStatus.ACCEPT,
            inviteEntity.getGame().getId());

    if(count >= inviteEntity.getGame().getHeadCount()) {
      throw new CustomException(FULL_PARTICIPANT);
    }

    InviteEntity result = InviteEntity.toAcceptEntity(inviteEntity);
    inviteRepository.save(result);

    // 경기 개설자가 초대 한 경우 수락 -> 경기 참가
    if(Objects.equals(inviteEntity.getGame().getUser().getId(),
        inviteEntity.getSenderUser().getId())) {

      ParticipantGameEntity gameCreatorInvite =
          ParticipantGameEntity.gameCreatorInvite(inviteEntity);

      participantGameRepository.save(gameCreatorInvite);
    } else { // 경기 개설자가 아닌 팀원이 초대 한 경우 -> 경기 개설자가 수락,거절 진행
      ParticipantGameEntity gameUserInvite =
          ParticipantGameEntity.gameUserInvite(inviteEntity);

      participantGameRepository.save(gameUserInvite);
    }
    return ReceiveAcceptResponse.toDto(result);
  }

  /**
   * 경기 초대 요청 상대방 거절
   */
  public ReceiveRejectResponse receiveRejectInviteGame(
      ReceiveRejectRequest request) {
    setUpUser();

    InviteEntity inviteEntity = inviteRepository
        .findByIdAndInviteStatus(request.getInviteId(),
            InviteStatus.REQUEST)
        .orElseThrow(() -> new CustomException(NOT_INVITE_FOUND));

    // 본인이 받은 초대 요청만 거절 가능
    if(!Objects.equals(inviteEntity.getReceiverUser().getId(),
        user.getId())) {
      throw new CustomException(NOT_SELF_INVITE_REQUEST);
    }

    InviteEntity result = InviteEntity.toRejectEntity(inviteEntity);

    inviteRepository.save(result);

    return ReceiveRejectResponse.toDto(result);
  }

  /**
   * 내가 초대 요청 받은 리스트 조회
   */
  public List<InviteMyListResponse> getInviteRequestList() {
    setUpUser();

    List<InviteEntity> inviteEntityList =
        inviteRepository.findByInviteStatusAndReceiverUserId
        (InviteStatus.REQUEST, user.getId());

    List<InviteMyListResponse> result = inviteEntityList.stream()
        .map(InviteMyListResponse::toDto)
        .collect(Collectors.toList());

    return result;
  }

  public void setUpUser() {
    Long userId = jwtTokenExtract.currentUser().getId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }

  // 친구 인지 검사
  private void validFriendUser(Long receiverUserId) {
    boolean existFriendFlag =
        friendRepository
            .existsByUserIdAndFriendUserIdAndStatus
                (user.getId(), receiverUserId, FriendStatus.ACCEPT);

    if(!existFriendFlag) {
      throw new CustomException(NOT_FOUND_ACCEPT_FRIEND);
    }
  }
}

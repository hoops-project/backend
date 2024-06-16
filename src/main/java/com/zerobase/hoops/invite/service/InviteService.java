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
import com.zerobase.hoops.invite.dto.InviteDto.CommonRequest;
import com.zerobase.hoops.invite.dto.InviteDto.CreateRequest;
import com.zerobase.hoops.invite.dto.InviteDto.InviteMyListResponse;
import com.zerobase.hoops.invite.repository.InviteRepository;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
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
public class InviteService {

  private final GameRepository gameRepository;

  private final ParticipantGameRepository participantGameRepository;

  private final InviteRepository inviteRepository;

  private final UserRepository userRepository;

  private final FriendRepository friendRepository;

  private final JwtTokenExtract jwtTokenExtract;

  private final Clock clock;

  /**
   * 경기 초대 요청 전 validation
   */
  public String validRequestInvite(CreateRequest request) {
    UserEntity user = getCurrentUser();

    GameEntity game = getGame(request.getGameId());

    //해당 경기가 초대 불가능이면 막음
    if(!game.getInviteYn()) {
      throw new CustomException(NOT_GAME_INVITE);
    }
    
    // 경기 시작이 되면 경기 초대 막음
    checkGameStart(game);

    // 해당 경기에 참가해 있지 않은 사람이 초대를 할경우 막음
    checkParticipantGame(game, user);

    // 경기 인원이 다 차면 초대 막음
    countsHeadCount(game);

    UserEntity receiverUser = userRepository
        .findById(request.getReceiverUserId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 초대 받은 사람이 친구 인지 검사
    checkFriend(user, receiverUser.getId());

    // 해당 경기에 이미 초대 요청 되어 있으면 막음
    boolean requestFlag =
        inviteRepository.existsByInviteStatusAndGameIdAndReceiverUserId
        (InviteStatus.REQUEST, game.getId(),
            receiverUser.getId());

    if(requestFlag) {
      throw new CustomException(ALREADY_INVITE_GAME);
    }

    // 해당 경기에 이미 참가 하거나 요청한 경우 막음
    checkAcceptOrApplyGame(game, receiverUser);

    String message = requestInvite(user, receiverUser, game);

    return message;
  }

  /**
   * 경기 초대 요청
   */
  private String requestInvite(UserEntity user, UserEntity receiverUser,
      GameEntity game) {
    InviteEntity inviteEntity = CreateRequest
        .toEntity(user, receiverUser, game);

    inviteRepository.save(inviteEntity);

    return game.getTitle() + "에 " + receiverUser.getNickName() + "을 초대 요청 "
        + "했습니다.";
  }

  /**
   * 경기 초대 요청 취소 전 validation
   */
  public String validCancelInvite(CommonRequest request) {
    UserEntity user = getCurrentUser();

    InviteEntity inviteEntity = getInvite(request.getInviteId());

    // 본인이 경기 초대 요청한 것만 취소 가능
    if(!Objects.equals(inviteEntity.getSenderUser().getId(), user.getId())) {
      throw new CustomException(NOT_SELF_REQUEST);
    }

    String message = cancelInvite(inviteEntity);

    return message;
  }

  /**
   * 경기 초대 요청 취소
   */
  private String cancelInvite(InviteEntity inviteEntity) {
    InviteEntity result = InviteEntity.toCancelEntity(inviteEntity, clock);

    inviteRepository.save(result);

    return "초대 요청을 취소 했습니다.";
  }

  /**
   * 경기 초대 요청 수락 전 validation
   */
  public String validAcceptInvite(CommonRequest request) {
    UserEntity user = getCurrentUser();

    InviteEntity inviteEntity = getInvite(request.getInviteId());

    GameEntity game = inviteEntity.getGame();

    // 본인이 받은 초대 요청만 거절 가능
    checkMyReceiveInvite(inviteEntity, user);

    // 친구 인지 검사
    checkFriend(user, inviteEntity.getSenderUser().getId());

    // 경기 시작이 되면 경기 수락 막음
    checkGameStart(game);

    // 초대한 사람이 해당 경기에 참가해 있지 않으면 막음
    boolean gameExistFlag = participantGameRepository
        .existsByStatusAndGameIdAndUserId
            (ParticipantGameStatus.ACCEPT, game.getId(),
                inviteEntity.getSenderUser().getId());

    if(!gameExistFlag) {
      throw new CustomException(NOT_PARTICIPANT_GAME);
    }

    // 해당 경기에 인원이 다차면 수락 불가능
    countsHeadCount(game);

    // 해당 경기에 이미 참가 하거나 요청한 경우 막음
    checkAcceptOrApplyGame(game, user);

    String message = acceptInvite(inviteEntity);

    return message;
  }

  /**
   * 경기 초대 요청 수락
   */
  private String acceptInvite(InviteEntity inviteEntity) {

    LocalDateTime nowDateTime = LocalDateTime.now(clock);

    InviteEntity result = InviteEntity.toAcceptEntity(inviteEntity, nowDateTime);
    inviteRepository.save(result);

    String message = "";

    // 경기 개설자가 초대 한 경우 수락 -> 경기 참가
    if(Objects.equals(inviteEntity.getGame().getUser().getId(),
        inviteEntity.getSenderUser().getId())) {

      ParticipantGameEntity gameCreatorInvite =
          ParticipantGameEntity.gameCreatorInvite(inviteEntity, nowDateTime);

      participantGameRepository.save(gameCreatorInvite);

      message = "경기 개설자가 초대 했으므로 경기에 바로 참가합니다.";

    } else { // 경기 개설자가 아닌 팀원이 초대 한 경우 -> 경기 개설자가 수락,거절 진행
      ParticipantGameEntity gameUserInvite =
          ParticipantGameEntity.gameUserInvite(inviteEntity);

      participantGameRepository.save(gameUserInvite);

      message = "참가자가 초대 했으므로 경기 개설자가 수락하면 경기에 참가할수 있습니다.";
    }

    return message;
  }

  /**
   * 경기 초대 요청 거절 전 validation
   */
  public String validRejectInvite(CommonRequest request) {
    UserEntity user = getCurrentUser();

    InviteEntity inviteEntity = getInvite(request.getInviteId());

    // 본인이 받은 초대 요청만 거절 가능
    checkMyReceiveInvite(inviteEntity, user);

    String message = rejectInvite(inviteEntity);

    return message;
  }

  /**
   * 경기 초대 요청 거절
   */
  private String rejectInvite(InviteEntity inviteEntity) {
    InviteEntity result = InviteEntity.toRejectEntity(inviteEntity, clock);

    inviteRepository.save(result);

    return "경기 초대 요청을 거절 했습니다.";
  }

  /**
   * 내가 초대 요청 받은 리스트 조회 전 validation
   */
  public List<InviteMyListResponse> validGetRequestInviteList(Pageable pageable) {
    UserEntity user = getCurrentUser();

    return getRequestInviteList(user, pageable);
  }

  /**
   * 내가 초대 요청 받은 리스트 조회
   */
  private List<InviteMyListResponse> getRequestInviteList(UserEntity user,
      Pageable pageable) {
    Page<InviteEntity> inviteEntityList =
        inviteRepository.findByInviteStatusAndReceiverUserId
            (InviteStatus.REQUEST, user.getId(), pageable);

    return inviteEntityList.stream()
        .map(InviteMyListResponse::toDto)
        .toList();
  }

  // 로그인 한 유저 조회
  private UserEntity getCurrentUser() {
    Long userId = jwtTokenExtract.currentUser().getId();

    return userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }
  
  // 경기 조회
  private GameEntity getGame(Long gameId) {
    return gameRepository.findByIdAndDeletedDateTimeNull(gameId)
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));
  }

  // 친구 인지 검사
  private void checkFriend(UserEntity user, Long userId) {
    boolean existFriendFlag =
        friendRepository
            .existsByUserIdAndFriendUserIdAndStatus
                (user.getId(), userId, FriendStatus.ACCEPT);

    if(!existFriendFlag) {
      throw new CustomException(NOT_FOUND_ACCEPT_FRIEND);
    }
  }
  
  // 경기가 시작 했는지 검사
  private void checkGameStart(GameEntity game) {
    LocalDateTime nowDatetime = LocalDateTime.now();

    // 경기 시작이 되면 경기 초대 막음
    if(game.getStartDateTime().isBefore(nowDatetime)) {
      throw new CustomException(ALREADY_GAME_START);
    }
  }

  // 해당 경기에 참가해 있는지 검사
  private void checkParticipantGame(GameEntity game, UserEntity user) {
    boolean gameExistFlag = participantGameRepository
        .existsByStatusAndGameIdAndUserId
            (ParticipantGameStatus.ACCEPT, game.getId(), user.getId());

    if(!gameExistFlag) {
      throw new CustomException(NOT_PARTICIPANT_GAME);
    }
  }

  // 경기 인원을 계산
  private void countsHeadCount(GameEntity game) {
    int headCount = participantGameRepository.countByStatusAndGameId(
        ParticipantGameStatus.ACCEPT, game.getId());

    if(headCount >= game.getHeadCount()) {
      throw new CustomException(FULL_PARTICIPANT);
    }
  }

  // 경기에 참가, 요청 했는지 검사
  private void checkAcceptOrApplyGame(GameEntity game, UserEntity user) {
    boolean participantGameFlag = participantGameRepository
        .existsByStatusInAndGameIdAndUserId
            (List.of(ParticipantGameStatus.ACCEPT, ParticipantGameStatus.APPLY),
                game.getId(), user.getId());

    if(participantGameFlag) {
      throw new CustomException(ALREADY_PARTICIPANT_GAME);
    }
  }
  
  // 초대 조회
  private InviteEntity getInvite(Long inviteId) {
    return inviteRepository.findByIdAndInviteStatus
            (inviteId, InviteStatus.REQUEST).orElseThrow
        (() -> new CustomException(NOT_INVITE_FOUND));
  }
  
  // 본인이 받은 요청인지 검사
  private void checkMyReceiveInvite(InviteEntity inviteEntity,
      UserEntity user) {
    if(!Objects.equals(inviteEntity.getReceiverUser().getId(), user.getId())) {
      throw new CustomException(NOT_SELF_INVITE_REQUEST);
    }
  }
}

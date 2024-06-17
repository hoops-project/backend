package com.zerobase.hoops.friends.service;

import static com.zerobase.hoops.exception.ErrorCode.ALREADY_APPLY_ACCEPT_STATUS;
import static com.zerobase.hoops.exception.ErrorCode.NOT_FOUND_ACCEPT_FRIEND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_FOUND_APPLY_FRIEND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_FOUND_NICKNAME;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_ACCEPT;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_APPLY;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_FRIEND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_MY_RECEIVE;
import static com.zerobase.hoops.exception.ErrorCode.OTHER_FRIEND_FULL;
import static com.zerobase.hoops.exception.ErrorCode.SELF_FRIEND_FULL;
import static com.zerobase.hoops.exception.ErrorCode.USER_NOT_FOUND;

import com.zerobase.hoops.alarm.domain.NotificationType;
import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyRequest;
import com.zerobase.hoops.friends.dto.FriendDto.CommonRequest;
import com.zerobase.hoops.friends.dto.FriendDto.InviteFriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.FriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RequestFriendListResponse;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.repository.impl.FriendCustomRepositoryImpl;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.users.repository.UserRepository;
import java.time.Clock;
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
public class FriendService {

  private final Clock clock;

  private final FriendRepository friendRepository;

  private final UserRepository userRepository;

  private final FriendCustomRepositoryImpl friendCustomRepository;

  private final NotificationService notificationService;

  /**
   * 친구 신청 전 validation
   */
  public String validApplyFriend(ApplyRequest request, UserEntity user) {
    log.info("loginId = {} validApplyFriend start", user.getLoginId());

    String message = "";

    try {
      // 자기 자신은 친구 신청 불가
      if(Objects.equals(user.getId(), request.getFriendUserId())) {
        throw new CustomException(NOT_SELF_FRIEND);
      }

      // 이미 친구 신청, 수락 상태이면 신청 불가
      boolean exist =
          friendRepository.existsByUserIdAndFriendUserIdAndStatusIn
              (user.getId(), request.getFriendUserId(),
                  List.of(FriendStatus.APPLY, FriendStatus.ACCEPT));

      if(exist) {
        throw new CustomException(ALREADY_APPLY_ACCEPT_STATUS);
      }

      // 자신 친구 목록 최대 30명 체크
      countsMyFriendMax(user.getId());

      // 상대방 친구 목록 최대 30명 체크
      countsOtherFriendMax(request.getFriendUserId());

      // 친구 유저 조회
      UserEntity friendUser = getFriendUser(request.getFriendUserId());

      message = applyFriend(user, friendUser);

    } catch (CustomException e) {
      log.warn("loginId = {} validApplyFriend CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validApplyFriend Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validApplyFriend end", user.getLoginId());
    return message;
  }


  /**
   * 친구 신청
   */
  private String applyFriend(UserEntity user, UserEntity friendUser) {

    FriendEntity friendEntity = ApplyRequest.toEntity(user, friendUser);

    friendRepository.save(friendEntity);
    log.info("loginId = {} friend created", user.getLoginId());

    notificationService.send(NotificationType.FRIEND,
        friendUser, user.getNickName() + "의 친구신청이 도착했습니다.");

    return friendUser.getNickName() + "에게 친구 신청을 완료했습니다.";
  }


  /**
   * 친구 신청 취소 전 validation
   */
  public String validCancelFriend(CommonRequest request, UserEntity user) {
    log.info("loginId = {} validCancelFriend start", user.getLoginId());

    String message = "";

    try {
      // 친구 상태 조회
      FriendEntity friendEntity =
          friendRepository.findByIdAndStatus(request.getFriendId(),
                  FriendStatus.APPLY)
              .orElseThrow(() -> new CustomException(NOT_FOUND_APPLY_FRIEND));

      // 자기 자신이 한 친구 신청만 취소 가능
      if(!Objects.equals(user.getId(), friendEntity.getUser().getId())) {
        throw new CustomException(NOT_SELF_APPLY);
      }

      message = cancelFriend(friendEntity, user);

    } catch (CustomException e) {
      log.warn("loginId = {} validCancelFriend CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validCancelFriend Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validCancelFriend end", user.getLoginId());
    return message;
  }

  /**
   * 친구 신청 취소
   */
  public String cancelFriend(FriendEntity friend, UserEntity user) {
    FriendEntity result = FriendEntity.setCancel(friend, clock);

    friendRepository.save(result);
    log.info("loginId = {} friend canceled", user.getLoginId());

    return result.getFriendUser().getNickName() + "에게 친구 신청 한것을 취소 했습니다.";
  }

  /**
   * 친구 수락 전 validation
   */
  public String validAcceptFriend(CommonRequest request, UserEntity user) {
    log.info("loginId = {} validAcceptFriend start", user.getLoginId());

    String message = "";

    try {

      FriendEntity friendEntity = getFriendEntity(request.getFriendId());

      // 자신이 받은 친구 신청인지 체크
      checkMyReceive(user, friendEntity);

      // 자신 친구 목록 최대 30명 체크
      countsMyFriendMax(friendEntity.getFriendUser().getId());

      // 상대방 친구 목록 최대 30명 체크
      countsOtherFriendMax(friendEntity.getUser().getId());

      message = acceptFriend(friendEntity, user);

    } catch (CustomException e) {
      log.warn("loginId = {} validAcceptFriend CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validAcceptFriend Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validAcceptFriend end", user.getLoginId());
    return message;
  }

  /**
   * 친구 수락
   */
  private String acceptFriend(FriendEntity friend, UserEntity user) {
    FriendEntity myFriendEntity = FriendEntity.setAcceptMyFriend(friend, clock);

    friendRepository.save(myFriendEntity);
    log.info("loginId = {} myFriend accepted", user.getLoginId());

    FriendEntity otherFriendEntity =
        FriendEntity.setAcceptOtherFriend(myFriendEntity);

    friendRepository.save(otherFriendEntity);
    log.info("loginId = {} otherFriend accepted", user.getLoginId());

    return myFriendEntity.getUser().getNickName() + "의 친구 신청을 수락 했습니다.";
  }

  /**
   * 친구 거절 전 validation
   */
  public String validRejectFriend(CommonRequest request, UserEntity user) {
    log.info("loginId = {} validRejectFriend start", user.getLoginId());

    String message = "";

    try {

      FriendEntity friendEntity = getFriendEntity(request.getFriendId());

      // 자신이 받은 친구 신청인지 체크
      checkMyReceive(user, friendEntity);

      message = rejectFriend(friendEntity, user);

    } catch (CustomException e) {
      log.warn("loginId = {} validRejectFriend CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validRejectFriend Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validRejectFriend end", user.getLoginId());
    return message;
  }

  /**
   * 친구 거절
   */
  private String rejectFriend(FriendEntity friend, UserEntity user) {
    FriendEntity rejectEntity = FriendEntity.setReject(friend, clock);

    friendRepository.save(rejectEntity);
    log.info("loginId = {} friend rejected", user.getLoginId());

    return rejectEntity.getUser().getNickName() + "의 친구 신청을 거절 했습니다.";
  }

  /**
   * 친구 삭제 전 validation
   */
  public String validDeleteFriend(CommonRequest request, UserEntity user) {
    log.info("loginId = {} validDeleteFriend start", user.getLoginId());

    String message = "";

    try {

      FriendEntity myAcceptFriendEntity =
          friendRepository.findByIdAndStatus(request.getFriendId(),
                  FriendStatus.ACCEPT)
              .orElseThrow(() -> new CustomException(NOT_FOUND_ACCEPT_FRIEND));

      // 자신이 받은 친구만 삭제 가능
      if(!Objects.equals(user.getId(), myAcceptFriendEntity.getUser().getId())) {
        throw new CustomException(NOT_SELF_ACCEPT);
      }

      Long userId = user.getId();
      Long friendUserId = myAcceptFriendEntity.getFriendUser().getId();

      FriendEntity otherAcceptFriendEntity =
          friendRepository.findByFriendUserIdAndUserIdAndStatus
                  (userId, friendUserId, FriendStatus.ACCEPT)
              .orElseThrow(() -> new CustomException(NOT_FOUND_ACCEPT_FRIEND));

      message =
          deleteFriend(myAcceptFriendEntity, otherAcceptFriendEntity, user);

    } catch (CustomException e) {
      log.warn("loginId = {} validDeleteFriend CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validDeleteFriend Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validDeleteFriend end", user.getLoginId());
    return message;
  }

  /**
   * 친구 삭제
   */
  public String deleteFriend(FriendEntity myAcceptFriendEntity,
      FriendEntity otherAcceptFriendEntity, UserEntity user) {

    FriendEntity myDeleteFriendEntity =
        FriendEntity.setDeleteMyFriend(myAcceptFriendEntity, clock);

    friendRepository.save(myDeleteFriendEntity);
    log.info("loginId = {} myFriend deleted", user.getLoginId());

    FriendEntity otherDeleteFriendEntity =
        FriendEntity.setDeleteOtherFriend(myDeleteFriendEntity, otherAcceptFriendEntity);

    friendRepository.save(otherDeleteFriendEntity);
    log.info("loginId = {} otherFriend deleted", user.getLoginId());

    return myAcceptFriendEntity.getFriendUser().getNickName() + "를 친구 삭제 했습니다.";
  }

  /**
   * 친구 검색 전 validation
   */
  public Page<FriendListResponse> validSearchFriend(String nickName,
      Pageable pageable, UserEntity user) {
    log.info("loginId = {} validSearchFriend start", user.getLoginId());

    Page<FriendListResponse> result = null;

    try {

      if(nickName.isBlank()) {
        throw new CustomException(NOT_FOUND_NICKNAME);
      }

      result = searchNickName(user, nickName, pageable);
      log.info("loginId = {} searchFriendList got", user.getLoginId());

    } catch (CustomException e) {
      log.warn("loginId = {} validSearchFriend CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validSearchFriend Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validSearchFriend end", user.getLoginId());
    return result;
  }

  /**
   * 친구 검색
   */
  private Page<FriendListResponse> searchNickName(UserEntity user, String nickName, Pageable pageable) {
    return friendCustomRepository.findBySearchFriendList
            (user.getId(), nickName, pageable);
  }

  /**
   * 친구 리스트 조회 전 validation
   */
  public List<FriendListResponse> validGetMyFriendList(Pageable pageable,
      UserEntity user) {
    log.info("loginId = {} validGetMyFriendList start", user.getLoginId());

    List<FriendListResponse> result = null;

    try {

      result = getMyFriendList(user, pageable);

    } catch (CustomException e) {
      log.warn("loginId = {} validGetMyFriendList CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validGetMyFriendList Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validGetMyFriendList end", user.getLoginId());
    return result;
  }

  /**
   * 친구 리스트 조회
   */
  private List<FriendListResponse> getMyFriendList(UserEntity user,
      Pageable pageable) {
    Page<FriendEntity> friendEntityPage =
        friendRepository.findByStatusAndUserId
            (FriendStatus.ACCEPT, user.getId(), pageable);
    log.info("loginId = {} myFriendList got", user.getLoginId());

    return friendEntityPage.stream()
        .map(FriendListResponse::toDto)
        .toList();
  }

  /**
   * 경기 초대 친구 리스트 조회 전 validation
   */
  public Page<InviteFriendListResponse> validGetMyInviteFriendList(
      Long gameId, Pageable pageable, UserEntity user) {
    log.info("loginId = {} validGetMyInviteFriendList start", user.getLoginId());

    Page<InviteFriendListResponse> result = null;

    try {

      result = getMyInviteFriendList(user, gameId, pageable);
      log.info("loginId = {} myInviteFriendList got", user.getLoginId());

    } catch (CustomException e) {
      log.warn("loginId = {} validGetMyInviteFriendList CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validGetMyInviteFriendList Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validGetMyInviteFriendList end", user.getLoginId());
    return result;
  }

  /**
   * 경기 초대 친구 리스트 조회
   */
  private Page<InviteFriendListResponse> getMyInviteFriendList
  (UserEntity user, Long gameId, Pageable pageable) {
    return friendCustomRepository.findByMyInviteFriendList(user.getId(), gameId, pageable);
  }

  /**
   * 내가 친구 요청 받은 리스트 조회 전 validation
   */
  public List<RequestFriendListResponse> validGetRequestFriendList
  (Pageable pageable, UserEntity user) {
    log.info("loginId = {} validGetRequestFriendList start", user.getLoginId());

    List<RequestFriendListResponse> result = null;

    try {

      result = getRequestFriendList(user, pageable);

    } catch (CustomException e) {
      log.warn("loginId = {} validGetRequestFriendList CustomException message = {}",
          user.getLoginId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("loginId = {} validGetRequestFriendList Exception message = {}",
          user.getLoginId(), e.getMessage(), e);
      throw e;
    }

    log.info("loginId = {} validGetRequestFriendList end", user.getLoginId());
    return result;
  }

  /**
   * 내가 친구 요청 받은 리스트 조회
   */
  private List<RequestFriendListResponse> getRequestFriendList(UserEntity user,
      Pageable pageable) {
    Page<FriendEntity> friendEntityList =
        friendRepository.findByStatusAndFriendUserId
            (FriendStatus.APPLY, user.getId(), pageable);
    log.info("loginId = {} requestFriendList got", user.getLoginId());

    return friendEntityList.stream()
        .map(RequestFriendListResponse::toDto)
        .toList();
  }

  // 내 친구 최대 체크
  private void countsMyFriendMax(Long userId) {
    int friendCount = countsFriendCount(userId);

    if(friendCount >= 30) {
      throw new CustomException(SELF_FRIEND_FULL);
    }
  }

  // 상대방 친구 최대 체크
  private void countsOtherFriendMax(Long friendUserId) {
    int friendCount = countsFriendCount(friendUserId);

    if(friendCount >= 30) {
      throw new CustomException(OTHER_FRIEND_FULL);
    }
  }

  // 친구 몇명인지 계산
  private int countsFriendCount(Long userId) {
    return friendRepository
        .countByUserIdAndStatus(userId, FriendStatus.ACCEPT);
  }
  
  // 친구 유저 조회
  private UserEntity getFriendUser(Long friendUserId) {
    return userRepository.findById(friendUserId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }
  
  // 친구 신청 entity 조회
  private FriendEntity getFriendEntity(Long friendId) {
    return friendRepository.findByIdAndStatus(friendId, FriendStatus.APPLY)
        .orElseThrow(() -> new CustomException(NOT_FOUND_APPLY_FRIEND));
  }

  // 자신이 받은 친구 신청 인지 체크
  private void checkMyReceive(UserEntity user, FriendEntity friendEntity) {
    if(!Objects.equals(user.getId(), friendEntity.getFriendUser().getId())) {
      throw new CustomException(NOT_MY_RECEIVE);
    }
  }

}

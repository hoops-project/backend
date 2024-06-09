package com.zerobase.hoops.friends.service;

import static com.zerobase.hoops.exception.ErrorCode.ALREADY_APPLY_ACCEPT_STATUS;
import static com.zerobase.hoops.exception.ErrorCode.NOT_FOUND_ACCEPT_FRIEND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_FOUND_APPLY_FRIEND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_FOUND_NICKNAME;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_ACCEPT;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_APPLY;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_FRIEND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_RECEIVE;
import static com.zerobase.hoops.exception.ErrorCode.OTHER_FRIEND_FULL;
import static com.zerobase.hoops.exception.ErrorCode.SELF_FRIEND_FULL;
import static com.zerobase.hoops.exception.ErrorCode.USER_NOT_FOUND;

import com.zerobase.hoops.alarm.domain.NotificationType;
import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.friends.dto.FriendDto.AcceptRequest;
import com.zerobase.hoops.friends.dto.FriendDto.AcceptResponse;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyRequest;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyResponse;
import com.zerobase.hoops.friends.dto.FriendDto.CancelRequest;
import com.zerobase.hoops.friends.dto.FriendDto.CancelResponse;
import com.zerobase.hoops.friends.dto.FriendDto.DeleteRequest;
import com.zerobase.hoops.friends.dto.FriendDto.DeleteResponse;
import com.zerobase.hoops.friends.dto.FriendDto.InviteFriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RejectRequest;
import com.zerobase.hoops.friends.dto.FriendDto.RejectResponse;
import com.zerobase.hoops.friends.dto.FriendDto.FriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RequestFriendListResponse;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.repository.impl.FriendCustomRepositoryImpl;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendService {

  private final JwtTokenExtract jwtTokenExtract;

  private final Clock clock;

  private final FriendRepository friendRepository;

  private final UserRepository userRepository;

  private final FriendCustomRepositoryImpl friendCustomRepository;

  private final NotificationService notificationService;

  private static UserEntity user;

  /**
   * 친구 신청
   */
  public ApplyResponse applyFriend(ApplyRequest request) {
    setUpUser();

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
    int selfFriendCount = friendRepository
        .countByUserIdAndStatus
            (user.getId(), FriendStatus.ACCEPT);

    if(selfFriendCount >= 30) {
      throw new CustomException(SELF_FRIEND_FULL);
    }

    // 상대방 친구 목록 최대 30명 체크
    int friendCount = friendRepository
        .countByUserIdAndStatus
            (request.getFriendUserId(), FriendStatus.ACCEPT);

    if(friendCount >= 30) {
      throw new CustomException(OTHER_FRIEND_FULL);
    }

    UserEntity friendUserEntity =
        userRepository.findById(request.getFriendUserId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    FriendEntity friendEntity = ApplyRequest.toEntity(user, friendUserEntity);
    notificationService.send(NotificationType.FRIEND,
        friendUserEntity, user.getNickName() + "의 친구신청이 도착했습니다.");
    friendRepository.save(friendEntity);

    return ApplyResponse.toDto(friendEntity);
  }

  /**
   * 친구 신청 취소
   */
  public CancelResponse cancelFriend(CancelRequest request) {
    setUpUser();

    FriendEntity friendEntity =
        friendRepository.findByIdAndStatus(request.getFriendId(),
            FriendStatus.APPLY)
            .orElseThrow(() -> new CustomException(NOT_FOUND_APPLY_FRIEND));

    // 자기 자신이 한 친구 신청만 취소 가능
    if(!Objects.equals(user.getId(),
        friendEntity.getUser().getId())) {
      throw new CustomException(NOT_SELF_APPLY);
    }

    FriendEntity result = CancelRequest.toEntity(friendEntity, clock);

    friendRepository.save(result);

    return CancelResponse.toDto(result);
  }

  /**
   * 친구 수락
   */
  public List<AcceptResponse> acceptFriend(AcceptRequest request) {
    setUpUser();

    FriendEntity friendEntity =
        friendRepository.findByIdAndStatus(request.getFriendId(),
                FriendStatus.APPLY)
            .orElseThrow(() -> new CustomException(NOT_FOUND_APPLY_FRIEND));

    // 자신이 받은 친구 신청만 수락 가능
    if(!Objects.equals(user.getId(),
        friendEntity.getFriendUser().getId())) {
      throw new CustomException(NOT_SELF_RECEIVE);
    }

    // 자신의 친구 목록 최대 30개 체크
    int selfFriendCount = friendRepository
        .countByUserIdAndStatus
            (user.getId(), FriendStatus.ACCEPT);

    if(selfFriendCount >= 30) {
      throw new CustomException(SELF_FRIEND_FULL);
    }

    // 상대방 친구 목록 최대 30명 체크
    int friendCount = friendRepository
        .countByUserIdAndStatus
            (friendEntity.getUser().getId(), FriendStatus.ACCEPT);

    if(friendCount >= 30) {
      throw new CustomException(OTHER_FRIEND_FULL);
    }


    FriendEntity myFriendEntity = AcceptRequest.toMyFriendEntity(friendEntity, clock);

    friendRepository.save(myFriendEntity);

    FriendEntity otherFriendEntity = AcceptRequest.toOtherFriendEntity(myFriendEntity);

    friendRepository.save(otherFriendEntity);

    List<AcceptResponse> result = new ArrayList<>();
    result.add(AcceptResponse.toDto(myFriendEntity));
    result.add(AcceptResponse.toDto(otherFriendEntity));

    return result;
  }

  /**
   * 친구 거절
   */
  public RejectResponse rejectFriend(RejectRequest request) {
    setUpUser();

    FriendEntity friendEntity =
        friendRepository.findByIdAndStatus(request.getFriendId(),
                FriendStatus.APPLY)
            .orElseThrow(() -> new CustomException(NOT_FOUND_APPLY_FRIEND));

    // 자신이 받은 친구 신청만 거절 가능
    if(!Objects.equals(user.getId(),
        friendEntity.getFriendUser().getId())) {
      throw new CustomException(NOT_SELF_RECEIVE);
    }

    FriendEntity rejectEntity = RejectRequest.toEntity(friendEntity, clock);

    friendRepository.save(rejectEntity);

    return RejectResponse.toDto(rejectEntity);
  }

  /**
   * 친구 삭제
   */
  public List<DeleteResponse> deleteFriend(DeleteRequest request) {
    setUpUser();

    FriendEntity myFriendAcceptEntity =
        friendRepository.findByIdAndStatus(request.getFriendId(),
                FriendStatus.ACCEPT)
            .orElseThrow(() -> new CustomException(NOT_FOUND_ACCEPT_FRIEND));

    // 자신이 받은 친구만 삭제 가능
    if(!Objects.equals(user.getId(),
        myFriendAcceptEntity.getUser().getId())) {
      throw new CustomException(NOT_SELF_ACCEPT);
    }

    Long userId = user.getId();
    Long friendUserId = myFriendAcceptEntity.getFriendUser().getId();

    FriendEntity otherFriendAcceptEntity =
        friendRepository.findByFriendUserIdAndUserIdAndStatus
                (userId, friendUserId, FriendStatus.ACCEPT)
            .orElseThrow(() -> new CustomException(NOT_FOUND_ACCEPT_FRIEND));

    FriendEntity myDeleteFriendEntity =
        DeleteRequest.toMyFriendEntity(myFriendAcceptEntity, clock);

    friendRepository.save(myDeleteFriendEntity);

    FriendEntity otherDeleteFriendEntity =
        DeleteRequest.toOtherFriendEntity(myDeleteFriendEntity, otherFriendAcceptEntity);

    friendRepository.save(otherDeleteFriendEntity);

    List<DeleteResponse> result = new ArrayList<>();
    result.add(DeleteResponse.toDto(myDeleteFriendEntity));
    result.add(DeleteResponse.toDto(otherDeleteFriendEntity));

    return result;
  }

  /**
   * 친구 검색
   */
  public Page<FriendListResponse> searchNickName(String nickName, Pageable pageable) {
    //validation
    if(nickName.isBlank()) {
      throw new CustomException(NOT_FOUND_NICKNAME);
    }

    setUpUser();

    Page<FriendListResponse> result =
        friendCustomRepository.findBySearchFriendList
            (user.getId(), nickName,
            pageable);

    return result;
  }

  /**
   * 친구 리스트 조회
   */
  public List<FriendListResponse> getMyFriends(Pageable pageable) {
    setUpUser();

    Page<FriendEntity> friendEntityPage =
        friendRepository.findByStatusAndUserId
            (FriendStatus.ACCEPT, user.getId(), pageable);

    List<FriendListResponse> result = friendEntityPage.stream()
        .map(FriendListResponse::toDto)
        .toList();

    return result;
  }

  /**
   * 경기 초대 친구 리스트 조회
   */
  public Page<InviteFriendListResponse> getMyInviteList(Long gameId,
      Pageable pageable) {
    setUpUser();

    Page<InviteFriendListResponse> result =
        friendCustomRepository.findByMyInviteFriendList
            (user.getId(), gameId, pageable);

    return result;
  }

  /**
   * 내가 친구 요청 받은 리스트 조회
   */
  public List<RequestFriendListResponse> getRequestFriendList() {
    setUpUser();

    List<FriendEntity> friendEntityList = friendRepository
        .findByStatusAndFriendUserId
            (FriendStatus.APPLY, user.getId());

    List<RequestFriendListResponse> result = friendEntityList.stream()
        .map(RequestFriendListResponse::toDto)
        .toList();

    return result;
  }

  public void setUpUser() {
    Long userId = jwtTokenExtract.currentUser().getId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }



}

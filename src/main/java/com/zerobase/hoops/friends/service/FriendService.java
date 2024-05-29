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
import com.zerobase.hoops.friends.dto.FriendDto.InviteListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RejectRequest;
import com.zerobase.hoops.friends.dto.FriendDto.RejectResponse;
import com.zerobase.hoops.friends.dto.FriendDto.ListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RequestListResponse;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.repository.impl.FriendCustomRepositoryImpl;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendService {

  private FriendRepository friendRepository;

  private final JwtTokenExtract jwtTokenExtract;

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
    if(Objects.equals(user.getUserId(), request.getFriendUserId())) {
      throw new CustomException(NOT_SELF_FRIEND);
    }

    // 이미 친구 신청, 수락 상태이면 신청 불가
    boolean exist =
        friendRepository.existsByUserEntityUserIdAndFriendUserEntityUserIdAndStatusIn
            (user.getUserId(), request.getFriendUserId(),
            List.of(FriendStatus.APPLY, FriendStatus.ACCEPT));

    if(exist) {
      throw new CustomException(ALREADY_APPLY_ACCEPT_STATUS);
    }

    // 자신 친구 목록 최대 30명 체크
    int selfFriendCount = friendRepository
        .countByUserEntityUserIdAndStatus
            (user.getUserId(), FriendStatus.ACCEPT);

    if(selfFriendCount >= 30) {
      throw new CustomException(SELF_FRIEND_FULL);
    }

    // 상대방 친구 목록 최대 30명 체크
    int friendCount = friendRepository
        .countByUserEntityUserIdAndStatus
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
        friendRepository.findByFriendIdAndStatus(request.getFriendId(),
            FriendStatus.APPLY)
            .orElseThrow(() -> new CustomException(NOT_FOUND_APPLY_FRIEND));

    // 자기 자신이 한 친구 신청만 취소 가능
    if(!Objects.equals(user.getUserId(),
        friendEntity.getUserEntity().getUserId())) {
      throw new CustomException(NOT_SELF_APPLY);
    }

    FriendEntity result = CancelRequest.toEntity(friendEntity);

    friendRepository.save(result);

    return CancelResponse.toDto(result);
  }

  /**
   * 친구 수락
   */
  public List<AcceptResponse> acceptFriend(AcceptRequest request) {
    setUpUser();

    FriendEntity friendEntity =
        friendRepository.findByFriendIdAndStatus(request.getFriendId(),
                FriendStatus.APPLY)
            .orElseThrow(() -> new CustomException(NOT_FOUND_APPLY_FRIEND));

    // 자신이 받은 친구 신청만 수락 가능
    if(!Objects.equals(user.getUserId(),
        friendEntity.getFriendUserEntity().getUserId())) {
      throw new CustomException(NOT_SELF_RECEIVE);
    }

    // 자신의 친구 목록 최대 30개 체크
    int selfFriendCount = friendRepository
        .countByUserEntityUserIdAndStatus
            (user.getUserId(), FriendStatus.ACCEPT);

    if(selfFriendCount >= 30) {
      throw new CustomException(SELF_FRIEND_FULL);
    }

    // 상대방 친구 목록 최대 30명 체크
    int friendCount = friendRepository
        .countByUserEntityUserIdAndStatus
            (friendEntity.getUserEntity().getUserId(), FriendStatus.ACCEPT);

    if(friendCount >= 30) {
      throw new CustomException(OTHER_FRIEND_FULL);
    }


    FriendEntity selfEntity = AcceptRequest.toSelfEntity(friendEntity);

    friendRepository.save(selfEntity);

    FriendEntity otherEntity = AcceptRequest.toOtherEntity(selfEntity);

    friendRepository.save(otherEntity);

    List<AcceptResponse> result = new ArrayList<>();
    result.add(AcceptResponse.toDto(selfEntity));
    result.add(AcceptResponse.toDto(otherEntity));

    return result;
  }

  /**
   * 친구 거절
   */
  public RejectResponse rejectFriend(RejectRequest request) {
    setUpUser();

    FriendEntity friendEntity =
        friendRepository.findByFriendIdAndStatus(request.getFriendId(),
                FriendStatus.APPLY)
            .orElseThrow(() -> new CustomException(NOT_FOUND_APPLY_FRIEND));

    // 자신이 받은 친구 신청만 거절 가능
    if(!Objects.equals(user.getUserId(),
        friendEntity.getFriendUserEntity().getUserId())) {
      throw new CustomException(NOT_SELF_RECEIVE);
    }

    FriendEntity rejectEntity = RejectRequest.toEntity(friendEntity);

    friendRepository.save(rejectEntity);

    return RejectResponse.toDto(rejectEntity);
  }

  /**
   * 친구 삭제
   */
  public List<DeleteResponse> deleteFriend(DeleteRequest request) {
    setUpUser();

    FriendEntity selfFriendEntity =
        friendRepository.findByFriendIdAndStatus(request.getFriendId(),
                FriendStatus.ACCEPT)
            .orElseThrow(() -> new CustomException(NOT_FOUND_ACCEPT_FRIEND));

    Long userId = user.getUserId();
    Long friendUserId = selfFriendEntity.getFriendUserEntity().getUserId();

    FriendEntity otherFriendEntity =
        friendRepository.findByFriendUserEntityUserIdAndUserEntityUserIdAndStatus
                (userId, friendUserId, FriendStatus.ACCEPT)
            .orElseThrow(() -> new CustomException(NOT_FOUND_ACCEPT_FRIEND));

    // 자신이 받은 친구만 삭제 가능
    if(!Objects.equals(user.getUserId(),
        selfFriendEntity.getUserEntity().getUserId())) {
      throw new CustomException(NOT_SELF_ACCEPT);
    }
    
    FriendEntity selfResult = DeleteRequest.toSelfEntity(selfFriendEntity);

    friendRepository.save(selfResult);

    FriendEntity otherResult = DeleteRequest.toOtherEntity(selfResult, otherFriendEntity);

    friendRepository.save(otherResult);

    List<DeleteResponse> result = new ArrayList<>();
    result.add(DeleteResponse.toDto(selfResult));
    result.add(DeleteResponse.toDto(otherResult));

    return result;
  }

  /**
   * 친구 검색
   */
  public Page<ListResponse> searchNickName(String nickName, Pageable pageable) {
    //validation
    if(nickName.isBlank()) {
      throw new CustomException(NOT_FOUND_NICKNAME);
    }

    setUpUser();

    Page<ListResponse> result =
        friendCustomRepository.findBySearchFriendList
            (user.getUserId(), nickName,
            pageable);

    return result;
  }

  /**
   * 친구 리스트 조회
   */
  public List<ListResponse> getMyFriends(Pageable pageable) {
    setUpUser();

    Page<FriendEntity> friendEntityPage =
        friendRepository.findByStatusAndUserEntityUserId
            (FriendStatus.ACCEPT, user.getUserId(), pageable);

    List<ListResponse> result = new ArrayList<>();

    friendEntityPage.stream().forEach(friendEntity -> {
      result.add(ListResponse.toDto(friendEntity));
    });

    return result;
  }

  /**
   * 경기 초대 친구 리스트 조회
   */
  public Page<InviteListResponse> getMyInviteList(Long gameId,
      Pageable pageable) {
    setUpUser();

    Page<InviteListResponse> result =
        friendCustomRepository.findByMyInviteFriendList
            (user.getUserId(), gameId, pageable);

    return result;
  }

  /**
   * 내가 친구 요청 받은 리스트 조회
   */
  public List<RequestListResponse> getRequestFriendList() {
    setUpUser();

    List<FriendEntity> friendEntityList = friendRepository
        .findByStatusAndFriendUserEntityUserId
            (FriendStatus.APPLY, user.getUserId());

    List<RequestListResponse> result = friendEntityList.stream()
        .map(RequestListResponse::toDto)
        .collect(Collectors.toList());

    return result;
  }

  public void setUpUser() {
    Long userId = jwtTokenExtract.currentUser().getUserId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }



}

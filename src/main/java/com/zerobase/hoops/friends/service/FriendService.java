package com.zerobase.hoops.friends.service;

import static com.zerobase.hoops.exception.ErrorCode.ALREADY_APPLY_ACCEPT_STATUS;
import static com.zerobase.hoops.exception.ErrorCode.NOT_FOUND_APPLY_FRIEND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_APPLY;
import static com.zerobase.hoops.exception.ErrorCode.NOT_SELF_FRIEND;
import static com.zerobase.hoops.exception.ErrorCode.OTHER_FRIEND_FULL;
import static com.zerobase.hoops.exception.ErrorCode.SELF_FRIEND_FULL;
import static com.zerobase.hoops.exception.ErrorCode.USER_NOT_FOUND;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyRequest;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyResponse;
import com.zerobase.hoops.friends.dto.FriendDto.CancelRequest;
import com.zerobase.hoops.friends.dto.FriendDto.CancelResponse;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendService {

  private FriendRepository friendRepository;

  private final JwtTokenExtract jwtTokenExtract;

  private final UserRepository userRepository;

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
    int count =
        friendRepository.findByFriendIdAndStatusIn(request.getFriendUserId(),
            List.of(FriendStatus.APPLY, FriendStatus.ACCEPT));

    if(count >= 1) {
      throw new CustomException(ALREADY_APPLY_ACCEPT_STATUS);
    }

    // 자신 친구 목록 최대 50명 체크
    int selfFriendCount = friendRepository
        .findByFriendUserEntityUserIdAndStatus
            (user.getUserId(), FriendStatus.ACCEPT);
    if(selfFriendCount >= 50) {
      throw new CustomException(SELF_FRIEND_FULL);
    }

    // 상대방 친구 목록 최대 50명 체크
    int friendCount = friendRepository
        .findByFriendUserEntityUserIdAndStatus
            (request.getFriendUserId(), FriendStatus.ACCEPT);

    if(friendCount >= 50) {
      throw new CustomException(OTHER_FRIEND_FULL);
    }

    UserEntity friendUserEntity =
        userRepository.findById(request.getFriendUserId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    FriendEntity friendEntity = ApplyRequest.toEntity(user, friendUserEntity);

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
        friendEntity.getApplyUserEntity().getUserId())) {
      throw new CustomException(NOT_SELF_APPLY);
    }

    FriendEntity result = CancelRequest.toEntity(friendEntity);

    friendRepository.save(result);

    return CancelResponse.toDto(result);
  }

  public void setUpUser() {
    Long userId = jwtTokenExtract.currentUser().getUserId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }


}

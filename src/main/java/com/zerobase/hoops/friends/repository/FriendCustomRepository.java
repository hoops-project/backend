package com.zerobase.hoops.friends.repository;

import com.zerobase.hoops.friends.dto.FriendDto.InviteFriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.FriendListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendCustomRepository {

  Page<FriendListResponse> findBySearchFriendList(
      Long userId, String nickName, Pageable pageable);

  public Page<InviteFriendListResponse> findByMyInviteFriendList(Long userId,
      Long gameId, Pageable pageable);
}

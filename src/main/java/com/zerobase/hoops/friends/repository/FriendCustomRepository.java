package com.zerobase.hoops.friends.repository;

import com.zerobase.hoops.friends.dto.FriendListDto;
import com.zerobase.hoops.friends.dto.InviteFriendListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendCustomRepository {

  Page<FriendListDto.Response> findBySearchFriendList(
      Long userId, String nickName, Pageable pageable);

  public Page<InviteFriendListDto.Response> findByMyInviteFriendList(Long userId,
      Long gameId, Pageable pageable);
}

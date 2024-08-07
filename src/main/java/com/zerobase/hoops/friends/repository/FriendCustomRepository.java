package com.zerobase.hoops.friends.repository;

import com.zerobase.hoops.friends.dto.FriendListDto;
import com.zerobase.hoops.friends.dto.InviteFriendListDto;
import com.zerobase.hoops.friends.dto.SearchFriendListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendCustomRepository {

  Page<SearchFriendListDto.Response> findBySearchFriendList(
      Long userId, String nickName, Pageable pageable);

  public Page<InviteFriendListDto.Response> findByMyInviteFriendList(Long userId,
      Long gameId, Pageable pageable);
}

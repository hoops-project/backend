package com.zerobase.hoops.friends.repository;

import com.zerobase.hoops.friends.dto.FriendDto.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendCustomRepository {

  Page<SearchResponse> findBySearchFriendList(
      Long userId, String nickName, Pageable pageable);
}

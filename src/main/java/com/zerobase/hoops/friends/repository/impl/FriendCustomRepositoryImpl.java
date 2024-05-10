package com.zerobase.hoops.friends.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.hoops.entity.QFriendEntity;
import com.zerobase.hoops.entity.QUserEntity;
import com.zerobase.hoops.friends.dto.FriendDto.SearchResponse;
import com.zerobase.hoops.friends.repository.FriendCustomRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
public class FriendCustomRepositoryImpl implements FriendCustomRepository {
  private final JPAQueryFactory jpaQueryFactory;

  public FriendCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public Page<SearchResponse> findBySearchFriendList(Long userId, String nickName, Pageable pageable) {
    QUserEntity user = QUserEntity.userEntity;
    QFriendEntity friend = QFriendEntity.friendEntity;

    List<SearchResponse> result = jpaQueryFactory.select(Projections.constructor(
            SearchResponse.class,
            user.userId,
            user.birthday,
            user.gender,
            user.nickName,
            user.playStyle,
            user.ability,
            friend.friendId))
        .from(user)
        .leftJoin(friend).on(user.userId.eq(friend.friendUserEntity.userId)
            .and(friend.userEntity.userId.eq(userId)))
        .where(user.nickName.likeIgnoreCase("%" + nickName + "%")
            .and(user.userId.ne(userId)))
        .orderBy(user.nickName.asc())
        .fetch();

    return new PageImpl<>(result, pageable, result.size());
  }
}

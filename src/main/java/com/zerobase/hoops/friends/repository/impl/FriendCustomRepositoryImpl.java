package com.zerobase.hoops.friends.repository.impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.hoops.entity.QFriendEntity;
import com.zerobase.hoops.entity.QUserEntity;
import com.zerobase.hoops.friends.dto.FriendDto.SearchResponse;
import com.zerobase.hoops.friends.repository.FriendCustomRepository;
import java.util.List;
import java.util.Optional;
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

    // Count query 생성

    JPAQuery<Long> countQuery = jpaQueryFactory.select(user.count())
        .from(user)
        .leftJoin(friend).on(user.userId.eq(friend.friendUserEntity.userId)
            .and(friend.userEntity.userId.eq(userId)))
        .where(user.nickName.likeIgnoreCase("%" + nickName + "%")
            .and(user.userId.ne(userId)));

    // Pageable에서 페이지 번호와 페이지 크기 가져오기
    int pageNumber = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    // 결과 쿼리 생성
    List<SearchResponse> result = jpaQueryFactory
        .select(Projections.constructor(
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
        .offset((long) pageNumber * pageSize)
        .limit(pageSize)
        .fetch();

    // 전체 결과의 크기 가져오기
    long total = Optional.ofNullable(countQuery.fetchOne()).orElse(0L);

    return new PageImpl<>(result, pageable, total);
  }
}

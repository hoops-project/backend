package com.zerobase.hoops.friends.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.hoops.entity.QFriendEntity;
import com.zerobase.hoops.entity.QInviteEntity;
import com.zerobase.hoops.entity.QParticipantGameEntity;
import com.zerobase.hoops.entity.QUserEntity;
import com.zerobase.hoops.friends.dto.FriendDto.InviteListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.ListResponse;
import com.zerobase.hoops.friends.repository.FriendCustomRepository;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.invite.type.InviteStatus;
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
  public Page<ListResponse> findBySearchFriendList(Long userId, String nickName, Pageable pageable) {
    QUserEntity user = QUserEntity.userEntity;
    QFriendEntity friend = QFriendEntity.friendEntity;

    List<Long> excludedIds = jpaQueryFactory
        .select(user.userId)
        .from(user)
        .where(
            user.roles.any().in("ROLE_OWNER")
            .and(user.deletedDateTime.isNull())
        )
        .fetch();

    excludedIds.add(userId);

    // Count query 생성
    JPAQuery<Long> countQuery = jpaQueryFactory
        .select(user.count())
        .from(user)
        .leftJoin(friend)
        .on(
            user.userId.eq(friend.friendUserEntity.userId)
            .and(friend.userEntity.userId.eq(userId))
            .and(friend.status.in
                (List.of(FriendStatus.ACCEPT, FriendStatus.APPLY)))
        )
        .where(user.nickName.likeIgnoreCase("%" + nickName + "%")
            .and(user.userId.notIn(excludedIds))
            .and(user.deletedDateTime.isNull()));

    // Pageable에서 페이지 번호와 페이지 크기 가져오기
    int pageNumber = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    // 결과 쿼리 생성
    List<ListResponse> result = jpaQueryFactory
        .select(Projections.constructor(ListResponse.class, user.userId, user.birthday,
                user.gender, user.nickName, user.playStyle, user.ability,
                user.stringAverageRating, friend.friendId))
        .from(user)
        .leftJoin(friend)
        .on(
            user.userId.eq(friend.friendUserEntity.userId)
            .and(friend.userEntity.userId.eq(userId))
            .and(friend.status.in
                (List.of(FriendStatus.ACCEPT, FriendStatus.APPLY)))
        )
        .where(user.nickName.likeIgnoreCase("%" + nickName + "%")
            .and(user.userId.notIn(excludedIds))
            .and(user.deletedDateTime.isNull()))
        .orderBy(user.nickName.asc())
        .offset((long) pageNumber * pageSize)
        .limit(pageSize).fetch();

    // 전체 결과의 크기 가져오기
    long total = Optional.ofNullable(countQuery.fetchOne()).orElse(0L);

    return new PageImpl<>(result, pageable, total);
  }

  @Override
  public Page<InviteListResponse> findByMyInviteFriendList(Long userId,
      Long gameId, Pageable pageable) {
    QUserEntity user = QUserEntity.userEntity;
    QFriendEntity friend = QFriendEntity.friendEntity;
    QInviteEntity invite = QInviteEntity.inviteEntity;

    QParticipantGameEntity participantGameEntity =
        QParticipantGameEntity.participantGameEntity;


    List<Long> excludedIds = jpaQueryFactory
        .select(participantGameEntity.userEntity.userId)
        .from(participantGameEntity)
        .where(participantGameEntity.gameEntity.gameId.eq(gameId)
            .and(participantGameEntity.status.in
                (List.of(ParticipantGameStatus.ACCEPT, ParticipantGameStatus.APPLY))))
        .fetch();

    // Count query 생성
    JPAQuery<Long> countQuery = jpaQueryFactory
        .select(user.count())
        .from(friend)
        .innerJoin(user)
        .on(friend.friendUserEntity.userId.eq(user.userId)
            .and(friend.userEntity.userId.eq(userId))
            .and(user.deletedDateTime.isNull())
            .and(friend.status.eq(FriendStatus.ACCEPT))
            .and(friend.friendUserEntity.userId.notIn(excludedIds))
        )
        .leftJoin(invite)
        .on(friend.friendUserEntity.userId.eq(invite.receiverUserEntity.userId)
            .and(invite.inviteStatus.eq(InviteStatus.REQUEST))
        );

    // Pageable에서 페이지 번호와 페이지 크기 가져오기
    int pageNumber = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    // 결과 쿼리 생성
    List<InviteListResponse> result = jpaQueryFactory
        .select(Projections.constructor(InviteListResponse.class, user.userId, user.birthday,
            user.gender, user.nickName, user.playStyle, user.ability,
            user.stringAverageRating, invite.inviteStatus))
        .from(friend)
        .innerJoin(user)
        .on(friend.friendUserEntity.userId.eq(user.userId)
            .and(friend.userEntity.userId.eq(userId))
            .and(user.deletedDateTime.isNull())
            .and(friend.status.eq(FriendStatus.ACCEPT))
            .and(friend.friendUserEntity.userId.notIn(excludedIds))
        )
        .leftJoin(invite)
        .on(friend.friendUserEntity.userId.eq(invite.receiverUserEntity.userId)
            .and(invite.inviteStatus.eq(InviteStatus.REQUEST))
        )
        .orderBy(user.nickName.asc())
        .offset((long) pageNumber * pageSize)
        .limit(pageSize).fetch();

    // 전체 결과의 크기 가져오기
    long total = Optional.ofNullable(countQuery.fetchOne()).orElse(0L);

    return new PageImpl<>(result, pageable, total);
  }

}

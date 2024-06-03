package com.zerobase.hoops.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriendEntity is a Querydsl query type for FriendEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriendEntity extends EntityPathBase<FriendEntity> {

    private static final long serialVersionUID = -748283487L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriendEntity friendEntity = new QFriendEntity("friendEntity");

    public final DateTimePath<java.time.LocalDateTime> acceptedDateTime = createDateTime("acceptedDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> canceledDateTime = createDateTime("canceledDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> createdDateTime = createDateTime("createdDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedDateTime = createDateTime("deletedDateTime", java.time.LocalDateTime.class);

    public final QUserEntity friendUserEntity;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> rejectedDateTime = createDateTime("rejectedDateTime", java.time.LocalDateTime.class);

    public final EnumPath<com.zerobase.hoops.friends.type.FriendStatus> status = createEnum("status", com.zerobase.hoops.friends.type.FriendStatus.class);

    public final QUserEntity userEntity;

    public QFriendEntity(String variable) {
        this(FriendEntity.class, forVariable(variable), INITS);
    }

    public QFriendEntity(Path<? extends FriendEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriendEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriendEntity(PathMetadata metadata, PathInits inits) {
        this(FriendEntity.class, metadata, inits);
    }

    public QFriendEntity(Class<? extends FriendEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.friendUserEntity = inits.isInitialized("friendUserEntity") ? new QUserEntity(forProperty("friendUserEntity")) : null;
        this.userEntity = inits.isInitialized("userEntity") ? new QUserEntity(forProperty("userEntity")) : null;
    }

}


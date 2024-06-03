package com.zerobase.hoops.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInviteEntity is a Querydsl query type for InviteEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInviteEntity extends EntityPathBase<InviteEntity> {

    private static final long serialVersionUID = -203292212L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInviteEntity inviteEntity = new QInviteEntity("inviteEntity");

    public final DateTimePath<java.time.LocalDateTime> acceptedDateTime = createDateTime("acceptedDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> canceledDateTime = createDateTime("canceledDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedDateTime = createDateTime("deletedDateTime", java.time.LocalDateTime.class);

    public final QGameEntity gameEntity;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.zerobase.hoops.invite.type.InviteStatus> inviteStatus = createEnum("inviteStatus", com.zerobase.hoops.invite.type.InviteStatus.class);

    public final QUserEntity receiverUserEntity;

    public final DateTimePath<java.time.LocalDateTime> rejectedDateTime = createDateTime("rejectedDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> requestedDateTime = createDateTime("requestedDateTime", java.time.LocalDateTime.class);

    public final QUserEntity senderUserEntity;

    public QInviteEntity(String variable) {
        this(InviteEntity.class, forVariable(variable), INITS);
    }

    public QInviteEntity(Path<? extends InviteEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInviteEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInviteEntity(PathMetadata metadata, PathInits inits) {
        this(InviteEntity.class, metadata, inits);
    }

    public QInviteEntity(Class<? extends InviteEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.gameEntity = inits.isInitialized("gameEntity") ? new QGameEntity(forProperty("gameEntity"), inits.get("gameEntity")) : null;
        this.receiverUserEntity = inits.isInitialized("receiverUserEntity") ? new QUserEntity(forProperty("receiverUserEntity")) : null;
        this.senderUserEntity = inits.isInitialized("senderUserEntity") ? new QUserEntity(forProperty("senderUserEntity")) : null;
    }

}


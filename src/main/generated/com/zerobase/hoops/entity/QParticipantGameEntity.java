package com.zerobase.hoops.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QParticipantGameEntity is a Querydsl query type for ParticipantGameEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParticipantGameEntity extends EntityPathBase<ParticipantGameEntity> {

    private static final long serialVersionUID = -1155889016L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QParticipantGameEntity participantGameEntity = new QParticipantGameEntity("participantGameEntity");

    public final DateTimePath<java.time.LocalDateTime> acceptedDateTime = createDateTime("acceptedDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> canceledDateTime = createDateTime("canceledDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> createdDateTime = createDateTime("createdDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedDateTime = createDateTime("deletedDateTime", java.time.LocalDateTime.class);

    public final QGameEntity game;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> kickoutDateTime = createDateTime("kickoutDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> rejectedDateTime = createDateTime("rejectedDateTime", java.time.LocalDateTime.class);

    public final EnumPath<com.zerobase.hoops.gameCreator.type.ParticipantGameStatus> status = createEnum("status", com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.class);

    public final QUserEntity user;

    public final DateTimePath<java.time.LocalDateTime> withdrewDateTime = createDateTime("withdrewDateTime", java.time.LocalDateTime.class);

    public QParticipantGameEntity(String variable) {
        this(ParticipantGameEntity.class, forVariable(variable), INITS);
    }

    public QParticipantGameEntity(Path<? extends ParticipantGameEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QParticipantGameEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QParticipantGameEntity(PathMetadata metadata, PathInits inits) {
        this(ParticipantGameEntity.class, metadata, inits);
    }

    public QParticipantGameEntity(Class<? extends ParticipantGameEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.game = inits.isInitialized("game") ? new QGameEntity(forProperty("game"), inits.get("game")) : null;
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}


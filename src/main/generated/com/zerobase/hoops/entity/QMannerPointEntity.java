package com.zerobase.hoops.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMannerPointEntity is a Querydsl query type for MannerPointEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMannerPointEntity extends EntityPathBase<MannerPointEntity> {

    private static final long serialVersionUID = -1115767982L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMannerPointEntity mannerPointEntity = new QMannerPointEntity("mannerPointEntity");

    public final DateTimePath<java.time.LocalDateTime> createdDateTime = createDateTime("createdDateTime", java.time.LocalDateTime.class);

    public final QGameEntity game;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final QUserEntity receiver;

    public final QUserEntity user;

    public QMannerPointEntity(String variable) {
        this(MannerPointEntity.class, forVariable(variable), INITS);
    }

    public QMannerPointEntity(Path<? extends MannerPointEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMannerPointEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMannerPointEntity(PathMetadata metadata, PathInits inits) {
        this(MannerPointEntity.class, metadata, inits);
    }

    public QMannerPointEntity(Class<? extends MannerPointEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.game = inits.isInitialized("game") ? new QGameEntity(forProperty("game"), inits.get("game")) : null;
        this.receiver = inits.isInitialized("receiver") ? new QUserEntity(forProperty("receiver")) : null;
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}


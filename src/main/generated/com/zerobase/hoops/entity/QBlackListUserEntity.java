package com.zerobase.hoops.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlackListUserEntity is a Querydsl query type for BlackListUserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlackListUserEntity extends EntityPathBase<BlackListUserEntity> {

    private static final long serialVersionUID = -1630934229L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlackListUserEntity blackListUserEntity = new QBlackListUserEntity("blackListUserEntity");

    public final QUserEntity blackUser;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public QBlackListUserEntity(String variable) {
        this(BlackListUserEntity.class, forVariable(variable), INITS);
    }

    public QBlackListUserEntity(Path<? extends BlackListUserEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlackListUserEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlackListUserEntity(PathMetadata metadata, PathInits inits) {
        this(BlackListUserEntity.class, metadata, inits);
    }

    public QBlackListUserEntity(Class<? extends BlackListUserEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.blackUser = inits.isInitialized("blackUser") ? new QUserEntity(forProperty("blackUser")) : null;
    }

}


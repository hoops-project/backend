package com.zerobase.hoops.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGameEntity is a Querydsl query type for GameEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGameEntity extends EntityPathBase<GameEntity> {

    private static final long serialVersionUID = -900437003L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGameEntity gameEntity = new QGameEntity("gameEntity");

    public final StringPath address = createString("address");

    public final EnumPath<com.zerobase.hoops.gameCreator.type.CityName> cityName = createEnum("cityName", com.zerobase.hoops.gameCreator.type.CityName.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdDateTime = createDateTime("createdDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedDateTime = createDateTime("deletedDateTime", java.time.LocalDateTime.class);

    public final EnumPath<com.zerobase.hoops.gameCreator.type.FieldStatus> fieldStatus = createEnum("fieldStatus", com.zerobase.hoops.gameCreator.type.FieldStatus.class);

    public final EnumPath<com.zerobase.hoops.gameCreator.type.Gender> gender = createEnum("gender", com.zerobase.hoops.gameCreator.type.Gender.class);

    public final NumberPath<Long> headCount = createNumber("headCount", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath inviteYn = createBoolean("inviteYn");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final EnumPath<com.zerobase.hoops.gameCreator.type.MatchFormat> matchFormat = createEnum("matchFormat", com.zerobase.hoops.gameCreator.type.MatchFormat.class);

    public final StringPath placeName = createString("placeName");

    public final DateTimePath<java.time.LocalDateTime> startDateTime = createDateTime("startDateTime", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final QUserEntity userEntity;

    public QGameEntity(String variable) {
        this(GameEntity.class, forVariable(variable), INITS);
    }

    public QGameEntity(Path<? extends GameEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGameEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGameEntity(PathMetadata metadata, PathInits inits) {
        this(GameEntity.class, metadata, inits);
    }

    public QGameEntity(Class<? extends GameEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userEntity = inits.isInitialized("userEntity") ? new QUserEntity(forProperty("userEntity")) : null;
    }

}


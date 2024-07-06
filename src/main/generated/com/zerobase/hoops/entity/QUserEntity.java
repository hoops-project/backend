package com.zerobase.hoops.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = 63964430L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final EnumPath<com.zerobase.hoops.users.type.AbilityType> ability = createEnum("ability", com.zerobase.hoops.users.type.AbilityType.class);

    public final DatePath<java.time.LocalDate> birthday = createDate("birthday", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> createdDateTime = createDateTime("createdDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedDateTime = createDateTime("deletedDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Double> doubleAverageRating = createNumber("doubleAverageRating", Double.class);

    public final StringPath email = createString("email");

    public final BooleanPath emailAuth = createBoolean("emailAuth");

    public final EnumPath<com.zerobase.hoops.users.type.GenderType> gender = createEnum("gender", com.zerobase.hoops.users.type.GenderType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath loginId = createString("loginId");

    public final StringPath name = createString("name");

    public final StringPath nickName = createString("nickName");

    public final StringPath password = createString("password");

    public final EnumPath<com.zerobase.hoops.users.type.PlayStyleType> playStyle = createEnum("playStyle", com.zerobase.hoops.users.type.PlayStyleType.class);

    public final ListPath<String, StringPath> roles = this.<String, StringPath>createList("roles", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath stringAverageRating = createString("stringAverageRating");

    public final NumberPath<Integer> totalRatings = createNumber("totalRatings", Integer.class);

    public final NumberPath<Integer> totalRatingsCount = createNumber("totalRatingsCount", Integer.class);

    public QUserEntity(String variable) {
        super(UserEntity.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata metadata) {
        super(UserEntity.class, metadata);
    }

}


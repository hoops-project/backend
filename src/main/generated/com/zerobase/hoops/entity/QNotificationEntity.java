package com.zerobase.hoops.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotificationEntity is a Querydsl query type for NotificationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationEntity extends EntityPathBase<NotificationEntity> {

    private static final long serialVersionUID = 1514916398L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotificationEntity notificationEntity = new QNotificationEntity("notificationEntity");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdDateTime = createDateTime("createdDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.zerobase.hoops.alarm.domain.NotificationType> notificationType = createEnum("notificationType", com.zerobase.hoops.alarm.domain.NotificationType.class);

    public final QUserEntity receiver;

    public QNotificationEntity(String variable) {
        this(NotificationEntity.class, forVariable(variable), INITS);
    }

    public QNotificationEntity(Path<? extends NotificationEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotificationEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotificationEntity(PathMetadata metadata, PathInits inits) {
        this(NotificationEntity.class, metadata, inits);
    }

    public QNotificationEntity(Class<? extends NotificationEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.receiver = inits.isInitialized("receiver") ? new QUserEntity(forProperty("receiver")) : null;
    }

}


package com.zerobase.hoops.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoomEntity is a Querydsl query type for ChatRoomEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoomEntity extends EntityPathBase<ChatRoomEntity> {

    private static final long serialVersionUID = 1148055446L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatRoomEntity chatRoomEntity = new QChatRoomEntity("chatRoomEntity");

    public final QGameEntity gameEntity;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> sessionId = createNumber("sessionId", Long.class);

    public QChatRoomEntity(String variable) {
        this(ChatRoomEntity.class, forVariable(variable), INITS);
    }

    public QChatRoomEntity(Path<? extends ChatRoomEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatRoomEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatRoomEntity(PathMetadata metadata, PathInits inits) {
        this(ChatRoomEntity.class, metadata, inits);
    }

    public QChatRoomEntity(Class<? extends ChatRoomEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.gameEntity = inits.isInitialized("gameEntity") ? new QGameEntity(forProperty("gameEntity"), inits.get("gameEntity")) : null;
    }

}


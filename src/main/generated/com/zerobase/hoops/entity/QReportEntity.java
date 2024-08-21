package com.zerobase.hoops.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReportEntity is a Querydsl query type for ReportEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportEntity extends EntityPathBase<ReportEntity> {

    private static final long serialVersionUID = 112434103L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReportEntity reportEntity = new QReportEntity("reportEntity");

    public final DateTimePath<java.time.LocalDateTime> blackListStartDateTime = createDateTime("blackListStartDateTime", java.time.LocalDateTime.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdDateTime = createDateTime("createdDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QUserEntity reportedUser;

    public final QUserEntity user;

    public QReportEntity(String variable) {
        this(ReportEntity.class, forVariable(variable), INITS);
    }

    public QReportEntity(Path<? extends ReportEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReportEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReportEntity(PathMetadata metadata, PathInits inits) {
        this(ReportEntity.class, metadata, inits);
    }

    public QReportEntity(Class<? extends ReportEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reportedUser = inits.isInitialized("reportedUser") ? new QUserEntity(forProperty("reportedUser")) : null;
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}


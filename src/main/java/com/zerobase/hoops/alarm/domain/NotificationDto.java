package com.zerobase.hoops.alarm.domain;

import com.zerobase.hoops.entity.NotificationEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class NotificationDto {

  // 알림 id (Pk)
  private Long id;

  // 알림 내용
  private String content;

  private LocalDateTime createdDateTime;

  @Builder
  public NotificationDto(Long id, String content, LocalDateTime createdDateTime) {
    this.id = id;
    this.content = content;
    this.createdDateTime = createdDateTime;
  }

  public static NotificationDto entityToDto(
      NotificationEntity notificationEntity) {
    return NotificationDto.builder()
        .id(notificationEntity.getId())
        .content(notificationEntity.getContent())
        .createdDateTime(notificationEntity.getCreatedDateTime())
        .build();
  }
}

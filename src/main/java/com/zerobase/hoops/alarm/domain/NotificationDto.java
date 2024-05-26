package com.zerobase.hoops.alarm.domain;

import com.zerobase.hoops.entity.NotificationEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {

  // 알림 id (Pk)
  private Long id;

  // 알림 내용
  private String content;

  private String type;
  private LocalDateTime createdDateTime;


  public static NotificationDto entityToDto(
      NotificationEntity notificationEntity) {
    return NotificationDto.builder()
        .id(notificationEntity.getId())
        .type(notificationEntity.getNotificationType().toString())
        .content(notificationEntity.getContent())
        .createdDateTime(notificationEntity.getCreatedDateTime())
        .build();
  }
}

package com.zerobase.hoops.alarm.domain;

import com.zerobase.hoops.document.NotificationDocument;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {

  private String id;
  private String content;
  private String type;
  private OffsetDateTime createdDateTime;

  public static NotificationDto entityToDto(
      NotificationDocument notification) {
    return NotificationDto.builder()
        .id(notification.getId())
        .type(notification.getNotificationType().toString())
        .content(notification.getContent())
        .createdDateTime(notification.getCreatedDateTime())
        .build();
  }
}

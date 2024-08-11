package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.document.ParticipantGameDocument;
import com.zerobase.hoops.document.UserDocument;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipateGameDto {

  private String participantId;
  private ParticipantGameStatus status;
  private OffsetDateTime createdDateTime;
  private OffsetDateTime acceptedDateTime;
  private OffsetDateTime rejectedDateTime;
  private OffsetDateTime canceledDateTime;
  private OffsetDateTime withdrewDateTime;
  private OffsetDateTime kickoutDateTime;
  private OffsetDateTime deletedDateTime;

  private GameDocument gameDocument;
  private UserDocument userDocument;

  public static ParticipateGameDto fromDocument(
      ParticipantGameDocument participantGameDocument) {
    return ParticipateGameDto.builder()
        .participantId(participantGameDocument.getId())
        .status(participantGameDocument.getStatus())
        .createdDateTime(participantGameDocument.getCreatedDateTime())
        .acceptedDateTime(participantGameDocument.getAcceptedDateTime())
        .rejectedDateTime(participantGameDocument.getRejectedDateTime())
        .canceledDateTime(participantGameDocument.getCanceledDateTime())
        .withdrewDateTime(participantGameDocument.getWithdrewDateTime())
        .kickoutDateTime(participantGameDocument.getKickoutDateTime())
        .deletedDateTime(participantGameDocument.getDeletedDateTime())
        .gameDocument(participantGameDocument.getGame())
        .userDocument(participantGameDocument.getUser())
        .build();
  }

}

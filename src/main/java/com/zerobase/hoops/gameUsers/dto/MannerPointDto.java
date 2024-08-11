package com.zerobase.hoops.gameUsers.dto;

import static com.zerobase.hoops.util.Common.getNowDateTime;

import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.document.MannerPointDocument;
import com.zerobase.hoops.document.UserDocument;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MannerPointDto {

  @NotBlank
  private String receiverId;

  @NotBlank
  private String gameId;

  @NotNull
  @Min(1)
  @Max(5)
  private int point;

  public MannerPointDocument toDocument(
      UserDocument user, UserDocument receiver, GameDocument game,
      long mannerPointId) {
    return MannerPointDocument.builder()
        .id(Long.toString(mannerPointId))
        .point(this.point)
        .createdDateTime(getNowDateTime())
        .user(user)
        .receiver(receiver)
        .game(game)
        .build();
  }
}

package com.zerobase.hoops.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageConvertDto {

  @NotBlank
  private Long id;

  @NotBlank
  private String sender;

  @NotBlank
  private String content;

}

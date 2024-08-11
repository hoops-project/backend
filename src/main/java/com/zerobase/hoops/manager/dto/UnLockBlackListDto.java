package com.zerobase.hoops.manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnLockBlackListDto {

  @NotBlank
  private String blackUserId;
}

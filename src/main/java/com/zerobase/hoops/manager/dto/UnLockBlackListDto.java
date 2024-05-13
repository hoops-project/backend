package com.zerobase.hoops.manager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnLockBlackListDto {
  @NotNull
  @Min(1)
  private String blackUserId;
}

package com.zerobase.hoops.manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class BlackListDto {

  @NotBlank
  private String reportedId;
}

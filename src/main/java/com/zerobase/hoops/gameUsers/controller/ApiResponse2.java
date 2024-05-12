package com.zerobase.hoops.gameUsers.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ApiResponse2 {

  private String title;
  private String detail;

  public ApiResponse2 toEntity() {
    return ApiResponse2.builder()
        .title(this.title)
        .detail(this.detail)
        .build();
  }
}

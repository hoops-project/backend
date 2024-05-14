package com.zerobase.hoops.commonResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ApiResponse {

  private String title;
  private String detail;

  public ApiResponse toEntity() {
    return ApiResponse.builder()
        .title(this.title)
        .detail(this.detail)
        .build();
  }
}

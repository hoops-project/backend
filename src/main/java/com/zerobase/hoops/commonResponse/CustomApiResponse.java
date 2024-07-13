package com.zerobase.hoops.commonResponse;

import com.zerobase.hoops.commonResponse.swaggerSchema.SwaggerApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CustomApiResponse
    implements BasicApiResponse, SwaggerApiResponse {

  private String title;
  private String detail;

  public static CustomApiResponse of(String title, String detail) {
    return CustomApiResponse.builder()
        .title(title)
        .detail(detail)
        .build();
  }
}

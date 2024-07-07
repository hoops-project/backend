package com.zerobase.hoops.commonResponse;

import org.springframework.stereotype.Component;

@Component
public class ApiResponseFactory {

  public BasicApiResponse createSuccessResponse(String title) {
    return new CustomApiResponse(title, "Success");
  }

  public BasicApiResponse createSuccessWithDetailResponse(String title,
      String detail) {
    return new CustomApiResponse(title, detail);
  }
}

package com.zerobase.hoops.gameCreator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class CommonGameDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    String message;

    public Response toDto(String message) {
      return Response.builder()
          .message(message)
          .build();
    }
  }

}

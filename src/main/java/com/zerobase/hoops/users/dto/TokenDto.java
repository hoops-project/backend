package com.zerobase.hoops.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

  private String id;
  private String accessToken;
  private String refreshToken;
}

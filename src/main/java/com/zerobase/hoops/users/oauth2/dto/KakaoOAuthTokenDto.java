package com.zerobase.hoops.users.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoOAuthTokenDto {

  private String access_token;
  private String token_type;
  private String refresh_token;
  private String id_token;
  private Integer expires_in;
  private String scope;
  private Integer refresh_token_expires_in;

}

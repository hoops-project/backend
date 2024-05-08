package com.zerobase.hoops.users.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoUserInfoDto {

  private Long id;
  private String connected_at;
  private Properties properties;
  private KakaoAccount kakao_account;

  @Data
  public static class Properties {

    private String nickname;
  }

  @Data
  public static class KakaoAccount {

    private boolean profile_nickname_needs_agreement;
    private Profile profile;
    private boolean has_email;
    private boolean email_needs_agreement;
    @JsonProperty("is_email_valid")
    private boolean is_email_valid;
    @JsonProperty("is_email_verified")
    private boolean is_email_verified;
    private String email;
    private boolean has_gender;
    private boolean gender_needs_agreement;
    private String gender;

    @Data
    public static class Profile {

      @JsonProperty("is_default_nickname")
      private boolean is_default_nickname;
      private String nickname;

    }
  }

}

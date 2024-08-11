package com.zerobase.hoops.users.dto;

import com.zerobase.hoops.document.UserDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

  @Schema(description = "PK", example = "1", defaultValue = "1")
  private String id;

  @Schema(description = "아이디", example = "hoops", defaultValue = "hoops")
  private String loginId;

  @Schema(description = "비밀번호", example = "Hoops123$%^",
      defaultValue = "Hoops123$%^")
  private String password;

  @Schema(description = "이메일", example = "hoops@hoops.com",
      defaultValue = "hoops@hoops.com")
  private String email;

  @Schema(description = "이름", example = "차은우", defaultValue = "차은우")
  private String name;

  @Schema(description = "생년월일", example = "19900101",
      defaultValue = "19900101")
  private LocalDate birthday;

  @Schema(description = "성별", example = "MALE", defaultValue = "MALE")
  private String gender;

  @Schema(description = "별명", example = "농구의신", defaultValue = "농구의신")
  private String nickName;

  @Schema(description = "가입 일시", example = "2024-06-04T13:31:24+09:00",
      defaultValue = "2024-06-04T13:31:24+09:00")
  private OffsetDateTime createDate;

  @Schema(description = "탈퇴 일시", example = "2024-06-04T13:31:24+09:00",
      defaultValue = "2024-06-04T13:31:24+09:00")
  private OffsetDateTime deleteDate;

  @Schema(description = "플레이 스타일", example = "AGGRESSIVE",
      defaultValue = "AGGRESSIVE")
  private String playStyle;

  @Schema(description = "능력", example = "SHOOT", defaultValue = "SHOOT")
  private String ability;

  @Schema(description = "권한", example = "[\"ROLE_USER\"]",
      defaultValue = "[\"ROLE_USER\"]")
  private List<String> roles;

  public static UserDto fromDocument(UserDocument userDocument) {
    return UserDto.builder()
        .id(userDocument.getId())
        .loginId(userDocument.getLoginId())
        .password(userDocument.getPassword())
        .email(userDocument.getEmail())
        .name(userDocument.getName())
        .birthday(userDocument.getBirthday())
        .gender(String.valueOf(userDocument.getGender()))
        .nickName(userDocument.getNickName())
        .createDate(userDocument.getCreatedDateTime())
        .deleteDate(userDocument.getDeletedDateTime())
        .playStyle(String.valueOf(userDocument.getPlayStyle()))
        .ability(String.valueOf(userDocument.getAbility()))
        .roles(userDocument.getRoles())
        .build();
  }

}

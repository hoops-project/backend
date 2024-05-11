package com.zerobase.hoops.users.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class EditDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @Size(min = 2)
    private String name;
    @Size(min = 2)
    private String nickName;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*()])"
        + "[a-zA-Z0-9~!@#$%^&*()]{8,13}$")
    private String password;

    @JsonFormat(shape = Shape.STRING,
        pattern = "yyyyMMdd",
        timezone = "Asia/Seoul")
    @Past(message = "생년월일은 과거의 날짜만 입력 가능합니다.")
    private LocalDate birthday;

    private String gender;
    private String playStyle;
    private String ability;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {

    private Long userId;
    private String id;
    private String email;
    private String name;
    private LocalDate birthday;
    private String gender;
    private String nickName;
    private LocalDateTime crateDate;
    private String playStyle;
    private String ability;
    private List<String> roles;
    private String refreshToken;

    public static Response fromDto(UserDto userDto, String refreshToken) {
      return Response.builder()
          .userId(userDto.getUserId())
          .id(userDto.getId())
          .email(userDto.getEmail())
          .name(userDto.getName())
          .birthday(userDto.getBirthday())
          .gender(userDto.getGender())
          .nickName(userDto.getNickName())
          .crateDate(userDto.getCreateDate())
          .playStyle(userDto.getPlayStyle())
          .ability(userDto.getAbility())
          .roles(userDto.getRoles())
          .refreshToken(refreshToken)
          .build();
    }
  }

}

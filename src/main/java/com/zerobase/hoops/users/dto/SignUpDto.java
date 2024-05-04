package com.zerobase.hoops.users.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SignUpDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @NotBlank(message = "아이디를 입력하세요.")
    private String id;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*()])"
        + "[a-zA-Z0-9~!@#$%^&*()]{8,13}$")
    private String password;

    @NotBlank(message = "확인할 비밀번호를 입력하세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*()])"
        + "[a-zA-Z0-9~!@#$%^&*()]{8,13}$")
    private String passwordCheck;

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "이메일 형식에 맞게 입력하세요.")
    private String email;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    @JsonFormat(shape = Shape.STRING,
        pattern = "yyyyMMdd",
        timezone = "Asia/Seoul")
    @Past(message = "생년월일은 과거의 날짜만 입력 가능합니다.")
    private LocalDate birthday;

    @NotBlank(message = "성별을 선택하세요.")
    private String gender;

    @NotBlank(message = "별명을 입력하세요.")
    private String nickName;

    private String playStyle;
    private String ability;

    public static UserEntity toEntity(Request request) {
      return UserEntity.builder()
          .id(request.getId())
          .password(request.getPassword())
          .email(request.getEmail())
          .name(request.getName())
          .birthday(request.getBirthday())
          .gender(GenderType.valueOf(request.getGender()))
          .nickName(request.getNickName())
          .playStyle(PlayStyleType.valueOf(request.getPlayStyle()))
          .ability(AbilityType.valueOf(request.getAbility()))
          .roles(new ArrayList<>(List.of("ROLE_USER")))
          .build();
    }
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

    public static Response fromDto(UserDto userDto) {
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
          .build();
    }
  }

}

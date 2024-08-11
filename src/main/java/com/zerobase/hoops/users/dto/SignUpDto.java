package com.zerobase.hoops.users.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.zerobase.hoops.document.UserDocument;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
    @Schema(description = "아이디", example = "hoops", defaultValue = "hoops")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*()])"
        + "[a-zA-Z0-9~!@#$%^&*()]{8,13}$")
    @Schema(description = "비밀번호", example = "Hoops123$%^",
        defaultValue = "Hoops123$%^")
    private String password;

    @NotBlank(message = "확인할 비밀번호를 입력하세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*()])"
        + "[a-zA-Z0-9~!@#$%^&*()]{8,13}$")
    @Schema(description = "비밀번호 확인", example = "Hoops123$%^",
        defaultValue = "Hoops123$%^")
    private String passwordCheck;

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "이메일 형식에 맞게 입력하세요.")
    @Schema(description = "이메일", example = "hoops@hoops.com",
        defaultValue = "hoops@hoops.com")
    private String email;

    @NotBlank(message = "이름을 입력하세요.")
    @Schema(description = "이름", example = "차은우", defaultValue = "차은우")
    private String name;

    @JsonFormat(shape = Shape.STRING,
        pattern = "yyyyMMdd",
        timezone = "Asia/Seoul")
    @Past(message = "생년월일은 과거의 날짜만 입력 가능합니다.")
    @Schema(description = "생년월일", example = "19900101",
        defaultValue = "19900101")
    private LocalDate birthday;

    @NotBlank(message = "성별을 선택하세요.")
    @Schema(description = "성별", example = "MALE", defaultValue = "MALE")
    private String gender;

    @NotBlank(message = "별명을 입력하세요.")
    @Schema(description = "별명", example = "농구의신", defaultValue = "농구의신")
    private String nickName;

    @Schema(description = "플레이 스타일", example = "AGGRESSIVE",
        defaultValue = "AGGRESSIVE")
    private String playStyle;
    @Schema(description = "능력", example = "SHOOT", defaultValue = "SHOOT")
    private String ability;

    public static UserDocument toDocument(Request request, long userId) {

      // 포맷 정의 (타임존 오프셋 포함)
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

      // 현재 시간을 OffsetDateTime으로 생성
      OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.ofHours(9)); // KST는 UTC+9

      // 문자열로 포맷
      String formattedDateTime = offsetDateTime.format(formatter);

      // 문자열을 OffsetDateTime으로 파싱
      OffsetDateTime dateTime = OffsetDateTime.parse(formattedDateTime, formatter);

      return UserDocument.builder()
          .id(Long.toString(userId))
          .loginId(request.getLoginId())
          .password(request.getPassword())
          .email(request.getEmail())
          .name(request.getName())
          .birthday(request.getBirthday())
          .gender(GenderType.valueOf(request.getGender()))
          .nickName(request.getNickName())
          .playStyle(PlayStyleType.valueOf(request.getPlayStyle()))
          .ability(AbilityType.valueOf(request.getAbility()))
          .roles(new ArrayList<>(List.of("ROLE_USER")))
          .createdDateTime(dateTime)
          .build();
    }

  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {

    @Schema(description = "PK", example = "1", defaultValue = "1")
    private String id;

    @Schema(description = "아이디", example = "hoops", defaultValue = "hoops")
    private String loginId;

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
    private OffsetDateTime crateDate;

    @Schema(description = "플레이 스타일", example = "AGGRESSIVE",
        defaultValue = "AGGRESSIVE")
    private String playStyle;

    @Schema(description = "능력", example = "SHOOT", defaultValue = "SHOOT")
    private String ability;

    @Schema(description = "권한", example = "[\"ROLE_USER\"]",
        defaultValue = "[\"ROLE_USER\"]")
    private List<String> roles;

    public static Response fromDto(UserDto userDto) {
      return Response.builder()
          .id(userDto.getId())
          .loginId(userDto.getLoginId())
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

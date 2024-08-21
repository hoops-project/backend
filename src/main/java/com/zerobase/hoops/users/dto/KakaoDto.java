package com.zerobase.hoops.users.dto;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.users.type.GenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class KakaoDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @NotBlank(message = "아이디를 입력하세요.")
    private String loginId;

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "이메일 형식에 맞게 입력하세요.")
    private String email;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    @NotBlank(message = "성별을 선택하세요.")
    private String gender;

    @NotBlank(message = "별명을 입력하세요.")
    private String nickName;

    public static UserEntity toEntity(Request request) {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
      return UserEntity.builder()
          .loginId(request.getLoginId())
          .password(encoder.encode("kakao"))
          .email(request.getEmail())
          .name(request.getName())
          .birthday(LocalDate.now())
          .gender(GenderType.valueOf(request.getGender()))
          .nickName(request.getNickName())
          .roles(new ArrayList<>(List.of(("ROLE_USER"))))
          .emailAuth(true)
          .build();
    }
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {

    @Schema(description = "PK", example = "1", defaultValue = "1")
    private Long id;

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

    @Schema(description = "가입 일시", example = "2024-06-04T13:31:24.255686",
        defaultValue = "2024-06-04T13:31:24.255686")
    private LocalDateTime crateDate;

    @Schema(description = "플레이 스타일", example = "AGGRESSIVE",
        defaultValue = "AGGRESSIVE")
    private String playStyle;

    @Schema(description = "능력", example = "SHOOT", defaultValue = "SHOOT")
    private String ability;

    @Schema(description = "권한", example = "[\"ROLE_USER\"]",
        defaultValue = "[\"ROLE_USER\"]")
    private List<String> roles;

    @Schema(description = "refresh-token", example = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTcxOTA0MDA1MSwiZXhwIjoxNzE5MDQzNjUxfQ.YjAmy2aB0_W3cx6bGT3-WA25hiq1axfHUlAeoAV8Y9LIYO86U8iKF6JdPWYLskrx",
        defaultValue = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTcxOTA0MDA1MSwiZXhwIjoxNzE5MDQzNjUxfQ.YjAmy2aB0_W3cx6bGT3-WA25hiq1axfHUlAeoAV8Y9LIYO86U8iKF6JdPWYLskrx")
    private String refreshToken;

    public static Response fromDto(UserDto userDto, String refreshToken) {
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
          .refreshToken(refreshToken)
          .build();
    }
  }
}

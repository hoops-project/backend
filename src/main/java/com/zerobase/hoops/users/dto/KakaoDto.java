package com.zerobase.hoops.users.dto;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.users.type.GenderType;
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
    private String id;

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
          .id(request.getId())
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

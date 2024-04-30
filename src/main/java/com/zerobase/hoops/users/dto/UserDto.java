package com.zerobase.hoops.users.dto;

import com.zerobase.hoops.entity.UserEntity;
import java.time.LocalDateTime;
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

  private int userId;

  private String id;
  private String password;

  private String email;
  private String name;
  private String birthday;
  private String gender;
  private String nickName;
  private LocalDateTime createDate;
  private LocalDateTime deleteDate;
  private String playStyle;
  private String ability;
  private List<String> roles;

  public static UserDto fromEntity(UserEntity userEntity) {
    return UserDto.builder()
        .userId(userEntity.getUserId())
        .id(userEntity.getId())
        .password(userEntity.getPassword())
        .email(userEntity.getEmail())
        .name(userEntity.getName())
        .birthday(userEntity.getBirthday())
        .gender(userEntity.getGender())
        .nickName(userEntity.getNickName())
        .createDate(userEntity.getCreateDate())
        .deleteDate(userEntity.getDeleteDate())
        .playStyle(userEntity.getPlayStyle())
        .ability(userEntity.getAbility())
        .roles(userEntity.getRoles())
        .build();
  }

}

package com.zerobase.hoops.users.service;

import static org.junit.jupiter.api.Assertions.*;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.users.dto.EditDto;
import com.zerobase.hoops.users.dto.LogInDto;
import com.zerobase.hoops.users.dto.SignUpDto;
import com.zerobase.hoops.users.dto.TokenDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.repository.UserRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthServiceTest {

  @Autowired
  AuthService authService;

  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

  @BeforeEach
  void insertTestUser() {
    userService.signUpUser(SignUpDto.Request.builder()
        .id("basketman")
        .password("Abcdefg123$%")
        .passwordCheck("Abcdefg123$%")
        .email("testMail@hoops.com")
        .name("농구공")
        .birthday(LocalDate.parse("18900101", DateTimeFormatter.ofPattern(
            "yyyyMMdd")))
        .gender("MALE")
        .nickName("농구짱")
        .playStyle("AGGRESSIVE")
        .ability("PASS")
        .build());

    userService.signUpUser(SignUpDto.Request.builder()
        .id("testUser")
        .password("Abcdefg123$%")
        .passwordCheck("Abcdefg123$%")
        .email("test@hoops.com")
        .name("테스트")
        .birthday(LocalDate.parse("19900101", DateTimeFormatter.ofPattern(
            "yyyyMMdd")))
        .gender("MALE")
        .nickName("별명")
        .playStyle("BALANCE")
        .ability("SHOOT")
        .build());

    UserEntity user = userRepository.findById("testUser")
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    user.confirm();
    userRepository.save(user);
    System.out.println("인증 결과 : " + user.isEmailAuth());
  }

  @Test
  @DisplayName("LogIn_User_Success")
  void logInUserTestSuccess() {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // given
    LogInDto.Request request = LogInDto.Request.builder()
        .id("testUser")
        .password("Abcdefg123$%")
        .build();

    // when
    UserDto user = authService.logInUser(request);

    // then
    assertEquals(user.getUserId(), 12);
    assertEquals(user.getId(), "testUser");
    assertTrue(passwordEncoder.matches(
        "Abcdefg123$%", user.getPassword())
    );
    assertEquals(user.getEmail(), "test@hoops.com");
    assertEquals(user.getName(), "테스트");
    assertEquals(user.getBirthday(), LocalDate
        .parse("19900101", DateTimeFormatter.ofPattern("yyyyMMdd")));
    assertEquals(user.getGender(), "MALE");
    assertEquals(user.getNickName(), "별명");
    assertEquals(user.getPlayStyle(), "BALANCE");
    assertEquals(user.getAbility(), "SHOOT");
    for (int i = 0; i < user.getRoles().size(); i++) {
      assertEquals(user.getRoles().get(i), "ROLE_USER");
    }
  }

  @Test
  @DisplayName("LogIn_User_Fail_User_Not_Found")
  void logInUserFailTest_UserNotFound() {
    // given
    LogInDto.Request request = LogInDto.Request.builder()
        .id("nouser")
        .password("Abcdefg123$%")
        .build();

    // when
    Throwable exception = assertThrows(CustomException.class, () ->
        authService.logInUser(request));

    // then
    assertThrows(CustomException.class, () -> authService.logInUser(request));
    assertEquals("아이디가 존재하지 않습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("LogIn_User_Fail_Not_Matched_Password")
  void logInUserFailTest_NotMatchedPassword() {
    // given
    LogInDto.Request request = LogInDto.Request.builder()
        .id("testUser")
        .password("Abcdefg123$")
        .build();

    // when
    Throwable exception = assertThrows(CustomException.class, () ->
        authService.logInUser(request));

    // then
    assertThrows(CustomException.class, () -> authService.logInUser(request));
    assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("LogIn_User_Fail_Not_Confirmed_Auth")
  void logInUserFailTest_NotConfirmedAuth() {
    // given
    LogInDto.Request request = LogInDto.Request.builder()
        .id("basketman")
        .password("Abcdefg123$%")
        .build();

    // when
    Throwable exception = assertThrows(CustomException.class, () ->
        authService.logInUser(request));

    // then
    assertThrows(CustomException.class, () -> authService.logInUser(request));
    assertEquals("인증되지 않은 회원입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Get_User_Info_Success")
  void getUserInfoTest() {
    // given
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserDto user = authService.logInUser(LogInDto.Request.builder()
        .id("testUser")
        .password("Abcdefg123$%")
        .build());
    TokenDto token = authService.getToken(user);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token.getAccessToken());

    UserEntity userEntity = userRepository.findById("testUser")
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    // when
    UserDto userInfo = authService.getUserInfo(request, userEntity);

    // then
    assertEquals(userInfo.getUserId(), 12);
    assertEquals(userInfo.getId(), "testUser");
    assertTrue(passwordEncoder.matches(
        "Abcdefg123$%", userInfo.getPassword())
    );
    assertEquals(userInfo.getEmail(), "test@hoops.com");
    assertEquals(userInfo.getName(), "테스트");
    assertEquals(userInfo.getBirthday(), LocalDate
        .parse("19900101", DateTimeFormatter.ofPattern("yyyyMMdd")));
    assertEquals(userInfo.getGender(), "MALE");
    assertEquals(userInfo.getNickName(), "별명");
    assertEquals(userInfo.getPlayStyle(), "BALANCE");
    assertEquals(userInfo.getAbility(), "SHOOT");
    for (int i = 0; i < userInfo.getRoles().size(); i++) {
      assertEquals(userInfo.getRoles().get(i), "ROLE_USER");
    }
  }

  @Test
  @DisplayName("Edit_User_Info_Success")
  void editUserInfoTest() {
    // given
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserDto user = authService.logInUser(LogInDto.Request.builder()
        .id("testUser")
        .password("Abcdefg123$%")
        .build());
    TokenDto token = authService.getToken(user);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token.getAccessToken());

    UserEntity userEntity = userRepository.findById("testUser")
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    EditDto.Request requestEdit = EditDto.Request.builder()
        .password("TransPa1@#")
        .nickName("변경")
        .gender("FEMALE")
        .build();

    // when
    UserDto edit = authService.editUserInfo(request, requestEdit, userEntity);

    // then
    assertEquals(edit.getUserId(), 12);
    assertEquals(edit.getId(), "testUser");
    assertTrue(passwordEncoder.matches(
        "TransPa1@#", edit.getPassword())
    );
    assertEquals(edit.getEmail(), "test@hoops.com");
    assertEquals(edit.getName(), "테스트");
    assertEquals(edit.getBirthday(), LocalDate
        .parse("19900101", DateTimeFormatter.ofPattern("yyyyMMdd")));
    assertEquals(edit.getGender(), "FEMALE");
    assertEquals(edit.getNickName(), "변경");
    assertEquals(edit.getPlayStyle(), "BALANCE");
    assertEquals(edit.getAbility(), "SHOOT");
    for (int i = 0; i < edit.getRoles().size(); i++) {
      assertEquals(edit.getRoles().get(i), "ROLE_USER");
    }
  }
}
package com.zerobase.hoops.users.service;

import static org.junit.jupiter.api.Assertions.*;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.users.dto.SignUpDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.users.repository.EmailRepository;
import com.zerobase.hoops.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Transactional
class UserServiceTest {

  @Autowired
  UserService userService;

  @Autowired
  EmailRepository emailRepository;

  @Autowired
  UserRepository userRepository;

  PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @BeforeEach
  void insertTestUser() {
    userService.signUpUser(SignUpDto.Request.builder()
        .id("testUser")
        .password("Abcdefg123$%")
        .passwordCheck("Abcdefg123$%")
        .email("test@hoops.com")
        .name("테스트")
        .birthday(
            LocalDate.parse("19900101",
                DateTimeFormatter.ofPattern("yyyyMMdd")))
        .gender("MALE")
        .nickName("별명")
        .playStyle("AGGRESSIVE")
        .ability("SHOOT")
        .build());
  }

  @Test
  @DisplayName("User_SignUp_Success")
  void signUpUserTest() {
    // given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .id("successUser")
        .password("Abcdefg123$%")
        .passwordCheck("Abcdefg123$%")
        .email("success@hoops.com")
        .name("성공")
        .birthday(
            LocalDate.parse("19900101",
                DateTimeFormatter.ofPattern("yyyyMMdd")))
        .gender("MALE")
        .nickName("별명2")
        .playStyle("BALANCE")
        .ability("DRIBBLE")
        .build();

    // when
    UserDto userDto = userService.signUpUser(request);

    // then
    assertEquals(userDto.getId(), "successUser");
    assertTrue(passwordEncoder.matches("Abcdefg123$%",
        userDto.getPassword()));
    assertEquals(userDto.getEmail(), "success@hoops.com");
    assertEquals(userDto.getName(), "성공");
    assertEquals(userDto.getBirthday(), LocalDate.parse(
        "19900101", DateTimeFormatter.ofPattern("yyyyMMdd")));
    assertEquals(userDto.getGender(), "MALE");
    assertEquals(userDto.getNickName(), "별명2");
    assertEquals(userDto.getPlayStyle(), "BALANCE");
    assertEquals(userDto.getAbility(), "DRIBBLE");
    for (int i = 0; i < userDto.getRoles().size(); i++) {
      assertEquals(userDto.getRoles().get(i), "ROLE_USER");
    }
  }

  @Test
  @DisplayName("User_SignUp_Fail_Not_Matched_Password")
  void signUpUserFailTest_NotMatchedPassword() {
    // given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .id("successUser")
        .password("Abcdefg123$%")
        .passwordCheck("aBCDEfg456&*")
        .email("fail@hoops.com")
        .name("성공")
        .birthday(
            LocalDate.parse("19900101",
                DateTimeFormatter.ofPattern("yyyyMMdd")))
        .gender("MALE")
        .nickName("실패")
        .playStyle("AGGRESSIVE")
        .ability("SHOOT")
        .build();

    // when
    assertThrows(CustomException.class, () -> userService.signUpUser(request));
  }

  @Test
  @DisplayName("User_IdCheck_Success")
  void idCheckTest() {
    // given
    String id = "success";

    // when
    boolean idCheck = userService.idCheck(id);

    // then
    assertTrue(idCheck);
  }

  @Test
  @DisplayName("User_IdCheck_Fail")
  void idCheckFailTest() {
    // given
    String id = "testUser";

    // when
    assertThrows(CustomException.class, () -> userService.idCheck(id));
  }

  @Test
  @DisplayName("User_EmailCheck_Success")
  void emailCheckTest() {
    // given
    String email = "email@hoops.com";

    // when
    boolean emailCheck = userService.emailCheck(email);

    // then
    assertTrue(emailCheck);
  }

  @Test
  @DisplayName("User_EmailCheck_Fail")
  void emailCheckFailTest() {
    // given
    String email = "test@hoops.com";

    // when
    assertThrows(CustomException.class, () -> userService.emailCheck(email));
  }

  @Test
  @DisplayName("User_NickNameCheck_Success")
  void nickNameCheckTest() {
    // given
    String nickName = "성공";

    // when
    boolean nickNameCheck = userService.nickNameCheck(nickName);

    // then
    assertTrue(nickNameCheck);
  }

  @Test
  @DisplayName("User_NickNameCheck_Fail")
  void nickNameCheckFailTest() {
    // given
    String nickName = "별명";

    // when
    assertThrows(CustomException.class, () -> userService.nickNameCheck(nickName));
  }

  @Test
  @DisplayName("User_ConfirmEmail_Success")
  void confirmEmailTest() {
    // given
    String id = "testUser";
    String email = "test@hoops.com";
    String certificationNumber = "123456";

    // when
    emailRepository.saveCertificationNumber(email, certificationNumber);
    userService.confirmEmail(id, email, certificationNumber);

    // then
    UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    assertTrue(user.isEmailAuth());
  }

  @Test
  @DisplayName("User_ConfirmEmail_Fail_Wrong_Email")
  void confirmEmailRailTest_WrongEmail() {
    // given
    String id = "testUser";
    String email = "testFail@hoops.com";
    String certificationNumber = "123456";

    // when
    Throwable exception = assertThrows(CustomException.class, () ->
        userService.confirmEmail(id, email, certificationNumber));

    // then
    assertEquals("잘못된 이메일 주소입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_ConfirmEmail_Fail_Invalid_Number")
  void confirmEmailFailTest_InvalidNumber() {
    // given
    String id = "testUser";
    String email = "test@hoops.com";
    String certificationNumber = "123456";

    // when
    Throwable exception = assertThrows(CustomException.class, () ->
      userService.confirmEmail(id, email, certificationNumber));

    // then
    assertEquals("유효하지 않은 인증번호입니다.", exception.getMessage());
  }
}
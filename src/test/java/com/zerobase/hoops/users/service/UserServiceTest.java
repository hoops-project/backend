package com.zerobase.hoops.users.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.users.dto.SignUpDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.provider.EmailProvider;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.repository.redis.EmailRepository;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  UserService userService;

  @Mock
  UserRepository userRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  EmailProvider emailProvider;

  @Mock
  EmailRepository emailRepository;

  UserEntity user;

  @BeforeEach
  void setUp() {
    // Initialize your test setup here if needed
    user = UserEntity.builder()
        .userId(1L)
        .id("test")
        .password("Hoops!@#456")
        .email("test@hoops.com")
        .name("테스트")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("테스트별명")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(new ArrayList<>(List.of("ROLE_USER")))
        .createdDateTime(LocalDateTime.now())
        .emailAuth(false)
        .build();
  }

  @Test
  @DisplayName("User_SignUp_Success")
  void signUpUserTest() {
    // given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .id("signUpTest")
        .password("Hoops!@#456")
        .passwordCheck("Hoops!@#456")
        .email("sign@hoops.com")
        .name("성공")
        .birthday(
            LocalDate.parse("19900101",
                DateTimeFormatter.ofPattern("yyyyMMdd")))
        .gender("MALE")
        .nickName("별명")
        .playStyle("BALANCE")
        .ability("DRIBBLE")
        .build();

    UserEntity user = SignUpDto.Request.toEntity(request);

    when(emailProvider.sendCertificationMail(
        any(), any(), any())).thenReturn(true);

    when(userRepository.save(any(UserEntity.class))).thenReturn(user);

    // when
    UserDto sinUpUser = userService.signUpUser(request);

    // then
    assertEquals(sinUpUser.getId(), "signUpTest");
    assertEquals(sinUpUser.getEmail(), "sign@hoops.com");
    assertEquals(sinUpUser.getName(), "성공");
    assertEquals(sinUpUser.getBirthday(), LocalDate.parse(
        "19900101", DateTimeFormatter.ofPattern("yyyyMMdd")));
    assertEquals(sinUpUser.getGender(), "MALE");
    assertEquals(sinUpUser.getNickName(), "별명");
    assertEquals(sinUpUser.getPlayStyle(), "BALANCE");
    assertEquals(sinUpUser.getAbility(), "DRIBBLE");
    for (int i = 0; i < sinUpUser.getRoles().size(); i++) {
      assertEquals(sinUpUser.getRoles().get(i), "ROLE_USER");
    }

  }

  @Test
  @DisplayName("User_SignUp_Fail_PasswordMismatch")
  void signUpUserTest_PasswordMismatch() {
    // given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .id("signUpTest")
        .password("Hoops!@#456")
        .passwordCheck("MismatchPassword")
        .email("sign@hoops.com")
        .name("성공")
        .birthday(LocalDate.parse("19900101",
            DateTimeFormatter.ofPattern("yyyyMMdd")))
        .gender("MALE")
        .nickName("별명")
        .playStyle("BALANCE")
        .ability("DRIBBLE")
        .build();

    //when
    Throwable exception = assertThrows(CustomException.class,
        () -> userService.signUpUser(request));

    // then
    assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_SignUp_Fail_DuplicatedId")
  void signUpUserTest_DuplicatedId() {
    // given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .id("test")
        .password("Hoops!@#456")
        .passwordCheck("Hoops!@#456")
        .email("sign@hoops.com")
        .name("성공")
        .birthday(LocalDate.parse("19900101",
            DateTimeFormatter.ofPattern("yyyyMMdd")))
        .gender("MALE")
        .nickName("별명")
        .playStyle("BALANCE")
        .ability("DRIBBLE")
        .build();

    // when
    when(userRepository.existsByIdAndDeletedDateTimeNull(
        request.getId())).thenReturn(true);
    Throwable exception = assertThrows(CustomException.class,
        () -> userService.signUpUser(request));

    // then
    assertEquals("이미 사용 중인 아이디입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_SignUp_Fail_DuplicatedEmail")
  void signUpUserTest_DuplicatedEmail() {
    // given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .id("signUpTest")
        .password("Hoops!@#456")
        .passwordCheck("Hoops!@#456")
        .email("test@hoops.com") // already existing email
        .name("성공")
        .birthday(LocalDate.parse("19900101",
            DateTimeFormatter.ofPattern("yyyyMMdd")))
        .gender("MALE")
        .nickName("별명")
        .playStyle("BALANCE")
        .ability("DRIBBLE")
        .build();

    // when
    when(userRepository.existsByEmailAndDeletedDateTimeNull(
        request.getEmail())).thenReturn(true);
    Throwable exception = assertThrows(CustomException.class,
        () -> userService.signUpUser(request));

    // then
    assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_SignUp_Fail_DuplicatedNickName")
  void signUpUserTest_DuplicatedNickName() {
    // given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .id("signUpTest")
        .password("Hoops!@#456")
        .passwordCheck("Hoops!@#456")
        .email("sign@hoops.com")
        .name("성공")
        .birthday(LocalDate.parse("19900101",
            DateTimeFormatter.ofPattern("yyyyMMdd")))
        .gender("MALE")
        .nickName("테스트별명")
        .playStyle("BALANCE")
        .ability("DRIBBLE")
        .build();

    // when
    when(userRepository.existsByNickNameAndDeletedDateTimeNull(
        request.getNickName())).thenReturn(true);
    Throwable exception = assertThrows(CustomException.class,
        () -> userService.signUpUser(request));

    // then
    assertEquals("이미 사용 중인 별명입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_SignUp_Fail_EmailSendFail")
  void signUpUserTest_EmailSendFail() {
    // given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .id("signUpTest")
        .password("Hoops!@#456")
        .passwordCheck("Hoops!@#456")
        .email("sign@hoops.com")
        .name("성공")
        .birthday(LocalDate.parse("19900101",
            DateTimeFormatter.ofPattern("yyyyMMdd")))
        .gender("MALE")
        .nickName("별명")
        .playStyle("BALANCE")
        .ability("DRIBBLE")
        .build();

    when(emailProvider.sendCertificationMail(any(), any(), any())).thenReturn(
        false);

    // then
    assertThrows(CustomException.class, () -> userService.signUpUser(request));
  }

  @Test
  @DisplayName("User_IdCheck_Success")
  void idCheckTest() {
    // given
    String id = "id";

    // when
    when(userRepository.existsByIdAndDeletedDateTimeNull(id)).thenReturn(false);

    // then
    assertTrue(userService.idCheck(id));
  }

  @Test
  @DisplayName("User_IdCheck_Fail_DuplicatedId")
  void idCheckTest_DuplicatedId() {
    // given
    String duplicatedId = "duplicatedId";

    // when
    when(userRepository.existsByIdAndDeletedDateTimeNull(
        duplicatedId)).thenReturn(true);
    Throwable exception = assertThrows(CustomException.class,
        () -> userService.idCheck(duplicatedId));

    // then
    assertEquals("이미 사용 중인 아이디입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_EmailCheck_Success")
  void emailCheckTest() {
    // given
    String email = "email";
    String duplicatedEmail = "duplicatedEmail";

    // when
    when(userRepository.existsByEmailAndDeletedDateTimeNull(email)).thenReturn(
        false);
    when(userRepository.existsByEmailAndDeletedDateTimeNull(
        duplicatedEmail)).thenReturn(true);

    // then
    assertThrows(CustomException.class,
        () -> userService.emailCheck(duplicatedEmail));
    assertTrue(userService.emailCheck(email));
  }

  @Test
  @DisplayName("User_EmailCheck_Fail_DuplicatedEmail")
  void emailCheckTest_DuplicatedEmail() {
    // given
    String duplicatedEmail = "duplicatedEmail";

    // when
    when(userRepository.existsByEmailAndDeletedDateTimeNull(
        duplicatedEmail)).thenReturn(true);
    Throwable exception = assertThrows(CustomException.class,
        () -> userService.emailCheck(duplicatedEmail));

    // then
    assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_NickNameCheck_Success")
  void nickNameCheckTest() {
    // given
    String nickName = "nickName";
    String duplicatedNickName = "duplicatedNickName";

    // when
    when(userRepository.existsByNickNameAndDeletedDateTimeNull(
        nickName)).thenReturn(false);
    when(userRepository.existsByNickNameAndDeletedDateTimeNull(
        duplicatedNickName)).thenReturn(true);

    // then
    assertThrows(CustomException.class,
        () -> userService.nickNameCheck(duplicatedNickName));
    assertTrue(userService.nickNameCheck(nickName));
  }

  @Test
  @DisplayName("User_NickNameCheck_Fail_DuplicatedNickName")
  void nickNameCheckTest_DuplicatedNickName() {
    // given
    String duplicatedNickName = "duplicatedNickName";

    // when
    when(userRepository.existsByNickNameAndDeletedDateTimeNull(
        duplicatedNickName)).thenReturn(true);
    Throwable exception = assertThrows(CustomException.class,
        () -> userService.nickNameCheck(duplicatedNickName));

    // then
    assertEquals("이미 사용 중인 별명입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_ConfirmEmail_Success")
  void confirmEmailTest_Success() {
    // given
    String id = user.getId();
    String email = user.getEmail();
    String certificationNumber = "certificationNumber";

    // when
    when(emailRepository.hasKey(anyString())).thenReturn(true);
    when(emailRepository.getCertificationNumber(anyString())).thenReturn(
        certificationNumber);
    when(userRepository.findByIdAndDeletedDateTimeNull(id)).thenReturn(
        Optional.of(user));

    // then
    userService.confirmEmail(id, email, certificationNumber);
    assertTrue(user.isEmailAuth());
    assertThrows(CustomException.class, () -> userService.confirmEmail(id,
        email, "wrongCertificationNumber"));
  }

  @Test
  @DisplayName("User_ConfirmEmail_Fail_InvalidEmail")
  void confirmEmailTest_InvalidEmail() {
    // given
    String id = "id";
    String invalidEmail = "invalidEmail@test.com";
    String certificationNumber = "certificationNumber";

    // when
    when(emailRepository.hasKey(invalidEmail)).thenReturn(false);

    Throwable exception = assertThrows(CustomException.class,
        () -> userService.confirmEmail(id, invalidEmail, certificationNumber));

    // then
    assertEquals("해당 이메일로 발송된 인증번호가 없습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_ConfirmEmail_Fail_InvalidCertificationNumber")
  void confirmEmailTest_InvalidCertificationNumber() {
    // given
    String id = "id";
    String email = "email@test.com";
    String certificationNumber = "certificationNumber";
    String invalidCertificationNumber = "invalidCertificationNumber";

    // when
    when(emailRepository.hasKey(email)).thenReturn(true);
    when(emailRepository.getCertificationNumber(email)).thenReturn(
        certificationNumber);

    Throwable exception = assertThrows(CustomException.class,
        () -> userService.confirmEmail(id, email, invalidCertificationNumber));

    // then
    assertEquals("인증번호가 일치하지 않습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_findId_Success")
  void findIdTest() {
    // given
    String existingEmail = "existingEmail@test.com";
    String nonExistingEmail = "nonExistingEmail@test.com";
    String expectedId = "expectedId";

    UserEntity existingUser = UserEntity.builder().id(expectedId).build();

    // when
    when(userRepository.findByEmailAndDeletedDateTimeNull(
        existingEmail)).thenReturn(Optional.of(existingUser));
    when(userRepository.findByEmailAndDeletedDateTimeNull(
        nonExistingEmail)).thenReturn(Optional.empty());

    // then
    assertEquals(expectedId, userService.findId(existingEmail));
    assertThrows(CustomException.class,
        () -> userService.findId(nonExistingEmail));
  }

  @Test
  @DisplayName("User_findId_Fail_NonExistingEmail")
  void findIdTest_NonExistingEmail() {
    // given
    String nonExistingEmail = "nonExistingEmail@test.com";

    // when
    when(userRepository.findByEmailAndDeletedDateTimeNull(
        nonExistingEmail)).thenReturn(Optional.empty());
    Throwable exception = assertThrows(CustomException.class,
        () -> userService.findId(nonExistingEmail));

    // then
    assertEquals("아이디가 존재하지 않습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_findPassword_Success")
  void findPasswordTest() throws NoSuchAlgorithmException {
    // given
    String existingId = "existingId";

    UserEntity existingUser = UserEntity.builder().id(existingId)
        .email("existingEmail@test.com").build();

    // when
    when(userRepository.findByIdAndDeletedDateTimeNull(existingId)).thenReturn(
        Optional.of(existingUser));
    when(emailProvider.sendTemporaryPasswordMail(anyString(),
        anyString())).thenReturn(true);

    // then
    assertTrue(userService.findPassword(existingId));
  }

  @Test
  @DisplayName("User_findPassword_Fail_NonExistingId")
  void findPasswordTest_NonExistingId() {
    // given
    String nonExistingId = "nonExistingId";

    // when
    when(userRepository.findByIdAndDeletedDateTimeNull(
        nonExistingId)).thenReturn(Optional.empty());
    Throwable exception = assertThrows(CustomException.class,
        () -> userService.findPassword(nonExistingId));

    // then
    assertEquals("아이디가 존재하지 않습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("User_findPassword_Fail_EmailSendFail")
  void findPasswordTest_EmailSendFail() {
    // given
    String existingId = "existingId";

    UserEntity existingUser = UserEntity.builder().id(existingId)
        .email("existingEmail@test.com").build();

    // when
    when(userRepository.findByIdAndDeletedDateTimeNull(existingId)).thenReturn(
        Optional.of(existingUser));
    when(emailProvider.sendTemporaryPasswordMail(any(), any())).thenReturn(
        false);
    Throwable exception = assertThrows(CustomException.class,
        () -> userService.findPassword(existingId));

    // then
    assertEquals("메일 발송에 실패하였습니다.", exception.getMessage());
  }
}
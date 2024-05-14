package com.zerobase.hoops.users.service;

import static com.zerobase.hoops.exception.ErrorCode.NOT_FOUND_APPLY_FRIEND;
import static org.junit.jupiter.api.Assertions.*;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.friends.dto.FriendDto.AcceptRequest;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyRequest;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.service.FriendService;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateRequest;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.service.GameService;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.gameUsers.service.GameUserService;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.dto.EditDto;
import com.zerobase.hoops.users.dto.LogInDto;
import com.zerobase.hoops.users.dto.SignUpDto;
import com.zerobase.hoops.users.dto.TokenDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthServiceTest {

  @Autowired
  AuthService authService;
  @Autowired
  TokenProvider tokenProvider;
  @Autowired
  UserService userService;
  @Autowired
  GameService gameService;
  @Autowired
  GameUserService gameUserService;
  @Autowired
  FriendService friendService;
  @Autowired
  UserRepository userRepository;
  @Autowired
  GameRepository gameRepository;
  @Autowired
  ParticipantGameRepository participantGameRepository;
  @Autowired
  FriendRepository friendRepository;

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

    UserEntity user = userRepository.findById("basketman")
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    user.confirm();
    userRepository.save(user);
    System.out.println("인증 결과 : " + user.isEmailAuth());
    UserEntity testUser = userRepository.findById("testUser")
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    testUser.confirm();
    userRepository.save(testUser);
    System.out.println("인증 결과 : " + testUser.isEmailAuth());
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

  @Test
  @DisplayName("Deactivate_User_Success")
  void deactivateUserTest() {
    // given
    UserDto user = authService.logInUser(LogInDto.Request.builder()
        .id("basketman")
        .password("Abcdefg123$%")
        .build());
    TokenDto token = authService.getToken(user);

    Authentication auth = tokenProvider.getAuthentication(
        token.getAccessToken());
    SecurityContextHolder.getContext().setAuthentication(auth);

    gameService.createGame(CreateRequest.builder()
        .title("테스트 경기")
        .content("테스트 내용")
        .headCount(10L)
        .fieldStatus(FieldStatus.OUTDOOR)
        .gender(Gender.ALL)
        .startDateTime(LocalDateTime.of(2024, 5, 15, 12, 0, 0))
        .inviteYn(true)
        .address("서울 종로구")
        .latitude(12.33)
        .longitude(11.33)
        .matchFormat(MatchFormat.FIVEONFIVE)
        .build());

    UserDto testUser = authService.logInUser(LogInDto.Request.builder()
        .id("testUser")
        .password("Abcdefg123$%")
        .build());
    TokenDto testUserToken = authService.getToken(testUser);

    Authentication testUserAuth =
        tokenProvider.getAuthentication(testUserToken.getAccessToken());
    SecurityContextHolder.getContext().setAuthentication(testUserAuth);

    gameUserService.participateInGame(1L);

    friendService.applyFriend(ApplyRequest.builder()
        .friendUserId(11L)
        .build());

    TokenDto reUsertoken = authService.getToken(user);

    Authentication reUserAuth =
        tokenProvider.getAuthentication(reUsertoken.getAccessToken());
    SecurityContextHolder.getContext().setAuthentication(reUserAuth);

    friendService.acceptFriend(AcceptRequest.builder()
        .friendId(1L)
        .build());

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization",
        "Bearer " + reUsertoken.getAccessToken());
    request.addHeader("refreshToken", reUsertoken.getRefreshToken());

    UserEntity userEntity = userRepository.findById("basketman")
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    // when
    authService.deactivateUser(request, userEntity);

    // then
    GameEntity resultGame = gameRepository.findById(1L)
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
    List<ParticipantGameEntity> participantGame =
        participantGameRepository
            .findByStatusAndGameEntityGameId(ParticipantGameStatus.DELETE, 1L);
    FriendEntity resultFriends =
        friendRepository.findByFriendIdAndStatus(1L,
                FriendStatus.DELETE)
            .orElseThrow(() -> new CustomException(NOT_FOUND_APPLY_FRIEND));

    FriendEntity resultAppliedFriends =
        friendRepository.findByFriendIdAndStatus(2L,
                FriendStatus.DELETE)
            .orElseThrow(() -> new CustomException(NOT_FOUND_APPLY_FRIEND));

    System.out.println("participantGame size : " + participantGame.size());
    assertNotNull(resultGame.getDeletedDateTime());
    assertEquals(participantGame.get(0).getStatus(),
        ParticipantGameStatus.DELETE);
    assertNotNull(participantGame.get(0).getDeletedDateTime());
    assertEquals(participantGame.get(1).getStatus(),
        ParticipantGameStatus.DELETE);
    assertNotNull(participantGame.get(1).getDeletedDateTime());
    assertEquals(resultFriends.getStatus(), FriendStatus.DELETE);
    assertNotNull(resultFriends.getDeletedDateTime());
    assertEquals(resultAppliedFriends.getStatus(), FriendStatus.DELETE);
    assertNotNull(resultAppliedFriends.getDeletedDateTime());
  }
}
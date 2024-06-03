package com.zerobase.hoops.users.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.WITHDRAW;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.alarm.repository.EmitterRepository;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.invite.repository.InviteRepository;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.dto.EditDto;
import com.zerobase.hoops.users.dto.LogInDto;
import com.zerobase.hoops.users.dto.TokenDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.repository.redis.AuthRepository;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @InjectMocks
  AuthService authService;

  @Mock
  UserRepository userRepository;

  @Mock
  AuthRepository authRepository;

  @Mock
  EmitterRepository emitterRepository;

  @Mock
  TokenProvider tokenProvider;

  @Mock
  GameRepository gameRepository;

  @Mock
  ParticipantGameRepository participantGameRepository;

  @Mock
  InviteRepository inviteRepository;

  @Mock
  FriendRepository friendRepository;

  @Spy
  BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  UserEntity user;
  UserEntity notConfirmedUser;

  @BeforeEach
  void setUp() {
    user = UserEntity.builder()
        .id(1L)
        .loginId("test")
        .password(passwordEncoder.encode("test"))
        .email("test@hoops.com")
        .name("테스트")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("테스트별명")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(new ArrayList<>(List.of("ROLE_USER")))
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    userRepository.save(user);

    notConfirmedUser = UserEntity.builder()
        .id(1L)
        .loginId("notConfirmedTest")
        .password(passwordEncoder.encode("test"))
        .email("notConfirmedTest@hoops.com")
        .name("미인증")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("미인증별명")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(new ArrayList<>(List.of("ROLE_USER")))
        .createdDateTime(LocalDateTime.now())
        .emailAuth(false)
        .build();
    userRepository.save(user);
  }

  @Test
  @DisplayName("Auth_LogInUser_Success")
  void logInUserTest_Success() {
    // given
    String id = "test";
    String password = "test";

    LogInDto.Request request = new LogInDto.Request(id, password);

    // when
    when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
    when(userRepository.findByLoginIdAndDeletedDateTimeNull(id)).thenReturn(Optional.of(user));

    UserDto result = authService.logInUser(request);

    // then
    assertEquals(id, result.getId());
    assertTrue(passwordEncoder.matches(password, result.getPassword()));
  }

  @Test
  @DisplayName("Auth_LogInUser_Fail_NonExistingId")
  void logInUserTest_NonExistingId() {
    // given
    String nonExistingId = "nonExistingId";
    String password = "password";
    LogInDto.Request request = new LogInDto.Request(nonExistingId, password);

    //when
    when(userRepository.findByLoginIdAndDeletedDateTimeNull(nonExistingId)).thenReturn(Optional.empty());

    Throwable exception = assertThrows(CustomException.class, () -> authService.logInUser(request));

    // then
    assertEquals("아이디가 존재하지 않습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Auth_LogInUser_Fail_WrongPassword")
  void logInUserTest_WrongPassword() {
    // given
    String id = "test";
    String wrongPassword = "wrongPassword";

    LogInDto.Request request = new LogInDto.Request(id, wrongPassword);

    // when
    when(userRepository.findByLoginIdAndDeletedDateTimeNull(id)).thenReturn(Optional.of(user));
    Throwable exception = assertThrows(CustomException.class, () -> authService.logInUser(request));

    // then
    assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Auth_LogInUser_Fail_NotConfirmedEmail")
  void logInUserTest_NotConfirmedEmail() {
    // Arrange
    String id = "notConfirmedTest";
    String password = "test";

    LogInDto.Request request = new LogInDto.Request(id, password);

    // when
    when(userRepository.findByLoginIdAndDeletedDateTimeNull(id)).thenReturn(Optional.of(notConfirmedUser));
    Throwable exception = assertThrows(CustomException.class, () -> authService.logInUser(request));

    // then
    assertEquals("인증되지 않은 회원입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Auth_GetToken_Success")
  void getTokenTest_Success() {
    // given
    UserDto userDto = UserDto.fromEntity(user);
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    // when
    when(tokenProvider.createAccessToken(userDto.getId(), userDto.getEmail(),
        userDto.getRoles())).thenReturn(accessToken);
    when(tokenProvider.createRefreshToken(userDto.getId())).thenReturn(refreshToken);

    TokenDto result = authService.getToken(userDto);

    // then
    assertEquals(accessToken, result.getAccessToken());
    assertEquals(refreshToken, result.getRefreshToken());
  }

  @Test
  @DisplayName("Auth_RefreshToken_Success")
  void refreshTokenTest_Success() {
    // given
    String refreshToken = "refreshToken";
    String newAccessToken = "newAccessToken";

    Claims claims = Jwts.claims().setSubject(user.getLoginId());

    // when
    when(tokenProvider.parseClaims(refreshToken)).thenReturn(claims);
    when(userRepository.findByLoginIdAndDeletedDateTimeNull(user.getLoginId())).thenReturn(Optional.of(user));
    when(tokenProvider.createAccessToken(user.getLoginId(), user.getEmail(), user.getRoles())).thenReturn(newAccessToken);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);

    TokenDto result = authService.refreshToken(request, user);

    // then
    assertEquals(newAccessToken, result.getAccessToken());
    assertEquals(refreshToken, result.getRefreshToken());
  }

  @Test
  @DisplayName("Auth_RefreshToken_Fail_InvalidAccessToken")
  void refreshTokenTest_InvalidAccessToken() {
    // given
    String invalidAccessToken = "invalidAccessToken";

    // when
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(invalidAccessToken);

    Throwable exception = assertThrows(CustomException.class, () -> authService.refreshToken(request, user));

    // then
    assertEquals("토큰 형식의 값을 찾을 수 없습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Auth_RefreshToken_Fail_InvalidRefreshToken")
  void refreshTokenTest_InvalidRefreshToken() {
    // given
    String invalidRefreshToken = "invalidRefreshToken";

    Claims refreshClaims = Jwts.claims().setSubject("invalidId");

    // when
    when(tokenProvider.parseClaims(invalidRefreshToken)).thenReturn(refreshClaims);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + invalidRefreshToken);

    Throwable exception = assertThrows(CustomException.class, () -> authService.refreshToken(request, user));

    // then
    assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Auth_RefreshToken_Fail_NotFoundToken")
  void refreshTokenTest_Fail_NotFoundToken() {
    // given
    String refreshToken = "refreshToken";

    Claims claims = Jwts.claims().setSubject(user.getLoginId());

    // when
    when(tokenProvider.parseClaims(refreshToken)).thenReturn(claims);
    doThrow(new CustomException(ErrorCode.NOT_FOUND_TOKEN)).when(authRepository).findById(user.getLoginId());

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);

    Throwable exception = assertThrows(CustomException.class, () -> authService.refreshToken(request, user));

    // then
    assertEquals("토큰 형식의 값을 찾을 수 없습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Auth_RefreshToken_Fail_UserNotFound")
  void refreshTokenTest_Fail_UserNotFound() {
    // given
    String refreshToken = "refreshToken";

    Claims claims = Jwts.claims().setSubject(user.getLoginId());

    // when
    when(tokenProvider.parseClaims(refreshToken)).thenReturn(claims);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);

    Throwable exception = assertThrows(CustomException.class, () -> authService.refreshToken(request, user));

    // then
    assertEquals("아이디가 존재하지 않습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Auth_LogOutUserTest_Success")
  void logOutUserTest_Success() {
    // given
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    // when
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + accessToken);
    when(request.getHeader("refreshToken")).thenReturn(refreshToken);

    Claims accessClaims = Jwts.claims().setSubject(user.getLoginId());
    accessClaims.put("id", user.getId());
    Claims refreshClaims = Jwts.claims().setSubject(user.getLoginId());
    refreshClaims.put("id", user.getId());

    when(tokenProvider.parseClaims(accessToken)).thenReturn(accessClaims);
    when(tokenProvider.parseClaims(refreshToken)).thenReturn(refreshClaims);

    doNothing().when(authRepository).deleteById(user.getLoginId());
    doNothing().when(emitterRepository).deleteAllStartWithUserId(
        String.valueOf(user.getId()));
    doNothing().when(emitterRepository).deleteAllEventCacheStartWithUserId(
        String.valueOf(user.getId()));
    doNothing().when(tokenProvider).addToLogOutList(accessToken);

    authService.logOutUser(request, user);

    // then
    verify(authRepository, times(1)).deleteById(user.getLoginId());
    verify(emitterRepository, times(1)).deleteAllStartWithUserId(
        String.valueOf(user.getId()));
    verify(emitterRepository, times(1)).deleteAllEventCacheStartWithUserId(
        String.valueOf(user.getId()));
    verify(tokenProvider, times(1)).addToLogOutList(accessToken);
  }

  @Test
  @DisplayName("Auth_LogOutUserTest_Fail_NotFoundToken")
  void logOutUserTest_Fail_NotFoundToken() {
    // when
    HttpServletRequest request = mock(HttpServletRequest.class);

    Throwable exception = assertThrows(CustomException.class, () -> authService.logOutUser(request, user));

    // then
    assertEquals("토큰 형식의 값을 찾을 수 없습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Auth_LogOutUserTest_Fail_InValidToken")
  void logOutUserTest_Fail_InvalidToken() {
    // given
    String accessToken = "invalidAccessToken";
    String refreshToken = "invalidRefreshToken";

    // when
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + accessToken);
    when(request.getHeader("refreshToken")).thenReturn(refreshToken);
    when(tokenProvider.parseClaims(anyString())).thenThrow(new CustomException(ErrorCode.INVALID_TOKEN));

    Throwable exception = assertThrows(CustomException.class, () -> authService.logOutUser(request, user));

    // then
    assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Auth_GetUserInfoTest_Success")
  void getUserInfoTest() {
    // given
    String accessToken = "accessToken";

    // when
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + accessToken);
    when(tokenProvider.parseClaims(anyString())).thenReturn(Jwts.claims().setSubject(user.getLoginId()));

    UserDto result = authService.getUserInfo(request, user);

    // then
    assertEquals(user.getId(), result.getId());
    verify(request, times(1)).getHeader(HttpHeaders.AUTHORIZATION);
    verify(tokenProvider, times(1)).parseClaims(anyString());
  }

  @Test
  @DisplayName("Auth_GetUserInfo_Fail_InvalidToken")
  void getUserInfoTest_InvalidToken() {
    // given
    String invalidAccessToken = "invalidAccessToken";
    UserEntity user = new UserEntity();
    user.setLoginId("testUser");

    // when
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + invalidAccessToken);
    when(tokenProvider.parseClaims(anyString())).thenThrow(new CustomException(ErrorCode.INVALID_TOKEN));

    Throwable exception = assertThrows(CustomException.class, () -> authService.getUserInfo(request, user));

    // then
    assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("Auth_EditUserInfoTest_Success")
  void editUserInfoTest_Success() {
    // given
    String accessToken = "accessToken";
    EditDto.Request editDto = new EditDto.Request();
    editDto.setPassword("newPassword");
    editDto.setNickName("newNickName");
    String password = user.getPassword();

    // when
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(
        "Bearer " + accessToken);
    when(tokenProvider.parseClaims(accessToken)).thenReturn(
        Jwts.claims().setSubject(user.getLoginId()));

    UserDto result = authService.editUserInfo(request, editDto, user);

    // then
    assertEquals(user.getId(), result.getId());
    assertEquals(user.getNickName(), result.getNickName());
    assertNotEquals(password, result.getPassword());
  }

  @Test
  @DisplayName("Auth_DeactivateUser_Success")
  void deactivateUserTest_Success() {
    // given
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    // when
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + accessToken);
    when(request.getHeader("refreshToken")).thenReturn(refreshToken);
    when(tokenProvider.parseClaims(accessToken)).thenReturn(Jwts.claims().setSubject(user.getLoginId()));
    when(tokenProvider.parseClaims(refreshToken)).thenReturn(Jwts.claims().setSubject(user.getLoginId()));
    lenient().when(gameRepository.findByUserEntityIdAndDeletedDateTimeNull(user.getId())).thenReturn(new ArrayList<>());
    lenient().when(participantGameRepository.findByGameEntityIdAndStatusNotAndDeletedDateTimeNull(eq(anyLong()), WITHDRAW)).thenReturn(new ArrayList<>());
    lenient().when(participantGameRepository.findByUserEntityIdAndStatusInAndWithdrewDateTimeNull(user.getId(), List.of(APPLY, ACCEPT))).thenReturn(new ArrayList<>());
    lenient().when(inviteRepository.findByInviteStatusAndGameEntityId(InviteStatus.REQUEST, eq(anyLong()))).thenReturn(new ArrayList<>());
    when(inviteRepository.findByInviteStatusAndSenderUserEntityIdOrReceiverUserEntityId(
        InviteStatus.REQUEST, user.getId(), user.getId())).thenReturn(new ArrayList<>());
    lenient().when(friendRepository.findByUserEntityIdOrFriendUserEntityIdAndStatusNotAndDeletedDateTimeNull(user.getId(), user.getId(), FriendStatus.DELETE)).thenReturn(new ArrayList<>());
    lenient().when(userRepository.findByLoginIdAndDeletedDateTimeNull(anyString())).thenReturn(Optional.of(user));

    authService.deactivateUser(request, user);

    // then
    assertNotNull(user.getDeletedDateTime());
  }
}
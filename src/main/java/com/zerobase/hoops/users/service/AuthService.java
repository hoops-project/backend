package com.zerobase.hoops.users.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.DELETE;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.WITHDRAW;
import static com.zerobase.hoops.util.Common.getNowDateTime;

import com.zerobase.hoops.alarm.repository.EmitterRepository;
import com.zerobase.hoops.document.FriendDocument;
import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.document.InviteDocument;
import com.zerobase.hoops.document.ParticipantGameDocument;
import com.zerobase.hoops.document.UserDocument;
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
import com.zerobase.hoops.users.repository.redis.AuthRepository;
import com.zerobase.hoops.users.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

  private final AuthRepository authRepository;
  private final UserRepository userRepository;

  private final GameRepository gameRepository;
  private final ParticipantGameRepository participantGameRepository;
  private final FriendRepository friendRepository;
  private final InviteRepository inviteRepository;
  private final EmitterRepository emitterRepository;

  private final TokenProvider tokenProvider;

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public UserDto logInUser(LogInDto.Request request) {
    log.info("로그인 시작 : {}", request.getLoginId());
    UserDocument user =
        userRepository.findByLoginIdAndDeletedDateTimeNull(request.getLoginId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String password = request.getPassword();
    String encodedPassword = user.getPassword();
    boolean isMatched = passwordEncoder.matches(password, encodedPassword);
    if (!isMatched) {
      log.error("로그인 에러 : {}", ErrorCode.NOT_MATCHED_PASSWORD.getDescription());
      throw new CustomException(ErrorCode.NOT_MATCHED_PASSWORD);
    }

    if (!user.isEmailAuth()) {
      log.error("로그인 에러 : {}", ErrorCode.USER_NOT_CONFIRM.getDescription());
      throw new CustomException(ErrorCode.USER_NOT_CONFIRM);
    }

    log.info("로그인 성공 : {}", user.getLoginId());
    return UserDto.fromDocument(user);
  }

  public TokenDto getToken(UserDto userDto) {
    log.info("토큰 생성 시작");
    String accessToken =
        tokenProvider.createAccessToken(userDto.getLoginId(),
            userDto.getEmail(), userDto.getRoles());
    log.info("Access Token 생성 완료");
    String refreshToken =
        tokenProvider.createRefreshToken(userDto.getLoginId());
    log.info("Refresh Token 생성 완료");

    return new TokenDto(userDto.getLoginId(), accessToken, refreshToken);
  }

  @Transactional
  public TokenDto refreshToken(
      HttpServletRequest request, UserDocument UserDocument
  ) {
    log.info("토큰 갱신 시작");
    String refreshToken = validateAccessTokenExistHeader(request);

    Claims claims = tokenProvider.parseClaims(refreshToken);
    String loginId = claims.get("sub", String.class);

    if (!UserDocument.getLoginId().equals(loginId)) {
      log.error("토큰 갱신 에러 : {}", ErrorCode.INVALID_TOKEN.getDescription());
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    try {
      authRepository.findByLoginId(loginId);
    } catch (Exception e) {
      log.error("토큰 갱신 에러 : {}", ErrorCode.NOT_FOUND_TOKEN.getDescription());
      throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
    }

    UserDocument user = userRepository.findByLoginIdAndDeletedDateTimeNull(loginId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String responseAccessToken =
        tokenProvider.createAccessToken(loginId, user.getEmail(), user.getRoles());

    log.info("토큰 갱신 완료");
    return new TokenDto(loginId, responseAccessToken, refreshToken);
  }

  private String validateAccessTokenExistHeader(HttpServletRequest request) {
    String token = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (!ObjectUtils.isEmpty(token) && token.startsWith("Bearer ")) {
      return token.substring("Bearer ".length());
    } else {
      log.error("토큰 없음");
      throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
    }
  }

  private String validateRefreshTokenExistHeader(HttpServletRequest request) {
    String token = request.getHeader("refreshToken");
    if (!ObjectUtils.isEmpty(token)) {
      return token;
    } else {
      log.error("토큰 없음");
      throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
    }
  }

  public void logOutUser(
      HttpServletRequest request, UserDocument UserDocument) {
    log.info("로그아웃 시작");
    String accessToken = validateAccessTokenExistHeader(request);
    log.info("Access Token 확인");
    String refreshToken = validateRefreshTokenExistHeader(request);
    log.info("Refresh Token 확인");

    Claims claims = tokenProvider.parseClaims(accessToken);
    String loginId = claims.get("sub", String.class);

    if (tokenUserMatch(accessToken, refreshToken) &&
        loginId.equals(UserDocument.getLoginId())) {
      log.info("Refresh Token 삭제");
      authRepository.deleteByLoginId(loginId);
    } else {
      log.error("로그아웃 에러 : {}", ErrorCode.INVALID_TOKEN.getDescription());
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    emitterRepository.deleteAllStartWithUserId(
        String.valueOf(UserDocument.getLoginId()));
    emitterRepository.deleteAllEventCacheStartWithUserId(
        String.valueOf(UserDocument.getLoginId()));

    tokenProvider.addToLogOutList(accessToken);
    log.info("로그아웃 완료");
  }

  private boolean tokenUserMatch(String accessToken, String refreshToken) {
    Claims accessClaims = tokenProvider.parseClaims(accessToken);
    Claims refreshClaims = tokenProvider.parseClaims(refreshToken);
    String accessId = accessClaims.get("sub", String.class);
    String refreshId = refreshClaims.get("sub", String.class);

    return accessId.equals(refreshId);
  }

  public UserDto getUserInfo(HttpServletRequest request, UserDocument user) {
    isSameLoginId(request, user);
    return UserDto.fromDocument(user);
  }

  public UserDto editUserInfo(HttpServletRequest request,
      EditDto.Request editDto, UserDocument user) {
    log.info("회원 정보 수정 시작");
    isSameLoginId(request, user);
    validateAccessTokenExistHeader(request);

    if (editDto.getPassword() != null) {
      String encodedNewPassword = passwordEncoder.encode(editDto.getPassword());
      user.passwordEdit(encodedNewPassword);
    }

    user.edit(editDto);
    userRepository.save(user);

    log.info("회원 정보 수정 완료");
    return UserDto.fromDocument(user);
  }

  private void isSameLoginId(HttpServletRequest request, UserDocument user) {
    String accessToken = validateAccessTokenExistHeader(request);

    Claims claims = tokenProvider.parseClaims(accessToken);
    String loginId = claims.get("sub", String.class);

    if (!user.getLoginId().equals(loginId)) {
      log.error("토큰 에러 : {}", ErrorCode.INVALID_TOKEN.getDescription());
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }

  @Transactional
  public void deactivateUser(HttpServletRequest request, UserDocument user) {
    log.info("회원 탈퇴 시작");
    isSameLoginId(request, user);
    String accessToken = validateAccessTokenExistHeader(request);
    String refreshToken = validateRefreshTokenExistHeader(request);

    if (!tokenUserMatch(accessToken, refreshToken)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }


    log.info("경기 테이블 삭제");
    List<GameDocument> gameList =
        gameRepository
            .findByUserIdAndDeletedDateTimeNull(user.getId());
    gameList.stream().forEach(game -> {
      game.setDeletedDateTime(getNowDateTime());
      gameRepository.save(game);

      log.info("참가 테이블 삭제");
      List<ParticipantGameDocument> participantList =
          participantGameRepository
              .findByGameIdAndStatusNotAndDeletedDateTimeNull(
                  game.getId(), WITHDRAW);
      participantList.stream().forEach(
          participantGame -> {
            participantGame.setDeletedDateTime(getNowDateTime());
            participantGame.setStatus(DELETE);
            participantGameRepository.save(participantGame);
          });

      log.info("초대 테이블 삭제");
      List<InviteDocument> inviteList = inviteRepository
          .findByInviteStatusAndGameId(
              InviteStatus.REQUEST, game.getId());
      inviteList.stream().forEach(
          invite -> {
            invite.setInviteStatus(InviteStatus.DELETE);
            invite.setDeletedDateTime(getNowDateTime());
            inviteRepository.save(invite);
          }
      );

    });

    log.info("참가 테이블 탈퇴");
    List<ParticipantGameDocument> participantList =
        participantGameRepository
            .findByUserIdAndStatusInAndWithdrewDateTimeNull(
                user.getId(), List.of(APPLY, ACCEPT));
    participantList.stream().forEach(
        participantGame -> {
          participantGame.setWithdrewDateTime(getNowDateTime());
          participantGame.setStatus(WITHDRAW);
          participantGameRepository.save(participantGame);
        });

    log.info("초대 테이블 삭제");
    List<InviteDocument> inviteList =
        inviteRepository
            .findByInviteStatusAndSenderUserIdOrReceiverUserId(
                InviteStatus.REQUEST, user.getId(), user.getId());
    inviteList.stream().forEach(
        invite -> {
          invite.setInviteStatus(InviteStatus.DELETE);
          invite.setDeletedDateTime(getNowDateTime());
          inviteRepository.save(invite);
        }
    );

    log.info("친구 목록 삭제");
    List<FriendDocument> friendList =
        friendRepository
            .findByUserIdOrFriendUserIdAndStatusNotAndDeletedDateTimeNull(
                user.getId(), user.getId(), FriendStatus.DELETE);
    friendList.stream().forEach(friend -> {
      friend.setStatus(FriendStatus.DELETE);
      friend.setDeletedDateTime(getNowDateTime());
      friendRepository.save(friend);
    });

    log.info("로그아웃");
    logOutUser(request, user);

    user.setDeletedDateTime(getNowDateTime());
    userRepository.save(user);
    log.info("회원 탈퇴 완료");
  }
}

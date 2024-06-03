package com.zerobase.hoops.users.service;

import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.DELETE;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.WITHDRAW;

import com.zerobase.hoops.alarm.repository.EmitterRepository;
import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
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
import com.zerobase.hoops.users.repository.redis.AuthRepository;
import com.zerobase.hoops.users.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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

    UserEntity user =
        userRepository.findByIdAndDeletedDateTimeNull(request.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String password = request.getPassword();
    String encodedPassword = user.getPassword();
    boolean isMatched = passwordEncoder.matches(password, encodedPassword);
    if (!isMatched) {
      throw new CustomException(ErrorCode.NOT_MATCHED_PASSWORD);
    }

    if (!user.isEmailAuth()) {
      throw new CustomException(ErrorCode.USER_NOT_CONFIRM);
    }

    return UserDto.fromEntity(user);
  }

  public TokenDto getToken(UserDto userDto) {
    String accessToken =
        tokenProvider.createAccessToken(userDto.getId(),
            userDto.getEmail(), userDto.getRoles());
    String refreshToken =
        tokenProvider.createRefreshToken(userDto.getId());

    return new TokenDto(userDto.getId(), accessToken, refreshToken);
  }

  @Transactional
  public TokenDto refreshToken(
      HttpServletRequest request, UserEntity userEntity
  ) {
    String refreshToken = validateAccessTokenExistHeader(request);

    Claims claims = tokenProvider.parseClaims(refreshToken);
    String id = claims.get("sub", String.class);

    if (!userEntity.getId().equals(id)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    try {
      authRepository.findById(id);
    } catch (Exception e) {
      throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
    }

    UserEntity user = userRepository.findByIdAndDeletedDateTimeNull(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String responseAccessToken =
        tokenProvider.createAccessToken(id, user.getEmail(), user.getRoles());

    return new TokenDto(id, responseAccessToken, refreshToken);
  }

  private String validateAccessTokenExistHeader(HttpServletRequest request) {
    String token = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (!ObjectUtils.isEmpty(token) && token.startsWith("Bearer ")) {
      return token.substring("Bearer ".length());
    } else {
      throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
    }
  }

  private String validateRefreshTokenExistHeader(HttpServletRequest request) {
    String token = request.getHeader("refreshToken");
    if (!ObjectUtils.isEmpty(token)) {
      return token;
    } else {
      throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
    }
  }

  public void logOutUser(
      HttpServletRequest request, UserEntity userEntity) {
    String accessToken = validateAccessTokenExistHeader(request);
    String refreshToken = validateRefreshTokenExistHeader(request);

    Claims claims = tokenProvider.parseClaims(accessToken);
    String id = claims.get("sub", String.class);

    if (tokenUserMatch(accessToken, refreshToken) &&
        id.equals(userEntity.getId())) {
      authRepository.deleteById(id);
    } else {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    emitterRepository.deleteAllStartWithUserId(
        String.valueOf(userEntity.getId()));
    emitterRepository.deleteAllEventCacheStartWithUserId(
        String.valueOf(userEntity.getId()));

    tokenProvider.addToLogOutList(accessToken);
  }

  private boolean tokenUserMatch(String accessToken, String refreshToken) {
    Claims accessClaims = tokenProvider.parseClaims(accessToken);
    Claims refreshClaims = tokenProvider.parseClaims(refreshToken);
    String accessId = accessClaims.get("sub", String.class);
    String refreshId = refreshClaims.get("sub", String.class);

    return accessId.equals(refreshId);
  }

  public UserDto getUserInfo(HttpServletRequest request, UserEntity user) {
    isSameId(request, user);
    return UserDto.fromEntity(user);
  }

  public UserDto editUserInfo(HttpServletRequest request,
      EditDto.Request editDto, UserEntity user) {
    isSameId(request, user);
    validateAccessTokenExistHeader(request);

    if (editDto.getPassword() != null) {
      String encodedNewPassword = passwordEncoder.encode(editDto.getPassword());
      user.passwordEdit(encodedNewPassword);
    }

    user.edit(editDto);
    userRepository.save(user);

    return UserDto.fromEntity(user);
  }

  private void isSameId(HttpServletRequest request, UserEntity user) {
    String accessToken = validateAccessTokenExistHeader(request);

    Claims claims = tokenProvider.parseClaims(accessToken);
    String id = claims.get("sub", String.class);

    if (!user.getId().equals(id)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }

  @Transactional
  public void deactivateUser(HttpServletRequest request, UserEntity user) {
    isSameId(request, user);
    String accessToken = validateAccessTokenExistHeader(request);
    String refreshToken = validateRefreshTokenExistHeader(request);

    if (!tokenUserMatch(accessToken, refreshToken)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    LocalDateTime now = LocalDateTime.now();

    // 내가 생성한 경기 삭제
    List<GameEntity> gameList =
        gameRepository
            .findByUserEntityUserIdAndDeletedDateTimeNull(user.getId());
    gameList.stream().forEach(game -> {
      game.setDeletedDateTime(now);
      gameRepository.save(game);

      // 내가 생성한 경기의 참가 테이블 삭제
      List<ParticipantGameEntity> participantList =
          participantGameRepository
              .findByGameEntityGameIdAndStatusNotAndDeletedDateTimeNull(
                  game.getGameId(), WITHDRAW);
      participantList.stream().forEach(
          participantGame -> {
            participantGame.setDeletedDateTime(now);
            participantGame.setStatus(DELETE);
            participantGameRepository.save(participantGame);
          });

      // 내가 생성한 경기의 초대 테이블 삭제
      List<InviteEntity> inviteList = inviteRepository
          .findByInviteStatusAndGameEntityGameId(
              InviteStatus.REQUEST, game.getGameId());
      inviteList.stream().forEach(
          invite -> {
            invite.setInviteStatus(InviteStatus.DELETE);
            invite.setDeletedDateTime(now);
            inviteRepository.save(invite);
          }
      );

    });

    // 내가 참가한 방의 참가 테이블에서 탈퇴 처리
    List<ParticipantGameEntity> participantList =
        participantGameRepository
            .findByUserEntityUserIdAndStatusInAndWithdrewDateTimeNull(
                user.getId(), List.of(APPLY, ACCEPT));
    participantList.stream().forEach(
        participantGame -> {
          participantGame.setWithdrewDateTime(now);
          participantGame.setStatus(WITHDRAW);
          participantGameRepository.save(participantGame);
        });

    // 내가 참가한 방의 초대에서 삭제
    List<InviteEntity> inviteList =
        inviteRepository
            .findByInviteStatusAndSenderUserEntityUserIdOrReceiverUserEntityUserId(
                InviteStatus.REQUEST, user.getId(), user.getId());
    inviteList.stream().forEach(
        invite -> {
          invite.setInviteStatus(InviteStatus.DELETE);
          invite.setDeletedDateTime(now);
          inviteRepository.save(invite);
        }
    );

    // 친구 목록에 있는 사람들 서로 삭제
    List<FriendEntity> friendList =
        friendRepository
            .findByUserEntityUserIdOrFriendUserEntityUserIdAndStatusNotAndDeletedDateTimeNull(
                user.getId(), user.getId(), FriendStatus.DELETE);
    friendList.stream().forEach(friend -> {
      friend.setStatus(FriendStatus.DELETE);
      friend.setDeletedDateTime(now);
      friendRepository.save(friend);
    });

    // 로그 아웃
    logOutUser(request, user);

    // 회원 탈퇴 처리
    user.setDeletedDateTime(now);
    userRepository.save(user);
  }
}

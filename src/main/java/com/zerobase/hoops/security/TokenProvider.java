package com.zerobase.hoops.security;

import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.manager.service.ManagerService;
import com.zerobase.hoops.users.repository.redis.AuthRepository;
import com.zerobase.hoops.users.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30;
  private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60;
  private final UserService userService;
  private final AuthRepository authRepository;
  private final ManagerService managerService;

  @Getter
  private final Set<String> logOut =
      new ConcurrentSkipListSet<>();

  @Value("${spring.jwt.secret}")
  private String secretKey;

  /**
   * AccessToken 생성
   */
  public String createAccessToken(String LoginId, String email,
      List<String> role) {
    return generateAccessToken(LoginId, email, role, ACCESS_TOKEN_EXPIRE_TIME);
  }

  /**
   * RefreshToken 생성
   */
  public String createRefreshToken(String LoginId) {

    String refreshToken =
        generateRefreshToken(LoginId, REFRESH_TOKEN_EXPIRE_TIME);

    authRepository.saveRefreshToken(
        LoginId, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));
    return refreshToken;
  }

  public String generateAccessToken(String LoginId, String email,
      List<String> roles, Long expireTime) {

    Claims claims = Jwts.claims().setSubject(LoginId);
    claims.put("email", email);
    claims.put("roles", roles);

    return returnToken(claims, expireTime);
  }

  public String generateRefreshToken(String LoginId, Long expireTime) {

    Claims claims = Jwts.claims().setSubject(LoginId);

    return returnToken(claims, expireTime);
  }

  private String returnToken(Claims claims, Long expireTime) {
    var now = new Date();
    var expireDate = new Date(now.getTime() + expireTime);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expireDate)
        .signWith(getSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)))
        .compact();
  }

  public static Key getSigningKey(byte[] secretKey) {
    return Keys.hmacShaKeyFor(secretKey);
  }

  public Claims parseClaims(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(
              getSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)))
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  public String getUsername(String token) {
    return this.parseClaims(token).getSubject();
  }

  public boolean validateToken(String token) {
    try {
      validateRefreshToken(token);
      Jws<Claims> claims = Jwts.parserBuilder()
          .setSigningKey(
              getSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)))
          .build()
          .parseClaimsJws(token);
      managerService.checkBlackList(claims.getBody().getSubject());
      checkLogOut(token);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (IllegalArgumentException e) {
      log.info("IllegalArgumentException!!");
      throw new JwtException(ErrorCode.NOT_FOUND_TOKEN.getDescription());
    } catch (MalformedJwtException e) {
      log.info("MalformedJwtException!!");
      throw new JwtException(ErrorCode.UNSUPPORTED_TOKEN.getDescription());
    } catch (ExpiredJwtException e) {
      log.info("ExpiredJwtException!!");
      throw new JwtException(ErrorCode.EXPIRED_TOKEN.getDescription());
    } catch (JwtException e) {
      log.info("JwtException!!");
      throw new JwtException(ErrorCode.INVALID_TOKEN.getDescription());
    } catch (CustomException e) {
      if(e.getErrorCode().equals(ErrorCode.ALREADY_LOGOUT)){
        log.info("CustomException!!");
        throw new JwtException(ErrorCode.ALREADY_LOGOUT.getDescription());
      } else if (e.getErrorCode().equals(ErrorCode.BAN_FOR_10DAYS)) {
        log.info("CustomException!!");
        throw new JwtException(ErrorCode.BAN_FOR_10DAYS.getDescription());
      } else if (e.getErrorCode().equals(ErrorCode.EXPIRED_REFRESH_TOKEN)) {
        log.info("CustomException!!");
        throw new JwtException(ErrorCode.EXPIRED_REFRESH_TOKEN.getDescription());
      } else {
        log.info("CustomException!!");
        throw new JwtException(ErrorCode.ACCESS_DENIED.getDescription());
      }
    }
  }

  public Authentication getAuthentication(String jwt) {
    UserDetails userDetails =
        this.userService.loadUserByUsername(
            this.getUsername(jwt));
    return new UsernamePasswordAuthenticationToken(
        userDetails,
        "",
        userDetails.getAuthorities());
  }

  public boolean isLogOut(String token) {
    return this.logOut.contains(token);
  }

  public void checkLogOut(String token) {
    if (isLogOut(token)) {
      throw new CustomException(ErrorCode.ALREADY_LOGOUT);
    }
  }

  public void validateRefreshToken(String token) {
    if (token.length() == 152 && parseClaims(token).getExpiration().before(new Date())) {
      throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }
  }

  public void addToLogOutList(String token) {
    this.logOut.add(token);
  }

}
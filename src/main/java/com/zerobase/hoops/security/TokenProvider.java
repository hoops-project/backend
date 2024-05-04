package com.zerobase.hoops.security;

import com.zerobase.hoops.users.repository.AuthRepository;
import com.zerobase.hoops.users.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
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
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60;
  private static final long BLACK_TOKEN_EXPIRE_TIME = 1000L * 60 * 2;
  private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60;
  private final UserService userService;
  private final AuthRepository authRepository;

  @Getter
  private final Set<String> logOut =
      new ConcurrentSkipListSet<>();

  @Value("${spring.jwt.secret}")
  private String secretKey;

  /**
   * AccessToken 생성
   */
  public String createAccessToken(String id, String email, List<String> role) {
    return generateToken(id, email, role, ACCESS_TOKEN_EXPIRE_TIME);
  }

  /**
   * RefreshToken 생성
   */
  public String createRefreshToken(String id, String email, List<String> role) {

    String refreshToken =
        generateToken(id, email, role, REFRESH_TOKEN_EXPIRE_TIME);

    authRepository.saveRefreshToken(
        id, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));
    return refreshToken;
  }

  public String generateToken(String id, String email, List<String> roles,
      Long expireTime) {

    Claims claims = Jwts.claims().setSubject(email);
    claims.put("id", id);
    claims.put("roles", roles);

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
    if (!StringUtils.hasText(token)) {
      return false;
    }
    var claims = this.parseClaims(token);
    return !claims.getExpiration().before(new Date());
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

  public void addToLogOutList(String token) {
    this.logOut.add(token);
  }

}
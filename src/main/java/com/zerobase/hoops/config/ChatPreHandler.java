package com.zerobase.hoops.config;


import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.security.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import static com.zerobase.hoops.exception.ErrorCode.EXPIRED_TOKEN;
import static com.zerobase.hoops.exception.ErrorCode.INVALID_TOKEN;

@RequiredArgsConstructor
@Component
@Slf4j
public class ChatPreHandler implements ChannelInterceptor {

  private final TokenProvider tokenProvider;
  private static final String TOKEN_PREFIX = "Bearer";

  /**
   * websocket을 통해 들어온 요청이 처리 되기 전 실행
   *
   * @param message
   * @param channel
   * @return
   */
  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    // websocket 연결 요청
    if (StompCommand.CONNECT == accessor.getCommand()) {
      String jwtToken = String.valueOf(
          accessor.getFirstNativeHeader("Authorization"));

      if (!this.validateAccessToken(jwtToken)) {
        throw new CustomException(INVALID_TOKEN);
      }

      String senderId = this.getSenderId(jwtToken);
      accessor.addNativeHeader("senderId", senderId);
    }
    return message;
  }

  /**
   * websocket 연결 후 실행
   *
   * @param message
   * @param channel
   * @param sent
   */
  @Override
  public void postSend(Message message, MessageChannel channel, boolean sent) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    String sessionId = accessor.getSessionId();

    switch ((Objects.requireNonNull(accessor.getCommand()))) {
      case CONNECT:
        log.info("세션 들어옴 -> {}", sessionId);
        break;

      case DISCONNECT:
        log.info("세션 끊음 -> {}", sessionId);
        break;

      default:
        break;
    }
  }

  /**
   * 토큰 인증
   *
   * @param accessToken
   * @return
   */
  private boolean validateAccessToken(String accessToken) {
    if (accessToken == null) {
      return false;
    }

    String bearerToken = accessToken.trim();

    if (!bearerToken.trim().isEmpty() && bearerToken.startsWith(TOKEN_PREFIX)) {
      accessToken = bearerToken.substring(7);

      try {
        Claims claims = tokenProvider.parseClaims(accessToken);
        return true;
      } catch (ExpiredJwtException | MalformedJwtException e) {
        return false;
      }
    }

    return false;
  }

  /**
   * token 인증된 사용자 정보 가져오기
   *
   * @param accessToken
   * @return
   */
  private String getSenderId(String accessToken) {
    String bearerToken = accessToken.trim();

    if (!bearerToken.trim().isEmpty() && bearerToken.startsWith(TOKEN_PREFIX)) {
      accessToken = bearerToken.substring(7);

      try {
        Claims claims = tokenProvider.parseClaims(accessToken);
        return claims.get("id", String.class);
      } catch (ExpiredJwtException | MalformedJwtException e) {
        throw new CustomException(EXPIRED_TOKEN);
      }
    }

    return null;
  }

  /**
   * websocket 연결되는 event 발생하면 실행되는 메서드, header에 senderId 추가
   *
   * @param event
   */
  @EventListener(SessionConnectEvent.class)
  public void onApplicationEvent(SessionConnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String accessToken = accessor.getFirstNativeHeader("Authorization");
    if (this.validateAccessToken(accessToken)) {
      String senderId = this.getSenderId(accessToken);
      accessor.getSessionAttributes().put("senderId", senderId);
    }
  }
}
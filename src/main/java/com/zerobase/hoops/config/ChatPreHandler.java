package com.zerobase.hoops.config;

import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.security.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
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
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
    log.info("preSend 실행됨");
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    // websocket 연결 요청
    if (StompCommand.CONNECT == accessor.getCommand()) {
      String jwtToken = String.valueOf(
          accessor.getFirstNativeHeader("Authorization"));

      // TODO -> 프론트와 연동 후 정상작동하면 log 삭제 예정
      log.info("Header에서 Authorization 추출 성공");

      if (!this.validateAccessToken(jwtToken)) {
        throw new CustomException(INVALID_TOKEN);
      }

      String senderId = this.getSenderId(jwtToken);
      log.info(" === senderId : {} === ", senderId);

      // TODO -> 추후 header에 추가된 senderID값 사용하지 않으면 삭제
      // header에 senderId 값 추가
      accessor.addNativeHeader("senderId", senderId);

      // session에 senderId값 추가
      accessor.getSessionAttributes().put("senderId", senderId);

    }
    return message;
  }


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


  @EventListener(SessionConnectEvent.class)
  public void handleWebSocketConnectListener(SessionConnectEvent event) {

    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = accessor.getSessionId();
    log.info("new connected sessionId : " + sessionId);

  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = accessor.getSessionId();

    log.info("sessionId Disconnected : " + sessionId);
  }
}

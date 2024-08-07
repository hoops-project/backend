package com.zerobase.hoops.config;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConnectionConfig implements
    WebSocketMessageBrokerConfigurer {

  private final JwtTokenExtract jwtTokenExtract;
  private final ParticipantGameRepository participantGameRepository;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    log.info("Registering STOMP endpoints...");
    registry.addEndpoint("/ws")
        .setAllowedOrigins(
            "http://127.0.0.1:8080",
            "http://127.0.0.1:5001",
            "http://localhost:5173",
            "https://hoops-frontend-jet.vercel.app",
            "https://hoops.services")
        .withSockJS();
    log.info("STOMP endpoints registered.");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic");
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void configureClientInboundChannel(
      ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message,
          MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
            message, StompHeaderAccessor.class);

        assert accessor != null;
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
          String authToken = accessor.getFirstNativeHeader(
              "Authorization");
          String gameId = accessor.getFirstNativeHeader("gameId");
          if (!isValidUser(authToken, gameId)) {
            UserEntity user = jwtTokenExtract.getUserFromToken(authToken);
            log.error("{} <- 채팅 참여 권한 없음", user.getId());
            throw new CustomException(ErrorCode.NOT_ACCEPT_USER_FOR_GAME);
          }
        }
        return message;
      }
    });
  }

  private boolean isValidUser(String token, String gameId) {
    Long gameIdNumber = Long.parseLong(gameId);
    UserEntity user = jwtTokenExtract.getUserFromToken(token);
    log.info("{} <- 채팅 입장 유효한지 체크", user.getNickName());
    return participantGameRepository.existsByGame_IdAndUser_IdAndStatus(
        gameIdNumber, user.getId(), ParticipantGameStatus.ACCEPT);
  }
}

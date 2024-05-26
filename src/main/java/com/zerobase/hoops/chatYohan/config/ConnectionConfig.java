package com.zerobase.hoops.chatYohan.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.support.ChannelInterceptor;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class ConnectionConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    log.info("Registering STOMP endpoints...");
    registry.addEndpoint("/ws")
        .setAllowedOrigins(
            "http://127.0.0.1:8080",
            "http://127.0.0.1:5000",
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

//  @Override
//  public void configureClientInboundChannel(ChannelRegistration registration) {
//    registration.interceptors(new ChannelInterceptor() {
//      @Override
//      public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//          String authToken = accessor.getFirstNativeHeader("Authorization");
//
//          log.info("Received STOMP CONNECT command with Authorization header: {}", authToken);
//
//          if (authToken != null && authToken.startsWith("Bearer ")) {
//            authToken = authToken.substring(7);
//            // Token 검증 로직 추가
//            // 예: JWT 검증
//            // boolean valid = jwtTokenProvider.validateToken(authToken);
//            // if (!valid) {
//            //     throw new IllegalArgumentException("Invalid Token");
//            // }
//            log.info("Authorization token validated successfully.");
//          } else {
//            log.error("Authorization header is missing or invalid");
//            throw new IllegalArgumentException("Authorization header is missing or invalid");
//          }
//        }
//        return message;
//      }
//    });
//  }

}

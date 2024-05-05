package com.zerobase.hoops.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final ChatPreHandler chatPreHandler;

  /**
   * 클라이언트에서 WebSocket에 접속하기위한 endpoint 등록하는 메서드
   *
   * @param registry
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {

    registry.addEndpoint("/chat")

        .setAllowedOrigins("*");

    // 브라우저가 WebSocket을 지원하지 않을 경우 fallback 옵션 활성화
    // test 실행 후 주석 해제 예정
//        .withSockJS();

  }

  /**
   * 한 클라이언트에서 다른 클라이언트로 메시지를 라우팅할 때 사용하는 브로커 구성
   *
   * @param registry
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {

    // @MessageMapping으로 라우팅
    registry.setApplicationDestinationPrefixes("/pub");

    // 해당 주소를 구독하고 있는 클라이언트들에게 메시지 전달
    registry.enableSimpleBroker("/sub");
  }

  /**
   * 인증된 사용자만 받기 위해 chatPreHandler 등록
   *
   * @param registration
   */
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(chatPreHandler);
  }

}

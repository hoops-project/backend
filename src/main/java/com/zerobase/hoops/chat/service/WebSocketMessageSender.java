package com.zerobase.hoops.chat.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketMessageSender implements MessageSender {
  private final SimpMessagingTemplate messagingTemplate;

  public WebSocketMessageSender(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @Override
  public void send(String destination, Object payload) {
    messagingTemplate.convertAndSend(destination, payload);
  }
}
package com.zerobase.hoops.chat.service;

public interface MessageSender {
  void send(String destination, Object payload);
}
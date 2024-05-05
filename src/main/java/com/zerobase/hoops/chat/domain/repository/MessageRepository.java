package com.zerobase.hoops.chat.domain.repository;

import com.zerobase.hoops.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

}

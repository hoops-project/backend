package com.zerobase.hoops.alarm.repository;

import com.zerobase.hoops.entity.NotificationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
List<NotificationEntity> findAllByReceiverUserId(Long userId);
}

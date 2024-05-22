package com.zerobase.hoops.gameCreator.repository;

import com.zerobase.hoops.entity.GameEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends
    JpaRepository<GameEntity, Long> {

  Optional<GameEntity> findByGameIdAndDeletedDateTimeNull(Long gameId);

  long countByDeletedDateTimeNullAndUserEntityUserId(Long userId);

  long countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot(
      LocalDateTime beforeDatetime, LocalDateTime afterDateTime,
      String address, Long gameId);

  List<GameEntity> findByUserEntityUserIdAndDeletedDateTimeNull(Long userId);

  boolean existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull(
      LocalDateTime beforeDatetime,
      LocalDateTime afterDateTime, String address);

  boolean existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot(
      LocalDateTime beforeDatetime, LocalDateTime afterDateTime, String address, Long gameId);
}



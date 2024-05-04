package com.zerobase.hoops.gameCreator.repository;

import com.zerobase.hoops.entity.GameEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.OptionalLong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends
    JpaRepository<GameEntity, Long> {

  OptionalLong countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull(
      LocalDateTime beforeDatetime, LocalDateTime afterDateTime,
      String address);

  Optional<GameEntity> findByGameIdAndDeletedDateTimeNull(Long gameId);

  OptionalLong countByDeletedDateTimeNullAndUserEntityUserId(Long userId);

  OptionalLong countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot(
      LocalDateTime beforeDatetime, LocalDateTime afterDateTime,
      String address, Long gameId);
}



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

  long countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull(
      LocalDateTime beforeDatetime, LocalDateTime afterDateTime,
      String address);

  Optional<GameEntity> findByGameIdAndDeletedDateTimeNull(Long gameId);

  long countByDeletedDateTimeNullAndUserEntityUserId(Long userId);

  long countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot(
      LocalDateTime beforeDatetime, LocalDateTime afterDateTime,
      String address, Long gameId);

  List<GameEntity> findByUserEntityUserIdAndDeletedDateTimeNull(Long userId);
}



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

  Optional<GameEntity> findByIdAndDeletedDateTimeNull(Long gameId);

  List<GameEntity> findByUserIdAndDeletedDateTimeNull(Long userId);



  boolean existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull(
      LocalDateTime beforeDatetime,
      LocalDateTime afterDateTime, String address);

  boolean existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndIdNot(
      LocalDateTime beforeDatetime, LocalDateTime afterDateTime, String address, Long gameId);

}



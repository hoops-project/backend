package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameUserRepository extends
    JpaRepository<GameEntity, Long> {

  @Query("SELECT g FROM game g WHERE g.gameId IN (SELECT MAX(g1.gameId) FROM game g1 GROUP BY g1.gameId) AND g.startDateTime >= CURRENT_DATE")
  List<GameEntity> findAllFromDateToday();

  @Query("SELECT g FROM game g WHERE g.cityName = :cityName AND g.startDateTime >= :today ORDER BY g.startDateTime ASC")
  List<GameEntity> findByCityNameAndStartDateTimeAfterOrderByStartDateTimeAsc(
      CityName cityName, LocalDateTime today);

  List<GameEntity> findByAddressContainingAndStartDateTimeAfterOrderByStartDateTimeAsc(String partOfAddress, LocalDateTime currentDateTime);

}

package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.entity.GameEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameUserRepository extends
    JpaRepository<GameEntity, Long> {

  List<GameEntity> findAll(Specification<GameEntity> spec);

  List<GameEntity> findByAddressContainingIgnoreCaseAndStartDateTimeAfterOrderByStartDateTimeAsc(
      String partOfAddress, LocalDateTime currentDateTime);
  
}

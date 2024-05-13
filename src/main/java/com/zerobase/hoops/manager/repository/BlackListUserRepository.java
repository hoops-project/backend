package com.zerobase.hoops.manager.repository;

import com.zerobase.hoops.entity.BlackListUserEntity;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListUserRepository extends
    JpaRepository<BlackListUserEntity, Long> {

  Optional<BlackListUserEntity> findByBlackUser_IdAndEndDateAfter(
      String id, LocalDate currentDate);

  Optional<BlackListUserEntity> findByBlackUser_EmailAndEndDateAfter(String email,
      LocalDate currentDate);
}

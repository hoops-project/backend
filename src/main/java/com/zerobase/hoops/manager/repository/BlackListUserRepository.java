package com.zerobase.hoops.manager.repository;

import com.zerobase.hoops.entity.BlackListUserEntity;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListUserRepository extends
    JpaRepository<BlackListUserEntity, Long> {

  Optional<BlackListUserEntity> findByBlackUser_IdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
      String id, LocalDate currentDate);

}

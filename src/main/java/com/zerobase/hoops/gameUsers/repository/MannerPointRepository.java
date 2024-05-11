package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.entity.MannerPointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MannerPointRepository extends
    JpaRepository<MannerPointEntity, Long> {

}

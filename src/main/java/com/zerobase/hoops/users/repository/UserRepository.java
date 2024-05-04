package com.zerobase.hoops.users.repository;

import com.zerobase.hoops.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsById(String id);

  boolean existsByEmail(String email);

  boolean existsByNickName(String nickName);

  Optional<UserEntity> findById(String id);

  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findByIdAndDeleteDateTimeNull(String id);

}

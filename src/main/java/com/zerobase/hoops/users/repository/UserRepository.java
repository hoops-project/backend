package com.zerobase.hoops.users.repository;

import com.zerobase.hoops.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByLoginIdAndDeletedDateTimeNull(String loginId);

  boolean existsByEmailAndDeletedDateTimeNull(String email);

  boolean existsByNickNameAndDeletedDateTimeNull(String nickName);

  Optional<UserEntity> findByEmailAndDeletedDateTimeNull(String email);

  Optional<UserEntity> findByLoginIdAndDeletedDateTimeNull(String loginId);

}

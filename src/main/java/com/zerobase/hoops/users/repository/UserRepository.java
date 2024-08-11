package com.zerobase.hoops.users.repository;

import com.zerobase.hoops.document.UserDocument;
import java.util.Optional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends
    ElasticsearchRepository<UserDocument, String> {

  boolean existsByLoginIdAndDeletedDateTimeNull(String loginId);

  boolean existsByEmailAndDeletedDateTimeNull(String email);

  boolean existsByNickNameAndDeletedDateTimeNull(String nickName);

  Optional<UserDocument> findByEmailAndDeletedDateTimeNull(String email);

  Optional<UserDocument> findByLoginIdAndDeletedDateTimeNull(String loginId);

}

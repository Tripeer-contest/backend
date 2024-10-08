package com.j10d207.tripeer.user.db.repository;

import com.j10d207.tripeer.user.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByProviderAndProviderId(String provider, String ProviderId);
    UserEntity findByUserId(long userId);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM user u WHERE u.nickname LIKE %:nickname% AND u.role = 'ROLE_USER'")
    List<UserEntity> findByNicknameContains(@Param("nickname") String nickname);
    UserEntity findByProviderId(String ProviderId);
}


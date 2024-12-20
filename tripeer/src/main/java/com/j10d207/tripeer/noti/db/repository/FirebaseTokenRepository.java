package com.j10d207.tripeer.noti.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.j10d207.tripeer.noti.db.entity.FirebaseToken;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import org.springframework.data.repository.query.Param;

public interface FirebaseTokenRepository extends JpaRepository<FirebaseToken, Long> {

	@Query("SELECT f FROM firebase_token f WHERE f.user IN :coworker AND f.checked = 'UNCHECKED' AND f.user.allowNotifications = true")
	List<FirebaseToken> findAllAvailableByUser(@Param("coworker") List<UserEntity> coworker);

	@Query("SELECT f FROM firebase_token f WHERE f.user IN :coworker")
	List<FirebaseToken> findAllByUser(@Param("coworker") List<UserEntity> coworker);

	@Query("SELECT f FROM firebase_token f WHERE f.token = :firebaseToken AND f.user.userId = :userId")
	Optional<FirebaseToken> findByToken(@Param("firebaseToken") String firebaseToken, @Param("userId") Long userId);

}

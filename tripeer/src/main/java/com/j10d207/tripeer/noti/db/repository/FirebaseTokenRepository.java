package com.j10d207.tripeer.noti.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.j10d207.tripeer.noti.db.entity.FirebaseToken;
import com.j10d207.tripeer.user.db.entity.UserEntity;

public interface FirebaseTokenRepository extends JpaRepository<FirebaseToken, Long> {

	@Query("SELECT f FROM firebase_token f WHERE f.user IN :coworker AND f.checked = 'UNCHECKED'")
	List<FirebaseToken> findAllAvailableByUser(List<UserEntity> coworker);

	@Query("SELECT f FROM firebase_token f WHERE f.user IN :coworker")
	List<FirebaseToken> findAllByUser(List<UserEntity> coworker);
}
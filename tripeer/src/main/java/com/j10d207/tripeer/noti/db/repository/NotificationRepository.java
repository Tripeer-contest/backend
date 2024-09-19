package com.j10d207.tripeer.noti.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.user.db.entity.UserEntity;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("SELECT n FROM notification n WHERE n.user IN :coworker AND n.checked = 'UNCHECKED'")
	List<Notification> findAllByUser(List<UserEntity> coworker);
}

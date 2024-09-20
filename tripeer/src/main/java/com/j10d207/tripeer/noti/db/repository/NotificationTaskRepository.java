package com.j10d207.tripeer.noti.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.j10d207.tripeer.noti.db.entity.NotificationTask;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

	@Query("SELECT n FROM notification_task n where n.state = 'CREATED'")
	List<NotificationTask> findAllWithUnsent();

}

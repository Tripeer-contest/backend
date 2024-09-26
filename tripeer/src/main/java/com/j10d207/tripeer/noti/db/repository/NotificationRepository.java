package com.j10d207.tripeer.noti.db.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.j10d207.tripeer.noti.db.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByIdLessThanAndUserIdAndStateAndStartAtLessThanEqualOrderByIdDesc(Long id, Long userId, Notification.State state, LocalDateTime now, Pageable pageable);

	List<Notification> findByUserIdAndStateAndStartAtLessThanEqualOrderByIdDesc(Long userId, Notification.State state, LocalDateTime now, Pageable pageable);
}

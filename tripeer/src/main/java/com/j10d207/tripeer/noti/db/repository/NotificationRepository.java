package com.j10d207.tripeer.noti.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.j10d207.tripeer.noti.db.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}

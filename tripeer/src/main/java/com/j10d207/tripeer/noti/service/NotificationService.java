package com.j10d207.tripeer.noti.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.noti.db.firebase.MessageBody;
import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.noti.db.repository.NotificationRepository;
import com.j10d207.tripeer.noti.dto.res.NotificationDto;
import com.j10d207.tripeer.noti.dto.res.NotificationList;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	private static final int ITEMS_OFFSET = 1;


	@Transactional
	public Map<Long, Notification> getNotificationMapAfterSave (
		final Map<Long, MessageBody> msgBodyMap,
		final LocalDateTime startAt,
		final Long targetId
	) {
		List<Notification> notifications = msgBodyMap.keySet().stream()
			.map(userId -> Notification.of(msgBodyMap.get(userId), userId, startAt, targetId))
			.toList();

		notificationRepository.saveAll(notifications);

		return notifications.stream()
			.collect(Collectors.toMap(
				Notification::getUserId,
				notification -> notification
			));
	}

	@Transactional
	public Notification getNotificationAfterSave(
		final MessageBody msgBody,
		final Long userId,
		final LocalDateTime startAt,
		final Long targetId
	) {
		final Notification notificaiton = Notification.of(msgBody, userId, startAt, targetId);

		notificationRepository.save(notificaiton);

		return notificaiton;
	}

	@Transactional
	public NotificationList findAllWithReceiveByUser(final Long userId, final Optional<Long> lastId, final int size) {
		final LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		if (lastId.isPresent()) {
			final List<Notification> notificationList = notificationRepository.findByIdLessThanAndUserIdAndStateAndStartAtLessThanEqualOrderByIdDesc(lastId.get(), userId, Notification.State.RECEIVE, now,  Pageable.ofSize(size + ITEMS_OFFSET));
			return getNotificationList(size, notificationList);
		}
		final List<Notification> notificationList = notificationRepository.findByUserIdAndStateAndStartAtLessThanEqualOrderByIdDesc(userId, Notification.State.RECEIVE, now, Pageable.ofSize(size + ITEMS_OFFSET));
		return getNotificationList(size, notificationList);
	}

	private NotificationList getNotificationList(final int size, final List<Notification> notificationList) {
		final List<NotificationDto> dtoList = notificationList.stream()
			.map(notification -> {
				final MessageType type = MessageType.valueOf(notification.getMsgType().name());
				return NotificationDto.of(notification.getId(), type, notification.getTitle(), notification.getContent(), notification.getStartAt().toLocalDate(), notification.getTargetId());
			})
			.toList();
		return NotificationList.of(dtoList, size);
	}

	@Transactional
	public void updateStateToRead(final Long id) {
		final Optional<Notification> notification = notificationRepository.findById(id);
		notification.ifPresentOrElse(
			Notification::toREAD,
			() -> {
				throw new CustomException(ErrorCode.NOT_FOUND_NOTI);
			}
		);
	}
}

package com.j10d207.tripeer.history.db.entity;

import com.j10d207.tripeer.plan.db.entity.PlanDayEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "gallery")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// PK
	private long galleryId;
	private long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UESR_ID")
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_DAY_ID")
	private PlanDayEntity planDay;

	private String url;
}

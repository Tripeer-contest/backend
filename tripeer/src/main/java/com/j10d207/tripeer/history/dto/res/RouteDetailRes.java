package com.j10d207.tripeer.history.dto.res;

import java.time.LocalTime;

import com.j10d207.tripeer.tmap.db.entity.PublicRootDetailEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RouteDetailRes {
	private LocalTime sectionTime;
	private String mode;
	private Integer distance;
	private String route;

	private String startName;
	private double startLat;
	private double startLon;

	private String endName;
	private double endLat;
	private double endLon;

	public static RouteDetailRes from(PublicRootDetailEntity publicRootDetailEntity) {
		int sectionTime = publicRootDetailEntity.getSectionTime();
		return RouteDetailRes.builder()
			.mode(publicRootDetailEntity.getMode())
			.sectionTime(LocalTime.of(sectionTime / 60, sectionTime % 60))
			.route(publicRootDetailEntity.getRoute())
			.distance(publicRootDetailEntity.getDistance())
			.startName(publicRootDetailEntity.getStartName())
			.startLat(publicRootDetailEntity.getStartLat())
			.startLon(publicRootDetailEntity.getStartLon())
			.endName(publicRootDetailEntity.getEndName())
			.endLat(publicRootDetailEntity.getEndLat())
			.endLon(publicRootDetailEntity.getEndLon())
			.build();
	}
}

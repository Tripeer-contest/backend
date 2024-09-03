package com.j10d207.tripeer.place.db.dto;

import java.util.ArrayList;
import java.util.List;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalBaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdditionalDto {
	private String title;
	private String content;

	public static void addIfNotEmpty(List<AdditionalDto> list, String title, String content) {
		if (content != null && !content.isEmpty()) {
			list.add(new AdditionalDto(title, content));
		}
	}

	public static List<AdditionalDto> from(AdditionalBaseEntity additionalBaseEntity) {
		if (additionalBaseEntity == null) {
			return new ArrayList<>();
		}
		return additionalBaseEntity.toDTO();
	}
}

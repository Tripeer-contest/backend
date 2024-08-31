package com.j10d207.tripeer.place.db.dto;

import java.util.List;

import com.j10d207.tripeer.place.db.entity.AdditionalBaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdditionalDto {
	private String title;
	private String content;


	public static void addIfNotEmpty(List<AdditionalDto> list, String title, String content) {
		if (content != null && !content.isEmpty()) {
			list.add(AdditionalDto.builder().title(title).content(content).build());
		}
	}

	public static List<AdditionalDto> from(AdditionalBaseEntity additionalBaseEntity) {
		 return additionalBaseEntity.toDTO();
	}
}

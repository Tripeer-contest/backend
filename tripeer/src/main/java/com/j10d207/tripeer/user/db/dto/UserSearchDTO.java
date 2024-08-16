package com.j10d207.tripeer.user.db.dto;

import com.j10d207.tripeer.user.db.entity.CoworkerEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchDTO {

	private long userId;
	private String nickname;
	private String profileImage;

	public static UserSearchDTO from(CoworkerEntity coworkerEntity) {
		return UserSearchDTO.builder()
			.userId(coworkerEntity.getUser().getUserId())
			.nickname(coworkerEntity.getUser().getNickname())
			.profileImage(coworkerEntity.getUser().getProfileImage())
			.build();
	}
}

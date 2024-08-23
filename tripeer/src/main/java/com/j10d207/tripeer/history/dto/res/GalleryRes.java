package com.j10d207.tripeer.history.dto.res;

import com.j10d207.tripeer.history.db.entity.GalleryEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GalleryRes {
	private long galleryId;
	private String userImg;
	private String userNickname;
	private String img;

	public static GalleryRes from(GalleryEntity galleryEntity) {
		return GalleryRes.builder()
			.galleryId(galleryEntity.getGalleryId())
			.userImg(galleryEntity.getUser().getProfileImage())
			.userNickname(galleryEntity.getUser().getNickname())
			.img(galleryEntity.getUrl())
			.build();
	}

}

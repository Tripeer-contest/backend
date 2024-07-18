package com.j10d207.tripeer.history.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.j10d207.tripeer.history.dto.response.GalleryDTO;

public interface GalleryService {

	// 이미지/동영상 업로드
	public List<GalleryDTO> uploadsImageAndMovie(List<MultipartFile> files, String token, long planDayId);

	public List<GalleryDTO> getGalleryList(long planDayId);

	public String deleteGalleryList(List<Long> galleryIdList);
}

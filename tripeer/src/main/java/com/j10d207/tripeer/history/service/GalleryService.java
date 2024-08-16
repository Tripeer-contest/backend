package com.j10d207.tripeer.history.service;

import com.j10d207.tripeer.history.db.dto.GalleryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GalleryService {

    // 이미지/동영상 업로드
    public List<GalleryDTO> uploadsImageAndMovie(List<MultipartFile> files, long userId, long planDayId);
    public List<GalleryDTO> getGalleryList(long planDayId);
    public String deleteGalleryList(List<Long> galleryIdList);
}

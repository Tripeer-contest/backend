package com.j10d207.tripeer.history.service;

import org.springframework.web.multipart.MultipartFile;
import com.j10d207.tripeer.history.dto.res.GalleryRes;
import java.util.List;

public interface GalleryService {

    // 이미지/동영상 업로드
    public List<GalleryRes> uploadsImageAndMovie(List<MultipartFile> files, long userId, long planDayId);
    public List<GalleryRes> getGalleryList(long planDayId);
    public String deleteGalleryList(List<Long> galleryIdList);
}

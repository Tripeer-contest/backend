package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.dto.req.ReviewReq;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {

    public void saveReview(long userId, ReviewReq reviewReq, List<MultipartFile> multipartFileList);
    public void deleteReview(long userId, long spotReviewId);
}

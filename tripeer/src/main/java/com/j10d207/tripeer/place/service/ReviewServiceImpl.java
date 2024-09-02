package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import com.j10d207.tripeer.place.db.repository.SpotReviewRepository;
import com.j10d207.tripeer.place.dto.req.ReviewReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final SpotReviewRepository spotReviewRepository;

    @Override
    public void saveReview(long userId, ReviewReq reviewReq) {
        spotReviewRepository.save(SpotReviewEntity.ofReviewReq(reviewReq, userId));
    }
}

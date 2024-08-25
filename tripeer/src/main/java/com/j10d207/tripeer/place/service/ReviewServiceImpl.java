package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import com.j10d207.tripeer.place.db.repository.SpotReviewRepository;
import com.j10d207.tripeer.place.db.vo.ReviewVO;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final SpotReviewRepository spotReviewRepository;

    @Override
    public void saveReview(long userId, ReviewVO reviewVO) {
        spotReviewRepository.save(SpotReviewEntity.ofReviewVO(reviewVO, userId));
    }
}

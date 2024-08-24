package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.db.vo.ReviewVO;

public interface ReviewService {

    public void saveReview(long userId, ReviewVO reviewVO);
}

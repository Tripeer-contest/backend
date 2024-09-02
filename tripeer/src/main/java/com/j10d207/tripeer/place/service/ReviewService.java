package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.dto.req.ReviewReq;

public interface ReviewService {

    public void saveReview(long userId, ReviewReq reviewReq);
}

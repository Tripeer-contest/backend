package com.j10d207.tripeer.place.dto.req;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.Queue;


@Getter
public class ReviewReq {

    private long spotReviewId;
    @NotBlank(message = "리뷰 작성 대상 장소가 입력되지 않았습니다.")
    private int spotInfoId;

    private double starPoint;
    @NotBlank(message = "내용이 입력되지 않았습니다.")
    private String message;
    private Queue<String> img;
}

package com.j10d207.tripeer.place.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SpotAddReq {

//    @Min(value = 0, message = "planId error")
//    private int planId;
    @Min(value = 10, message = "지정되지 않은 컨텐츠 타입 범위입니다. ${validatedValue}")
    @Max(40)
    private int contentTypeId;
    @NotBlank(message = "title 값이 입력되지 않았습니다.")
    private String title;
    @NotBlank(message = "주소값이 입력되지 않았습니다.")
    private String addr;
//    private String addr2;
//    private String zipcode;
//    @NotNull(message = "전화번호가 입력되지 않았습니다.")
//    private String tel;
//    private String firstImage;
//    private String secondImage;
    @Min(value = 33, message = "한국영토에 포함되지 않은 위도입니다. ${validatedValue}")
    @Max(value = 39, message = "한국영토에 포함되지 않은 위도입니다. ${validatedValue}")
    private Double latitude;
    @Min(value = 124, message = "한국영토에 포함되지 않은 경도입니다. ${validatedValue}")
    @Max(value = 131, message = "한국영토에 포함되지 않은 경도입니다. ${validatedValue}")
    private Double longitude;
    private String description;
//    private String cat1;
//    private String cat2;
//    private String cat3;
//    private boolean addPlanCheck;

    private int order;
}

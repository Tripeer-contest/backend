package com.j10d207.tripeer.place.db;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ContentTypeEnum {
    ALL_SPOT(-1, "전체"),
    TOURIST_ATTRACTION(12, "관광지"),
    CULTURAL_FACILITY(14, "문화시설"),
    FESTIVAL_EVENT(15, "축제 공연 행사"),
    TRAVEL_COURSE(25, "여행 코스"),
    SPORTS(28, "레포츠"),
    ACCOMMODATION(32, "숙박"),
    SHOPPING(38, "쇼핑"),
    RESTAURANT(39, "맛집"),
    MECCA(100, "명소");

    private final int code;
    private final String name;

    // 상수 값을 이름으로 변환하는 메서드
    public static String getNameByCode(int code) {
        for (ContentTypeEnum contentType : ContentTypeEnum.values()) {
            if (contentType.getCode() == code) {
                return contentType.getName();
            }
        }
        throw new CustomException(ErrorCode.UNDEFINED_TYPE);
    }

    // 상수 값을 대분류 이름으로 변환하는 메서드
    public static String getMajorNameByCode(int code) {
        for (ContentTypeEnum contentType : ContentTypeEnum.values()) {
            if (contentType.getCode() == code && (code == 32 || code == 39)) {
                return contentType.getName();
            } else if (contentType.getCode() == code) {
                return "명소";
            }
        }
        throw new CustomException(ErrorCode.UNDEFINED_TYPE);
    }

    public static ContentTypeEnum getByCode(int code) {
        for (ContentTypeEnum contentTypeEnum : ContentTypeEnum.values()) {
            if (contentTypeEnum.code == code) {
                return contentTypeEnum;
            }
        }
        throw new CustomException(ErrorCode.UNDEFINED_TYPE);
    }

    public static List<Integer> getContentTypeIdListFromSortType(int sortType) {
        return switch (sortType) {
            case 1 -> Arrays.asList(12, 14, 15, 25, 28, 32, 38, 39);
            case 2 -> Arrays.asList(12, 14, 15, 25, 28, 38);
            case 3 -> Collections.singletonList(32);
            case 4 -> Collections.singletonList(39);
            default -> throw new CustomException(ErrorCode.UNDEFINED_TYPE);
        };
    }
}

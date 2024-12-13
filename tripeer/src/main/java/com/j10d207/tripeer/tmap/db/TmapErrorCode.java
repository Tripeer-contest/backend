package com.j10d207.tripeer.tmap.db;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum TmapErrorCode {

    SUCCESS_CAR(0, "차량 경로 성공"),
    SUCCESS_PUBLIC(1, "대중교통 경로 성공"),
    SUCCESS_FERRY(2, "항해 경로 성공"),
    SUCCESS_AIR(3, "항공 경로 성공"),
    NO_PUBLIC_ROUTE_START_END_NEAR(11, "출발지/도착지 간 거리가 가까워서 탐색된 경로 없음"),
    NO_PUBLIC_AND_CAR_ROUTE_START_END_NEAR(411, "차로 우회해도, 출발지/도착지 간 거리가 가까워서 탐색된 경로 없음"),
    NO_PUBLIC_ROUTE_FROM_START_POINT(12, "출발지에서 검색된 정류장이 없어서 탐색된 경로 없음"),
    NO_PUBLIC_AND_CAR_ROUTE_FROM_START_POINT(412, "차로 우회해도, 출발지에서 검색된 정류장이 없어서 탐색된 경로 없음"),
    NO_PUBLIC_ROUTE_FROM_END_POINT(13, "도착지에서 검색된 정류장이 없어서 탐색된 경로 없음"),
    NO_PUBLIC_AND_CAR_ROUTE_FROM_END_POINT(413, "차로 우회해도, 도착지에서 검색된 정류장이 없어서 탐색된 경로 없음"),
    NO_PUBLIC_TRANSPORT_ROUTE(14, "출발지/도착지 간 탐색된 해당 수단의 경로가 없음"),
    NO_PUBLIC_AND_CAR_TRANSPORT_ROUTE(414, "차로 우회해도, 출발지/도착지 간 탐색된 대중교통 경로가 없음"),

    NO_CAR_AND_PUBLIC_TRANSPORT_ROUTE(400, "대중교통으로 우회해도, 자동차 경로가 없음");

    private int code;
    private String message;

    public static TmapErrorCode fromCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode() == code)
                .findFirst().orElseThrow(() -> new CustomException(ErrorCode.UNDEFINED_TYPE));
    }

    public static TmapErrorCode getNext(TmapErrorCode current) {
        return Arrays.stream(TmapErrorCode.values())
                .filter(e -> e.ordinal() > current.ordinal())  // 현재 값보다 큰 값만 필터링
                .findFirst()  // 가장 첫 번째 값을 찾음 (다음 값)
                .orElseThrow(() -> new CustomException(ErrorCode.UNDEFINED_TYPE));  // 없으면 예외 발생
    }

}

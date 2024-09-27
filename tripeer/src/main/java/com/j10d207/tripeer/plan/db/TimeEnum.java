package com.j10d207.tripeer.plan.db;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TimeEnum {

    NEAR_TIME(1),       // 거리가 가까울 때
    MIN_PER_SECOND(60), // 분과 초를 변환할 때
    HOUR_PER_MIN(60),   // 시간과 분을 변환할 때
    ERROR_TIME(99999);  // 오류 시간을 반환할 때

    private int time;
}

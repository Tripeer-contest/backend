package com.j10d207.tripeer.common;


import com.j10d207.tripeer.plan.db.TimeEnum;

public class CommonMethod {

    public static String timeToString(int time) {
        StringBuilder resultTime = new StringBuilder();
        if(time/ TimeEnum.HOUR_PER_MIN.getTime() > 0) {
            resultTime.append(time/TimeEnum.HOUR_PER_MIN.getTime()).append("시간 ");
        }
        resultTime.append(time%TimeEnum.HOUR_PER_MIN.getTime()).append("분");

        return  resultTime.toString();
    }
}

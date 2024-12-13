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

    //Enum화 필요
    public static String convertStatus(int status) {
        if(status == 0 ) return "문의 대기";
        else if (status == 1 ) return "문의 답변 대기중";
        else if (status == 2 ) return "답변 완료";
        else return "";
    }
}

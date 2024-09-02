package com.j10d207.tripeer.place.dto.res.additional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalTourCourseEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TourCourse implements AdditionalInfo {

    private String takeTime;
    private String theme;
    private String distance;
    private String infoCenterTourCourse;
    private String schedule;

    public static AdditionalInfo fromEntity(AdditionalTourCourseEntity entity) {
        return TourCourse.builder()
                .takeTime(entity.getTakeTime())
                .theme(entity.getTheme())
                .distance(entity.getDistance())
                .infoCenterTourCourse(entity.getInfoCenterTourCourse())
                .schedule(entity.getSchedule())
                .build();
    }
}

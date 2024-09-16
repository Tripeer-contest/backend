package com.j10d207.tripeer.place.db.entity.additional;

import java.util.ArrayList;
import java.util.List;

import com.j10d207.tripeer.place.dto.res.AdditionalDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "additional_tour_course")
public class AdditionalTourCourseEntity extends AdditionalBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotInfoId;

    private int contentTypeId;
    private String takeTime;
    private String theme;
    private String distance;
    private String infoCenterTourCourse;
    private String schedule;

    @Override
    public List<AdditionalDto> toDTO() {
        List<AdditionalDto> additionalDtoList = new ArrayList<>();
        AdditionalDto.addIfNotEmpty(additionalDtoList, "소요 시간", this.getTakeTime());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "테마", this.getTheme());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "거리", this.getDistance());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "안내센터 정보", this.getInfoCenterTourCourse());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "일정", this.getSchedule());
        return additionalDtoList;
    }
}

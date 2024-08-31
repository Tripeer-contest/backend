package com.j10d207.tripeer.place.db.entity;

import java.util.ArrayList;
import java.util.List;

import com.j10d207.tripeer.place.db.dto.AdditionalDto;

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
@Table(name = "additional_festival")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalFestivalEntity extends AdditionalBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotInfoId; // PK

    private int contentTypeId;
    private String discountInfoFestival;
    private String eventEndDate;
    private String playTime;
    private String placeInfo;
    private String eventHomepage;
    private String eventPlace;
    private String eventStartDate;
    private String festivalGrade;
    private String program;
    private String spendTimeFestival;
    private String sponsor1;
    private String sponsor1Tel;
    private String sponsor2;
    private String sponsor2Tel;
    private String subEvent;
    private String ageLimit;
    private String bookingPlace;
    private String useTimeFestival;

    @Override
    public List<AdditionalDto> toDTO() {
        List<AdditionalDto> additionalDtoList = new ArrayList<>();
        AdditionalDto.addIfNotEmpty(additionalDtoList, "축제 할인 정보", this.getDiscountInfoFestival());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "행사 종료일", this.getEventEndDate());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "공연 시간", this.getPlayTime());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "행사 장소 정보", this.getPlaceInfo());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "행사 홈페이지", this.getEventHomepage());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "행사 장소", this.getEventPlace());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "행사 시작일", this.getEventStartDate());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "축제 등급", this.getFestivalGrade());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "프로그램 정보", this.getProgram());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "축제 소요 시간", this.getSpendTimeFestival());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "후원사 1", this.getSponsor1());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "후원사 1 연락처", this.getSponsor1Tel());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "후원사 2", this.getSponsor2());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "후원사 2 연락처", this.getSponsor2Tel());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "부대 행사", this.getSubEvent());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "연령 제한", this.getAgeLimit());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "예약 장소", this.getBookingPlace());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "축제 이용 시간", this.getUseTimeFestival());
        return additionalDtoList;
    }
}

package com.j10d207.tripeer.place.dto.res.additional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalFestivalEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Festival implements AdditionalInfo {

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

    public static AdditionalInfo fromEntity(AdditionalFestivalEntity entity) {
        return Festival.builder()
                .discountInfoFestival(entity.getDiscountInfoFestival())
                .eventEndDate(entity.getEventEndDate())
                .playTime(entity.getPlayTime())
                .placeInfo(entity.getPlaceInfo())
                .eventHomepage(entity.getEventHomepage())
                .eventPlace(entity.getEventPlace())
                .eventStartDate(entity.getEventStartDate())
                .festivalGrade(entity.getFestivalGrade())
                .program(entity.getProgram())
                .spendTimeFestival(entity.getSpendTimeFestival())
                .sponsor1(entity.getSponsor1())
                .sponsor1Tel(entity.getSponsor1Tel())
                .sponsor2(entity.getSponsor2())
                .sponsor2Tel(entity.getSponsor2Tel())
                .subEvent(entity.getSubEvent())
                .ageLimit(entity.getAgeLimit())
                .bookingPlace(entity.getBookingPlace())
                .useTimeFestival(entity.getUseTimeFestival())
                .build();
    }
}

package com.j10d207.tripeer.place.db.dto.additional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalTourismEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Tourism implements AdditionalInfo {

    private String accomCount;
    private String chkBabyCarriage;
    private String chkCreditCard;
    private String chkPet;
    private String expAgeRange;
    private String expGuide;
    private String infoCenter;
    private String openDate;
    private String parking;
    private String restDate;
    private String useSeason;
    private String useTime;

    public static AdditionalInfo fromEntity(AdditionalTourismEntity entity) {
        return Tourism.builder()
                .accomCount(entity.getAccomCount())
                .chkBabyCarriage(entity.getChkBabyCarriage())
                .chkCreditCard(entity.getChkCreditCard())
                .chkPet(entity.getChkPet())
                .expAgeRange(entity.getExpAgeRange())
                .expGuide(entity.getExpGuide())
                .infoCenter(entity.getInfoCenter())
                .openDate(entity.getOpenDate())
                .parking(entity.getParking())
                .restDate(entity.getRestDate())
                .useSeason(entity.getUseSeason())
                .useTime(entity.getUseTime())
                .build();
    }
}

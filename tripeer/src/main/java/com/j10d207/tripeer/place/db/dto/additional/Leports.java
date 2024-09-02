package com.j10d207.tripeer.place.db.dto.additional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalLeportsEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Leports implements AdditionalInfo {

    private String scaleLeports;
    private String useFeeLeports;
    private String chkCreditCardLeports;
    private String chkBabyCarriageLeports;
    private String chkPetLeports;
    private String expAgeRangeLeports;
    private String infoCenterLeports;
    private String openPeriod;
    private String parkingFeeLeports;
    private String parkingLeports;
    private String reservation;
    private String restDateLeports;
    private String useTimeLeports;
    private String accomCountLeports;

    public static AdditionalInfo fromEntity(AdditionalLeportsEntity additionalLeportsEntity) {
        return Leports.builder()
                .scaleLeports(additionalLeportsEntity.getScaleLeports())
                .useFeeLeports(additionalLeportsEntity.getUseFeeLeports())
                .chkCreditCardLeports(additionalLeportsEntity.getChkCreditCardLeports())
                .chkBabyCarriageLeports(additionalLeportsEntity.getChkBabyCarriageLeports())
                .chkPetLeports(additionalLeportsEntity.getChkPetLeports())
                .expAgeRangeLeports(additionalLeportsEntity.getExpAgeRangeLeports())
                .infoCenterLeports(additionalLeportsEntity.getInfoCenterLeports())
                .openPeriod(additionalLeportsEntity.getOpenPeriod())
                .parkingFeeLeports(additionalLeportsEntity.getParkingFeeLeports())
                .parkingLeports(additionalLeportsEntity.getParkingLeports())
                .reservation(additionalLeportsEntity.getReservation())
                .restDateLeports(additionalLeportsEntity.getRestDateLeports())
                .useTimeLeports(additionalLeportsEntity.getUseTimeLeports())
                .accomCountLeports(additionalLeportsEntity.getAccomCountLeports())
                .build();
    }
}

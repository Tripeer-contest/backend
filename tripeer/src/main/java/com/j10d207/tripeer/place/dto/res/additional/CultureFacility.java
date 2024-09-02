package com.j10d207.tripeer.place.dto.res.additional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalCultureFacilityEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CultureFacility implements AdditionalInfo {

    private String chkBabyCarriageCulture;
    private String chkCreditCardCulture;
    private String chkPetCulture;
    private String discountInfo;
    private String infoCenterCulture;
    private String parkingCulture;
    private String parkingFee;
    private String restDateCulture;
    private String useFee;
    private String useTimeCulture;
    private String scale;
    private String spendTime;

    public static AdditionalInfo fromEntity(AdditionalCultureFacilityEntity additionalCultureFacilityEntity) {
        return CultureFacility.builder()
                .chkBabyCarriageCulture(additionalCultureFacilityEntity.getChkBabyCarriageCulture())
                .chkCreditCardCulture(additionalCultureFacilityEntity.getChkCreditCardCulture())
                .chkPetCulture(additionalCultureFacilityEntity.getChkPetCulture())
                .discountInfo(additionalCultureFacilityEntity.getDiscountInfo())
                .infoCenterCulture(additionalCultureFacilityEntity.getInfoCenterCulture())
                .parkingCulture(additionalCultureFacilityEntity.getParkingCulture())
                .parkingFee(additionalCultureFacilityEntity.getParkingFee())
                .restDateCulture(additionalCultureFacilityEntity.getRestDateCulture())
                .useFee(additionalCultureFacilityEntity.getUseFee())
                .useTimeCulture(additionalCultureFacilityEntity.getUseTimeCulture())
                .scale(additionalCultureFacilityEntity.getScale())
                .spendTime(additionalCultureFacilityEntity.getSpendTime())
                .build();
    }

}

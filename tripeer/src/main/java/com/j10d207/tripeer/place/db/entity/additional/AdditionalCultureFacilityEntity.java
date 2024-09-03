package com.j10d207.tripeer.place.db.entity.additional;

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
@Table(name = "additional_culture_facility")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalCultureFacilityEntity extends AdditionalBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotInfoId; // PK

    private int contentTypeId;
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

    @Override
    public List<AdditionalDto> toDTO() {
        List<AdditionalDto> additionalDtoList = new ArrayList<>();
        AdditionalDto.addIfNotEmpty(additionalDtoList, "유모차 대여 여부", this.getChkBabyCarriageCulture());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "신용카드 사용 가능 여부", this.getChkCreditCardCulture());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "반려동물 동반 가능 여부", this.getChkPetCulture());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "할인 정보", this.getDiscountInfo());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "안내센터 정보", this.getInfoCenterCulture());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "주차 정보", this.getParkingCulture());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "주차 요금", this.getParkingFee());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "휴무일", this.getRestDateCulture());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "이용 요금", this.getUseFee());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "이용 시간", this.getUseTimeCulture());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "규모", this.getScale());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "소요 시간", this.getSpendTime());
        return additionalDtoList;
    }

}

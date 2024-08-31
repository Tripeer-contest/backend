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
@Table(name = "additional_leports")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalLeportsEntity extends AdditionalBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotInfoId; // PK

    private int contentTypeId;
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

    @Override
    public List<AdditionalDto> toDTO() {
        List<AdditionalDto> additionalDtoList = new ArrayList<>();
        AdditionalDto.addIfNotEmpty(additionalDtoList, "레포츠 규모", this.getScaleLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "레포츠 이용 요금", this.getUseFeeLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "신용카드 사용 가능 여부", this.getChkCreditCardLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "유모차 대여 여부", this.getChkBabyCarriageLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "반려동물 동반 가능 여부", this.getChkPetLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "경험 적합 연령대", this.getExpAgeRangeLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "안내센터 정보", this.getInfoCenterLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "개장 기간", this.getOpenPeriod());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "주차 요금", this.getParkingFeeLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "주차 정보", this.getParkingLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "예약 정보", this.getReservation());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "휴무일", this.getRestDateLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "이용 시간", this.getUseTimeLeports());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "수용 인원 수", this.getAccomCountLeports());
        return additionalDtoList;
    }
}

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
@Table(name = "additional_tourism")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalTourismEntity extends AdditionalBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotInfoId; // PK

    private int contentTypeId;
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

    @Override
    public List<AdditionalDto> toDTO() {
        List<AdditionalDto> additionalDtoList = new ArrayList<>();
        AdditionalDto.addIfNotEmpty(additionalDtoList, "수용 인원 수", this.getAccomCount());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "유모차 대여 여부", this.getChkBabyCarriage());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "신용카드 사용 가능 여부", this.getChkCreditCard());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "반려동물 동반 가능 여부", this.getChkPet());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "경험 적합 연령대", this.getExpAgeRange());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "체험 안내", this.getExpGuide());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "안내센터 정보", this.getInfoCenter());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "개장일", this.getOpenDate());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "주차 정보", this.getParking());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "휴무일", this.getRestDate());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "사용 가능 계절", this.getUseSeason());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "사용 가능 시간", this.getUseTime());
        return additionalDtoList;
    }
}

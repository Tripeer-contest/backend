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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "additional_food")
public class AdditionalFoodEntity extends AdditionalBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotInfoId;

    private int contentTypeId;
    private String chkCreditCardFood;
    private String discountInfoFood;
    private String firstMenu;
    private String infoCenterFood;
    private String kidsFacility;
    private String openDateFood;
    private String openTimeFood;
    private String packing;
    private String parkingFood;
    private String reservationFood;
    private String restDateFood;
    private String scaleFood;
    private String seat;
    private String smoking;
    private String treatMenu;
    private String lcnsNo;

    @Override
    public List<AdditionalDto> toDTO() {
        List<AdditionalDto> additionalDtoList = new ArrayList<>();

        AdditionalDto.addIfNotEmpty(additionalDtoList, "신용카드 사용 가능 여부", this.getChkCreditCardFood());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "할인 정보", this.getDiscountInfoFood());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "대표 메뉴", this.getFirstMenu());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "안내센터 정보", this.getInfoCenterFood());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "어린이 시설 여부", this.getKidsFacility());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "개장일", this.getOpenDateFood());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "영업 시간", this.getOpenTimeFood());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "포장 가능 여부", this.getPacking());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "주차 정보", this.getParkingFood());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "예약 가능 여부", this.getReservationFood());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "휴무일", this.getRestDateFood());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "음식점 규모", this.getScaleFood());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "좌석 수", this.getSeat());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "흡연 가능 여부", this.getSmoking());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "제공 메뉴", this.getTreatMenu());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "사업자 등록 번호", this.getLcnsNo());

        return additionalDtoList;
    }
}

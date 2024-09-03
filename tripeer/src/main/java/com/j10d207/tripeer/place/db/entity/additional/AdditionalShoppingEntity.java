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
@Table(name = "additional_shopping")
public class AdditionalShoppingEntity extends AdditionalBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotInfoId;

    private int contentTypeId;
    private String chkBabyCarriageShopping;
    private String chkCreditCardShopping;
    private String chkPetShopping;
    private String cultureCenter;
    private String fairDay;
    private String infoCenterShopping;
    private String openDateShopping;
    private String openTime;
    private String parkingShopping;
    private String restDateShopping;
    private String restroom;
    private String saleItem;
    private String saleItemCost;
    private String scaleShopping;
    private String shopGuide;

    @Override
    public List<AdditionalDto> toDTO() {
        List<AdditionalDto> additionalDtoList = new ArrayList<>();

        AdditionalDto.addIfNotEmpty(additionalDtoList, "유모차 대여 여부", this.getChkBabyCarriageShopping());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "신용카드 사용 가능 여부", this.getChkCreditCardShopping());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "반려동물 동반 가능 여부", this.getChkPetShopping());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "문화센터 정보", this.getCultureCenter());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "장날 정보", this.getFairDay());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "안내센터 정보", this.getInfoCenterShopping());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "개장일", this.getOpenDateShopping());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "영업 시간", this.getOpenTime());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "주차 정보", this.getParkingShopping());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "휴무일", this.getRestDateShopping());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "화장실 유무", this.getRestroom());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "판매 상품", this.getSaleItem());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "판매 상품 가격", this.getSaleItemCost());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "쇼핑몰 규모", this.getScaleShopping());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "매장 안내", this.getShopGuide());

        return additionalDtoList;
    }
}

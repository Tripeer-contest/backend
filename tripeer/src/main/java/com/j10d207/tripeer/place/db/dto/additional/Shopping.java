package com.j10d207.tripeer.place.db.dto.additional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalShoppingEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Shopping implements AdditionalInfo{

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

    public static AdditionalInfo fromEntity(AdditionalShoppingEntity entity) {
        return Shopping.builder()
                .chkBabyCarriageShopping(entity.getChkBabyCarriageShopping())
                .chkCreditCardShopping(entity.getChkCreditCardShopping())
                .chkPetShopping(entity.getChkPetShopping())
                .cultureCenter(entity.getCultureCenter())
                .fairDay(entity.getFairDay())
                .infoCenterShopping(entity.getInfoCenterShopping())
                .openDateShopping(entity.getOpenDateShopping())
                .openTime(entity.getOpenTime())
                .parkingShopping(entity.getParkingShopping())
                .restDateShopping(entity.getRestDateShopping())
                .restroom(entity.getRestroom())
                .saleItem(entity.getSaleItem())
                .saleItemCost(entity.getSaleItemCost())
                .scaleShopping(entity.getScaleShopping())
                .shopGuide(entity.getShopGuide())
                .build();
    }

}

package com.j10d207.tripeer.place.dto.res.additional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalFoodEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Food implements AdditionalInfo {

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

    public static AdditionalInfo fromEntity(AdditionalFoodEntity entity) {
        return Food.builder()
                .chkCreditCardFood(entity.getChkCreditCardFood())
                .discountInfoFood(entity.getDiscountInfoFood())
                .firstMenu(entity.getFirstMenu())
                .infoCenterFood(entity.getInfoCenterFood())
                .kidsFacility(entity.getKidsFacility())
                .openDateFood(entity.getOpenDateFood())
                .openTimeFood(entity.getOpenTimeFood())
                .packing(entity.getPacking())
                .parkingFood(entity.getParkingFood())
                .reservationFood(entity.getReservationFood())
                .restDateFood(entity.getRestDateFood())
                .scaleFood(entity.getScaleFood())
                .seat(entity.getSeat())
                .smoking(entity.getSmoking())
                .treatMenu(entity.getTreatMenu())
                .lcnsNo(entity.getLcnsNo())
                .build();
    }

}

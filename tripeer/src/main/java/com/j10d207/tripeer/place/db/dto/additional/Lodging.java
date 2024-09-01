package com.j10d207.tripeer.place.db.dto.additional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalLodgingEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Lodging implements AdditionalInfo {

    private String roomCount;
    private String reservationLodging;
    private String reservationUrl;
    private String roomType;
    private String scaleLodging;
    private String subFacility;
    private String barbecue;
    private String beauty;
    private String beverage;
    private String bicycle;
    private String campfire;
    private String fitness;
    private String parkingLodging;
    private String pickup;
    private String publicBath;
    private String foodPlace;
    private String goodStay;
    private String hanok;
    private String infoCenterLodging;
    private String karaoke;
    private String publicPc;
    private String sauna;
    private String seminar;
    private String sports;
    private String refundRegulation;
    private String checkinTime;
    private String checkoutTime;
    private String chkCooking;
    private String accomCountLodging;
    private String benikia;

    public static AdditionalInfo fromEntity(AdditionalLodgingEntity entity) {
        return Lodging.builder()
                .roomCount(entity.getRoomCount())
                .reservationLodging(entity.getReservationLodging())
                .reservationUrl(entity.getReservationUrl())
                .roomType(entity.getRoomType())
                .scaleLodging(entity.getScaleLodging())
                .subFacility(entity.getSubFacility())
                .barbecue(entity.getBarbecue())
                .beauty(entity.getBeauty())
                .beverage(entity.getBeverage())
                .bicycle(entity.getBicycle())
                .campfire(entity.getCampfire())
                .fitness(entity.getFitness())
                .parkingLodging(entity.getParkingLodging())
                .pickup(entity.getPickup())
                .publicBath(entity.getPublicBath())
                .foodPlace(entity.getFoodPlace())
                .goodStay(entity.getGoodStay())
                .hanok(entity.getHanok())
                .infoCenterLodging(entity.getInfoCenterLodging())
                .karaoke(entity.getKaraoke())
                .publicPc(entity.getPublicPc())
                .sauna(entity.getSauna())
                .seminar(entity.getSeminar())
                .sports(entity.getSports())
                .refundRegulation(entity.getRefundRegulation())
                .checkinTime(entity.getCheckinTime())
                .checkoutTime(entity.getCheckoutTime())
                .chkCooking(entity.getChkCooking())
                .accomCountLodging(entity.getAccomCountLodging())
                .benikia(entity.getBenikia())
                .build();
    }

}

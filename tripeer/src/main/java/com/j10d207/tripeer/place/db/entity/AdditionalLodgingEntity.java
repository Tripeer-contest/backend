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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "additional_lodging")
public class AdditionalLodgingEntity extends AdditionalBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spotInfoId;

    private int contentTypeId;
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

    @Override
    public List<AdditionalDto> toDTO() {
        List<AdditionalDto> additionalDtoList = new ArrayList<>();
        AdditionalDto.addIfNotEmpty(additionalDtoList, "객실 수", this.getRoomCount());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "예약 가능 여부", this.getReservationLodging());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "예약 URL", this.getReservationUrl());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "객실 타입", this.getRoomType());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "숙박 시설 규모", this.getScaleLodging());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "부대시설", this.getSubFacility());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "바베큐 시설", this.getBarbecue());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "미용 시설", this.getBeauty());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "음료 제공 여부", this.getBeverage());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "자전거 대여 여부", this.getBicycle());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "캠프파이어 가능 여부", this.getCampfire());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "피트니스 시설", this.getFitness());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "주차 시설", this.getParkingLodging());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "픽업 서비스", this.getPickup());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "공용 목욕 시설", this.getPublicBath());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "음식점", this.getFoodPlace());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "굿스테이 인증 여부", this.getGoodStay());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "한옥 여부", this.getHanok());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "안내센터 정보", this.getInfoCenterLodging());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "노래방 여부", this.getKaraoke());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "공용 PC 제공 여부", this.getPublicPc());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "사우나 시설", this.getSauna());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "세미나실", this.getSeminar());
        AdditionalDto.addIfNotEmpty(additionalDtoList, "스포츠 시설", this.getSports());
        return additionalDtoList;
    }
}

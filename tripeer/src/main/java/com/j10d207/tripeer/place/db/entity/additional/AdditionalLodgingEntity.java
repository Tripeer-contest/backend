package com.j10d207.tripeer.place.db.entity.additional;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "additional_lodging")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalLodgingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//     PK
    private int additionalLodgingId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "spot_info_id")
    private SpotInfoEntity spotInfo;

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
}

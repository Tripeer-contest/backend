package com.j10d207.tripeer.place.db.entity.additional;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "additional_food")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalFoodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//     PK
    private int additionalFoodId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "spot_info_id")
    private SpotInfoEntity spotInfo;

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
}

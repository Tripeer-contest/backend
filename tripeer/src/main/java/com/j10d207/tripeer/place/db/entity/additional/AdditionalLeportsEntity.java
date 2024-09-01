package com.j10d207.tripeer.place.db.entity.additional;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "additional_leports")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalLeportsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//     PK
    private int additionalLeportsId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "spot_info_id")
    private SpotInfoEntity spotInfo;

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
}

package com.j10d207.tripeer.place.db.entity.additional;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "additional_culture_facility")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalCultureFacilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//     PK
    private int additionalCultureFacilityId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "spot_info_id")
    private SpotInfoEntity spotInfo;

    private String chkBabyCarriageCulture;
    private String chkCreditCardCulture;
    private String chkPetCulture;
    private String discountInfo;
    private String infoCenterCulture;
    private String parkingCulture;
    private String parkingFee;
    private String restDateCulture;
    private String useFee;
    private String useTimeCulture;
    private String scale;
    private String spendTime;
}

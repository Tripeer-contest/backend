package com.j10d207.tripeer.place.db.entity.additional;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "additional_tourism")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalTourismEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//     PK
    private int additionalTourismId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "spot_info_id")
    private SpotInfoEntity spotInfo;

    private String accomCount;
    private String chkBabyCarriage;
    private String chkCreditCard;
    private String chkPet;
    private String expAgeRange;
    private String expGuide;
    private String infoCenter;
    private String openDate;
    private String parking;
    private String restDate;
    private String useSeason;
    private String useTime;

}

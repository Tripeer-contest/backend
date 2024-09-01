package com.j10d207.tripeer.place.db.entity.additional;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "additional_festival")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalFestivalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//     PK
    private int additionalFestivalId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "spot_info_id")
    private SpotInfoEntity spotInfo;

    private String discountInfoFestival;
    private String eventEndDate;
    private String playTime;
    private String placeInfo;
    private String eventHomepage;
    private String eventPlace;
    private String eventStartDate;
    private String festivalGrade;
    private String program;
    private String spendTimeFestival;
    private String sponsor1;
    private String sponsor1Tel;
    private String sponsor2;
    private String sponsor2Tel;
    private String subEvent;
    private String ageLimit;
    private String bookingPlace;
    private String useTimeFestival;
}

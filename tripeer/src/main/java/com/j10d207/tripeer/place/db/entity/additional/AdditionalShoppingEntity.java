package com.j10d207.tripeer.place.db.entity.additional;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "additional_shopping")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalShoppingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//     PK
    private int additionalShoppingId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "spot_info_id")
    private SpotInfoEntity spotInfo;

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
}

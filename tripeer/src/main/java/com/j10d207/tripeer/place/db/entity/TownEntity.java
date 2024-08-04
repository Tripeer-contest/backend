package com.j10d207.tripeer.place.db.entity;

import com.j10d207.tripeer.place.db.vo.SpotAddVO;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity(name = "town")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TownEntity {

    @EmbeddedId
    @Cascade(value = CascadeType.PERSIST)
    private TownPK townPK;

    private String townName;
    private String description;
    private String townImg;
    private double latitude;
    private double longitude;

    public static TownEntity MakeNewSpotTownEntity (SpotAddVO spotAddVO, String townName, TownPK townPK) {
        return TownEntity.builder()
                .townName(townName)
                .longitude(spotAddVO.getLongitude())
                .latitude(spotAddVO.getLatitude())
                .description("discription")
                .townImg("https://tripeer207.s3.ap-northeast-2.amazonaws.com/front/static/default1.png")
                .townPK(townPK)
                .build();
    }

}

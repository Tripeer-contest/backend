package com.j10d207.tripeer.place.db.entity;

import com.j10d207.tripeer.user.db.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "spot_star_point")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotStarPointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long spotStarPointId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPOT_INFO_ID")
    private SpotInfoEntity spotInfo;

    private double starPoint;
    private String message;
    private LocalDateTime createTime;
    private String image1;
    private String image2;
    private String image3;
    private String image4;



}

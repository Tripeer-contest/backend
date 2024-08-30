package com.j10d207.tripeer.place.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "city")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    // PK
    private int cityId;
    private String description;
    private String cityImg;
    private String cityName;
    private double latitude;
    private double longitude;
}

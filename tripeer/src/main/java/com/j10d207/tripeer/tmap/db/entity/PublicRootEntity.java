package com.j10d207.tripeer.tmap.db.entity;


import com.nimbusds.jose.shaded.gson.JsonElement;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "public_root")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicRootEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long publicRootId;

    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private int totalDistance;
    private int totalWalkTime;
    private int totalWalkDistance;
    private int pathType;
    private int totalFare;
    private int totalTime;


    public static PublicRootEntity JsonToEntity (JsonElement rootInfo, double SX, double SY, double EX, double EY, int time) {
        return  PublicRootEntity.builder()
                .startLat(SX)
                .startLon(SY)
                .endLat(EX)
                .endLon(EY)
                .totalTime(time)
                .totalDistance(rootInfo.getAsJsonObject().get("totalDistance").getAsInt())
                .totalWalkTime(rootInfo.getAsJsonObject().get("totalWalkTime").getAsInt())
                .totalWalkDistance(rootInfo.getAsJsonObject().get("totalWalkDistance").getAsInt())
                .pathType(rootInfo.getAsJsonObject().get("pathType").getAsInt())
                .totalFare(rootInfo.getAsJsonObject().getAsJsonObject("fare").getAsJsonObject("regular").get("totalFare").getAsInt())
                .build();
    }


}

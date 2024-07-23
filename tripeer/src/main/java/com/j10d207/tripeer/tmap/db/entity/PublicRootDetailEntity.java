package com.j10d207.tripeer.tmap.db.entity;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "public_root_detail")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicRootDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long publicRootDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUBLIC_ROOT_ID")
    private PublicRootEntity publicRoot;

    private String startName;
    private double startLat;
    private double startLon;
    private String endName;
    private double endLat;
    private double endLon;
    private int distance;
    private int sectionTime;
    private String mode;
    private String route;

    public static PublicRootDetailEntity JsonToDTO (JsonElement json, long rootId) {
        JsonObject legObject = json.getAsJsonObject();

        return PublicRootDetailEntity.builder()
                .publicRoot(PublicRootEntity.builder().publicRootId(rootId).build())
                .distance(legObject.get("distance").getAsInt())
                .sectionTime(legObject.get("sectionTime").getAsInt() / 60)
                .mode(legObject.get("mode").getAsString())
                .route(legObject.has("route") ? legObject.get("route").getAsString() : null)
                .startName(legObject.getAsJsonObject("start").get("name").getAsString())
                .startLat(legObject.getAsJsonObject("start").get("lat").getAsDouble())
                .startLon(legObject.getAsJsonObject("start").get("lon").getAsDouble())
                .endName(legObject.getAsJsonObject("end").get("name").getAsString())
                .endLat(legObject.getAsJsonObject("end").get("lat").getAsDouble())
                .endLon(legObject.getAsJsonObject("end").get("lon").getAsDouble())
                .build();

    }
}

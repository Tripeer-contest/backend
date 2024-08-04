package com.j10d207.tripeer.place.db.dto;


import com.j10d207.tripeer.place.db.entity.CityEntity;
import com.j10d207.tripeer.place.db.entity.TownEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CityAndTownDTO {
    private List<TownListDTO> townListDTOList;


    @Getter
    @Builder
    public static class TownListDTO {

        String townName;
        String townImg;
        String description;
        int townId;
        int cityId;

        public static TownListDTO convertToDto(TownEntity townEntity) {

            return TownListDTO.builder()
                    .cityId(townEntity.getTownPK().getCity().getCityId())
                    .townId(townEntity.getTownPK().getTownId())
                    .townImg(townEntity.getTownImg())
                    .description(townEntity.getDescription())
                    .townName(townEntity.getTownName())
                    .build();
        }

        public static TownListDTO convertToDto(CityEntity cityEntity) {

            return TownListDTO.builder()
                    .cityId(cityEntity.getCityId())
                    .townId(-1)
                    .townImg(cityEntity.getCityImg())
                    .description(cityEntity.getDescription())
                    .townName(cityEntity.getCityName())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CityListDTO {

        String cityName;
        String cityImg;
        String description;
        int cityId;

        public static CityListDTO convertToDto(CityEntity cityEntity) {

            return CityListDTO.builder()
                    .cityId(cityEntity.getCityId())
                    .cityImg(cityEntity.getCityImg())
                    .description(cityEntity.getDescription())
                    .cityName(cityEntity.getCityName())
                    .build();
        }
    }

}

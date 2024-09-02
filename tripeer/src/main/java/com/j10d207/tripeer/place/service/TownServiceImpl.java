package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.place.dto.res.CityAndTownDTO;
import com.j10d207.tripeer.place.db.entity.CityEntity;
import com.j10d207.tripeer.place.db.entity.TownEntity;
import com.j10d207.tripeer.place.db.repository.CityRepository;
import com.j10d207.tripeer.place.db.repository.TownRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TownServiceImpl implements TownService{

    private final TownRepository townRepository;
    private final CityRepository cityRepository;

    /*
    * 타운 검색.
    * townName이 -1이라면 city와 연관된 모든 town정보를 주고,
    * townName이 정확하다면 해당 town의 정보를 넘겨줌
    * */
    @Override
    public List<CityAndTownDTO.TownListDTO> searchTown(String cityId, String townName) {
        CityEntity cityEntity = cityRepository.findById(Integer.valueOf(cityId))
                .orElseThrow(() -> new CustomException(ErrorCode.CITY_NOT_FOUND));

        if (Objects.equals(townName, "-1")) {
            List<TownEntity> townEntities = townRepository.findByTownPK_City(cityEntity);
            List<CityAndTownDTO.TownListDTO> townListDTOList = townEntities
                    .stream().map(CityAndTownDTO.TownListDTO::convertToDto).collect(Collectors.toList());

            //overriding해놓은 converToDto로 cityEntity또한 townListDtos에 추가
            townListDTOList.add(0, CityAndTownDTO.TownListDTO.convertToDto(cityEntity));
            return townListDTOList;
        }

        // townName이 유효하게 들어왔을때는 singletonList를 생성하여 반환
        TownEntity townEntity = townRepository.findByTownNameAndTownPK_City_CityId(townName, Integer.parseInt(cityId))
                .orElseThrow(() -> new CustomException(ErrorCode.TOWN_NOT_FOUND));

        return Collections.singletonList(CityAndTownDTO.TownListDTO.convertToDto(townEntity));
    }


    /*
     * 타운 detail정보 조회
     * */
    @Override
    public CityAndTownDTO.TownListDTO townDetail(String townName) {
        TownEntity townEntity = townRepository.findByTownName(townName)
                .orElseThrow(() -> new CustomException(ErrorCode.TOWN_NOT_FOUND));
        return CityAndTownDTO.TownListDTO.convertToDto(townEntity);
    }

    @Override
    public CityAndTownDTO getAllCityAndTown() {
        List<TownEntity> towns = townRepository.findAll();
        List<CityEntity> citys = cityRepository.findAll();

        List<CityAndTownDTO.TownListDTO> townListDTOList = towns.stream().map(CityAndTownDTO.TownListDTO::convertToDto).collect(Collectors.toList());
        List<CityAndTownDTO.TownListDTO> cityListDTOList = citys.stream().map(CityAndTownDTO.TownListDTO::convertToDto).collect(Collectors.toList());

        townListDTOList.addAll(cityListDTOList);


        return new CityAndTownDTO(townListDTOList);
    }
}

package com.j10d207.tripeer.tmap.service;

import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.j10d207.tripeer.plan.db.dto.PublicRootDTO;
import com.j10d207.tripeer.tmap.db.dto.RouteReqDTO;
import com.j10d207.tripeer.tmap.db.entity.PublicRootDetailEntity;
import com.j10d207.tripeer.tmap.db.entity.PublicRootEntity;
import com.j10d207.tripeer.tmap.db.repository.PublicRootDetailRepository;
import com.j10d207.tripeer.tmap.db.repository.PublicRootRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiRequestServiceImpl implements ApiRequestService {

    @Value("${tmap.apikey}")
    private String apikey;

    private final PublicRootRepository publicRootRepository;
    private final PublicRootDetailRepository publicRootDetailRepository;

    // A에서 B로 가는 경로의 정보를 조회 (tMap API 요청)
    @Override
    public JsonObject getResult(double SX, double SY, double EX, double EY) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("appKey", apikey);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "*/*");
        RouteReqDTO route = RouteReqDTO.builder()
                .startX(String.valueOf(SX))
                .startY(String.valueOf(SY))
                .endX(String.valueOf(EX))
                .endY(String.valueOf(EY))
                .build();
        HttpEntity<RouteReqDTO> request = new HttpEntity<>(route, headers);
        String result = restTemplate.postForObject("https://apis.openapi.sk.com/transit/routes", request, String.class);
        return JsonParser.parseString(result).getAsJsonObject();
    }

    //경로 리스트 중에서 제일 좋은 경로 하나를 선정해서 반환 ( 시간 우선 )
    @Override
    public JsonElement getBestTime(JsonArray itineraries) {
        int minTime = Integer.MAX_VALUE;
        JsonElement bestJson = new JsonObject();
        for (JsonElement itinerary : itineraries) {
            int tmpTime = itinerary.getAsJsonObject().get("totalTime").getAsInt();
            int tmpPathType = itinerary.getAsJsonObject().get("pathType").getAsInt();
            // 이동수단이 6-항공일 경우 제외
            if( tmpPathType == 6) {
                continue;
            }

            if ( minTime > tmpTime ) {
                minTime = tmpTime;
                bestJson = itinerary;
            }
        }
        return  bestJson;
    }

    //저장된 결과를 가져와서 DTO로 변환
    @Override
    public PublicRootDTO getRootDTO (PublicRootEntity publicRootEntity) {
        List<PublicRootDetailEntity> publicRootDetailEntityList = publicRootDetailRepository.findByPublicRoot_PublicRootId(publicRootEntity.getPublicRootId());
        return PublicRootDTO.ofEntityAndDetailList(publicRootEntity, publicRootDetailEntityList);
    }

    //최초에 조회된 경로를 저장
    @Override
    public void saveRootInfo(JsonElement rootInfo, double SX, double SY, double EX, double EY, int time) {
        long rootId = 0;
        try {
            rootId = publicRootRepository.save(PublicRootEntity.JsonToEntity(rootInfo, SX, SY, EX, EY, time)).getPublicRootId();
        } catch (DataIntegrityViolationException e ) {
            log.error("DataIntegerityViolationError : " + e.getMessage() );
            return;
        }

        if( rootId > 0) {
            JsonArray legs = rootInfo.getAsJsonObject().getAsJsonArray("legs");
            for (JsonElement leg : legs) {
                publicRootDetailRepository.save(PublicRootDetailEntity.JsonToDTO(leg, rootId));
            }
        }
    }


}

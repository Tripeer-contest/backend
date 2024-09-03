package com.j10d207.tripeer.kakao.service;


import com.google.gson.Gson;
import com.j10d207.tripeer.kakao.db.entity.BlogInfoResponse;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.kakao.db.entity.RouteResponse;
import com.j10d207.tripeer.tmap.db.dto.CoordinateDTO;
import com.j10d207.tripeer.tmap.db.dto.RootInfoDTO;
import com.j10d207.tripeer.tmap.db.entity.PublicRootEntity;
import com.j10d207.tripeer.tmap.db.repository.PublicRootRepository;
import com.j10d207.tripeer.tmap.service.ApiRequestService;
import com.j10d207.tripeer.tmap.service.FindRoot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoServiceImpl implements KakaoService {

    @Value("${kakao.apikey}")
    private String kakaoApiKey;

    @Value("${kakao.apikey2}")
    private String kakaoApiKey2;

    private final ApiRequestService apiRequestService;
    private final PublicRootRepository publicRootRepository;


    @Override
    public FindRoot getOptimizingTime(List<CoordinateDTO> coordinates) throws IOException {

        RootInfoDTO[][] timeTable = getTimeTable(coordinates);
        ArrayList<Integer> startLocation  = new ArrayList<>();
        startLocation.add(0);
        FindRoot root = new FindRoot(timeTable);
        root.solve(0, 0, 0, new ArrayList<>(), startLocation);

        return root;
    }

    @Override
    public BlogInfoResponse getBlogInfo(String query, String sort, int page, int size) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String baseUrl = "https://dapi.kakao.com/v2/search/blog";
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("query", query)
                    .queryParam("sort", sort)
                    .queryParam("page", page)
                    .queryParam("size", size).encode();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey2);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    builder.build().toUri(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            Gson gson = new Gson();
            BlogInfoResponse data = gson.fromJson(response.getBody(), BlogInfoResponse.class);
            return data;

        } catch (RuntimeException e) {
            log.error("Kakao blog 검색중 에러 {}", e.getMessage());
            throw new CustomException(ErrorCode.BLOG_SEARCH_ERROR);
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            throw new RuntimeException();
        }
    }


    @Override
    public RootInfoDTO[][] getTimeTable(List<CoordinateDTO> coordinates) throws IOException {
        RootInfoDTO[][] timeTable = new RootInfoDTO[coordinates.size()][coordinates.size()];
        for (int i = 0; i < timeTable.length; i++) {
            for (int j = 0; j < timeTable[i].length; j++) {
                timeTable[i][j] = new RootInfoDTO(); // TimeRootInfoDTO의 새 인스턴스를 생성하여 할당
            }
        }
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = i; j < coordinates.size(); j++) {

                if (i == j) continue;
                int tmp = getDirections(coordinates.get(i).getLongitude(), coordinates.get(i).getLatitude(), coordinates.get(j).getLongitude(), coordinates.get(j).getLatitude());
                if (tmp == 99999) {
                    timeTable[i][j] = getPublicTime(coordinates.get(i).getLongitude(), coordinates.get(i).getLatitude(), coordinates.get(j).getLongitude(), coordinates.get(j).getLatitude());
                    tmp = timeTable[i][j].getTime();
                    if (tmp == 99999) {
                        throw new CustomException(ErrorCode.NOT_FOUND_ROOT);
                    }
                }
                timeTable[i][j].setTime(tmp);
                timeTable[j][i] = timeTable[i][j];
            }
        }

        return timeTable;
    }


    @Override
    public int getDirections(double SX, double SY, double EX, double EY) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String baseUrl = "https://apis-navi.kakaomobility.com/v1/directions";
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("origin", SX + "," + SY)
                    .queryParam("destination", EX + "," + EY)
                    .queryParam("summary", "true");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            Gson gson = new Gson();
            RouteResponse data = gson.fromJson(response.getBody(), RouteResponse.class);

            return data.getRoutes().getFirst().getSummary().getDuration() / 60;
        } catch (Exception e) {
            return 99999;
        }
    }


    private RootInfoDTO getPublicTime(double SX, double SY, double EX, double EY) {
        Optional<PublicRootEntity> optionalPublicRoot = publicRootRepository.findByStartLatAndStartLonAndEndLatAndEndLon(SX, SY, EX, EY);
        RootInfoDTO rootInfoDTO = new RootInfoDTO();
        rootInfoDTO.setStartLatitude(SX);
        rootInfoDTO.setStartLongitude(SY);
        rootInfoDTO.setEndLatitude(EX);
        rootInfoDTO.setEndLongitude(EY);
        if (optionalPublicRoot.isPresent()) {
            rootInfoDTO.setPublicRoot(apiRequestService.getRootDTO(optionalPublicRoot.get()));
            rootInfoDTO.setTime(rootInfoDTO.getPublicRoot().getTotalTime());
            return rootInfoDTO;
        } else {
            // A에서 B로 가는 경로의 정보를 조회
            JsonObject result = apiRequestService.getResult(SX, SY, EX, EY);

            if (result.getAsJsonObject().has("result")) {
                rootInfoDTO.setTime(99999);
                return rootInfoDTO;
            } else {
                // result.getAsJsonObject().has("metaData")
                JsonObject routeInfo = result.getAsJsonObject("metaData");

                //경로 정보중 제일 좋은 경로를 가져옴
                JsonElement bestRoot = apiRequestService.getBestTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries"));

                //반환 정보 생성
                int totalTime = bestRoot.getAsJsonObject().get("totalTime").getAsInt();
                rootInfoDTO.setTime(totalTime / 60);
                rootInfoDTO.setRootInfo(bestRoot);

                apiRequestService.saveRootInfo(bestRoot, SX, SY, EX, EY, totalTime / 60);

                return rootInfoDTO;
            }

        }

    }

}

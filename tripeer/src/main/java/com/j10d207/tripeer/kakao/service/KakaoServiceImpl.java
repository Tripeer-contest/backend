package com.j10d207.tripeer.kakao.service;


import com.google.gson.Gson;
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

    private final ApiRequestService apiRequestService;
    private final PublicRootRepository publicRootRepository;


    /*
    일정의 목적지 위치 리스트를 받아서 이동 방법 배열을 생성 후 최적화 하는 메소드
    최적화가 완료된 정보를 담은 클래스를 반환한다. Kakao 의 경우 시간 정보만 반
    */
    @Override
    public FindRoot getOptimizingTime(List<CoordinateDTO> coordinates) throws IOException {

        RootInfoDTO[][] timeTable = getTimeTable(coordinates);
        ArrayList<Integer> startLocation  = new ArrayList<>();
        startLocation.add(0);
        FindRoot root = new FindRoot(timeTable);
        root.solve(0, 0, 0, new ArrayList<>(), startLocation);

        return root;
    }


    /*
    목적지들의 리스트를 활용하여 각 목적지-> 다른 목적지의 소요 시간을 조회 한 결과를 2차원 배열로 반환하는 메소드
    AtoB 와 BtoA의 결과가 다를 수 있다.
 */
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
                //차로 못가면 대중교통 경로 조회
                if (tmp == 99999) {
                    timeTable[i][j] = getPublicTime(coordinates.get(i).getLongitude(), coordinates.get(i).getLatitude(), coordinates.get(j).getLongitude(), coordinates.get(j).getLatitude());
                    tmp = timeTable[i][j].getTime();
                    //둘다 없으면 경로 못찾음 throw
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

    /*
    출발지와 도착지의 좌표를 사용하여 이동 시간을 얻어오는 메소드
    출발지 도착지의 좌표는 double 변수, 각 지점의 이름 등은 rootInfoDTO 에 저장된 채로 입력
    산이나 섬같은 경우 차로는 안뜨는 경우가 있어 대중교통 시간으로 우회 경우가 있음
     */
    private RootInfoDTO getPublicTime(double SX, double SY, double EX, double EY) {
        Optional<PublicRootEntity> optionalPublicRoot = publicRootRepository.findByStartLatAndStartLonAndEndLatAndEndLon(SX, SY, EX, EY);
        RootInfoDTO rootInfoDTO = new RootInfoDTO();
        rootInfoDTO.setLocation(SX, SY, EX, EY);
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
                return ApiResponseHasRoot(result.getAsJsonObject("metaData"), rootInfoDTO);
            }

        }

    }

    private RootInfoDTO ApiResponseHasRoot(JsonObject routeInfo, RootInfoDTO rootInfoDTO) {
        //경로 정보중 제일 좋은 경로를 가져옴
        JsonElement bestRoot = apiRequestService.getBestTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries"));
        //모든 경로가 백트래킹 됨
        if(bestRoot.getAsJsonObject().size() == 0) {
            rootInfoDTO.setStatus(414);
            rootInfoDTO.setTime(99999);
            return rootInfoDTO;
        }
        //반환 정보 생성
        int totalTime = bestRoot.getAsJsonObject().get("totalTime").getAsInt();
        rootInfoDTO.setTime(totalTime / 60);
        rootInfoDTO.setRootInfo(bestRoot);

        apiRequestService.saveRootInfo(bestRoot,
                rootInfoDTO.getStartLatitude(),
                rootInfoDTO.getStartLongitude(),
                rootInfoDTO.getEndLatitude(),
                rootInfoDTO.getEndLongitude(),
                totalTime/60);

        return rootInfoDTO;
    }

}

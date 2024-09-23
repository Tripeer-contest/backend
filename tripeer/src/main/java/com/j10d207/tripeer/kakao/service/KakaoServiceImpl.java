package com.j10d207.tripeer.kakao.service;


import com.google.gson.Gson;
import com.j10d207.tripeer.kakao.db.entity.BlogInfoResponse;
import com.j10d207.tripeer.plan.db.TimeEnum;
import com.j10d207.tripeer.plan.dto.res.RootRes;
import com.j10d207.tripeer.tmap.db.TmapErrorCode;
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
import java.util.stream.IntStream;

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


    /*
    목적지들의 리스트를 활용하여 각 목적지-> 다른 목적지의 소요 시간을 조회 한 결과를 2차원 배열로 반환하는 메소드
    AtoB 와 BtoA의 결과가 다를 수 있다.
 */
    @Override
    public RootInfoDTO[][] getTimeTable(List<CoordinateDTO> coordinates) throws IOException {
        RootInfoDTO[][] timeTable = new RootInfoDTO[coordinates.size()][coordinates.size()];

        IntStream.range(0, timeTable.length).forEach(i -> IntStream.range(0, timeTable[i].length)
                .forEach(j ->timeTable[i][j] = new RootInfoDTO()));
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = i; j < coordinates.size(); j++) {
                if (i == j) continue;
                int tmp = getDirections(coordinates.get(i).getLongitude(), coordinates.get(i).getLatitude(), coordinates.get(j).getLongitude(), coordinates.get(j).getLatitude());
                //차로 못가면 대중교통 경로 조회
                if (tmp == TimeEnum.ERROR_TIME.getTime()) {
                    timeTable[i][j].setStatus(TmapErrorCode.NO_PUBLIC_TRANSPORT_ROUTE);
                } else {
                    timeTable[i][j].setStatus(TmapErrorCode.SUCCESS_CAR);
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

            return data.getRoutes().getFirst().getSummary().getDuration() / TimeEnum.MIN_PER_SECOND.getTime();
        } catch (Exception e) {
            return TimeEnum.ERROR_TIME.getTime();
        }
    }

    /*
    출발지와 도착지의 좌표를 사용하여 이동 시간을 얻어오는 메소드
    출발지 도착지의 좌표는 double 변수, 각 지점의 이름 등은 rootInfoDTO 에 저장된 채로 입력
    산이나 섬같은 경우 차로는 안뜨는 경우가 있어 대중교통 시간으로 우회 경우가 있음
     */
    private RootInfoDTO getPublicTime(double SX, double SY, double EX, double EY) {
        Optional<PublicRootEntity> optionalPublicRoot = publicRootRepository.findByStartLatAndStartLonAndEndLatAndEndLon(SX, SY, EX, EY);
        RootInfoDTO rootInfoDTO = RootInfoDTO.createOfLocation(SX, SY, EX, EY);

        return optionalPublicRoot.map(publicRoot -> {
            rootInfoDTO.setPublicRoot(apiRequestService.getRootDTO(optionalPublicRoot.get()));
            rootInfoDTO.setTime(rootInfoDTO.getPublicRoot().getTotalTime());
            return rootInfoDTO;
        }).orElseGet(() -> {
            // A에서 B로 가는 경로의 정보를 조회
            JsonObject result = apiRequestService.getResult(SX, SY, EX, EY);

            return Optional.of(result.getAsJsonObject())
                    .filter(json -> json.has("result"))
                    .map(json -> {
                        //임시, 종류가 많아질경우 Tmap에서 rename 하는게 좋아보임
                        rootInfoDTO.setStatus(TmapErrorCode.NO_CAR_AND_PUBLIC_TRANSPORT_ROUTE);
                        return rootInfoDTO;
                    })
                    .orElseGet(() -> ApiResponseHasRoot(result.getAsJsonObject("metaData"), rootInfoDTO));
        });

    }

    private RootInfoDTO ApiResponseHasRoot(JsonObject routeInfo, RootInfoDTO rootInfoDTO) {
        //경로 정보중 제일 좋은 경로를 가져옴
        JsonElement bestRoot = apiRequestService.getBestTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries"));
        //모든 경로가 백트래킹 됨
        if(bestRoot.getAsJsonObject().size() == 0) {
            rootInfoDTO.setStatus(TmapErrorCode.NO_PUBLIC_AND_CAR_TRANSPORT_ROUTE);
            rootInfoDTO.setTime(TimeEnum.ERROR_TIME.getTime());
            return rootInfoDTO;
        }
        //반환 정보 생성
        int totalTime = bestRoot.getAsJsonObject().get("totalTime").getAsInt();
        rootInfoDTO.setStatus(TmapErrorCode.SUCCESS_CAR);
        rootInfoDTO.setTime(totalTime / TimeEnum.MIN_PER_SECOND.getTime());
        rootInfoDTO.setRootInfo(bestRoot);

        apiRequestService.saveRootInfo(bestRoot,
                rootInfoDTO.getStartLatitude(),
                rootInfoDTO.getStartLongitude(),
                rootInfoDTO.getEndLatitude(),
                rootInfoDTO.getEndLongitude(),
                totalTime/TimeEnum.MIN_PER_SECOND.getTime());

        return rootInfoDTO;
    }

    @Override
    public RootRes setCarResult(int resultTime, RootRes rootRes) {
        StringBuilder rootInfoBuilder = new StringBuilder();
        List<String[]> timeList = new ArrayList<>();
        if( resultTime == TimeEnum.ERROR_TIME.getTime()) {
            rootRes.setOption(TmapErrorCode.NO_CAR_AND_PUBLIC_TRANSPORT_ROUTE.getCode());
            rootInfoBuilder.append("경로를 찾을 수 없습니다.");
            timeList.add(new String[] {rootInfoBuilder.toString(), "2" } );
            rootRes.setSpotTime(timeList);
            return rootRes;
        }
        if(resultTime/TimeEnum.HOUR_PER_MIN.getTime() > 0) {
            rootInfoBuilder.append(resultTime/TimeEnum.HOUR_PER_MIN.getTime()).append("시간 ");
        }
        rootInfoBuilder.append(resultTime%TimeEnum.HOUR_PER_MIN.getTime()).append("분");
        timeList.add(new String[] {rootInfoBuilder.toString(), String.valueOf(rootRes.getOption()) } );
        rootRes.setSpotTime(timeList);
        return rootRes;
    }

}

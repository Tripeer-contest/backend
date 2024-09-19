package com.j10d207.tripeer.tmap.service;


import com.j10d207.tripeer.plan.dto.res.RootOptimizeDTO;
import com.j10d207.tripeer.tmap.db.dto.PublicRootDTO;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.kakao.service.KakaoService;
import com.j10d207.tripeer.tmap.db.dto.CoordinateDTO;
import com.j10d207.tripeer.tmap.db.dto.RootInfoDTO;
import com.j10d207.tripeer.tmap.db.entity.PublicRootEntity;
import com.j10d207.tripeer.tmap.db.repository.PublicRootRepository;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TMapServiceImpl implements TMapService {

    @Value("${tmap.apikey}")
    private String apikey;

    private final ApiRequestService apiRequestService;
    private final KakaoService kakaoService;
    private final PublicRootRepository publicRootRepository;

    /*
    일정의 목적지 위치 리스트를 받아서 이동 방법 배열을 생성 후 최적화 하는 메소드
    최적화가 완료된 정보를 담은 클래스를 반환한다.
     */
    @Override
    public FindRoot getOptimizingTime(List<CoordinateDTO> coordinates) {
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
    public RootInfoDTO[][] getTimeTable(List<CoordinateDTO> coordinates) {

        RootInfoDTO[][] timeTable = new RootInfoDTO[coordinates.size()][coordinates.size()];
        for (int i = 0; i < timeTable.length; i++) {
            for (int j = 0; j < timeTable[i].length; j++) {
                timeTable[i][j] = new RootInfoDTO(); // RootInfoDTO의 새 인스턴스를 생성하여 할당
            }
        }
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = 0; j < coordinates.size(); j++) {

                // 자신에게 or 출발지에서 목적지로는 계산하지 않음
                if(i == j || (i == 0 && j== coordinates.size() - 1) || (i == coordinates.size() - 1 && j == 0) ) continue;

                // 출발지, 목적지 이름 설정
                timeTable[i][j].setStartTitle(coordinates.get(i).getTitle());
                timeTable[i][j].setEndTitle(coordinates.get(j).getTitle());

                //경로 추적
                timeTable[i][j] = getPublicTime(coordinates.get(i).getLongitude(), coordinates.get(i).getLatitude(), coordinates.get(j).getLongitude(), coordinates.get(j).getLatitude(), timeTable[i][j]);

            }
        }

        return timeTable;
    }

    /*
    출발지와 도착지의 좌표를 사용하여 이동 시간과 방법(뭘타고 뭐로 환승하는지)을 얻어오는 메소드
    출발지 도착지의 좌표는 double변수, 각 지점의 이름 등은 rootInfoDTO에 저장된 채로 입력
     -> 왜 좌표는 rootInfoDTO에 안넣었지?, 좌표를 따로 쓰는경우가 많은데 그때마다 get하는게 비효율적이라서 ???
     */
    @Override
    public RootInfoDTO getPublicTime(double SX, double SY, double EX, double EY, RootInfoDTO rootInfoDTO) {
        Optional<PublicRootEntity> optionalPublicRoot = publicRootRepository.findByStartLatAndStartLonAndEndLatAndEndLon(SX, SY, EX, EY);
        rootInfoDTO.setLocation(SX, SY, EX, EY);
        //최근에 조회한 경로가 있는경우
        if(optionalPublicRoot.isPresent()){
            rootInfoDTO.setPublicRoot(apiRequestService.getRootDTO(optionalPublicRoot.get()));
            rootInfoDTO.setTime(rootInfoDTO.getPublicRoot().getTotalTime());
            return rootInfoDTO;
        } else {
            // A에서 B로 가는 경로의 정보를 조회
            JsonObject result = apiRequestService.getResult(SX, SY, EX, EY);

            if (result.getAsJsonObject().has("result")) {
                //error 발생은 아니지만 일반적인 조회 성공 결과가 없는경우,
                return ApiResponseHasNonRoot(result.getAsJsonObject("result").get("status").getAsInt(), rootInfoDTO);
            } else {
                //조회 결과가 경로가 있는경우
                return ApiResponseHasRoot(result.getAsJsonObject("metaData"), rootInfoDTO);
            }

        }
    }

    /*
    대중교통 길찾기를 사용하기 위해 TMap API 를 요청하는 메소드
    입력 파라미터로 출발지와 목적지의 정보를 담은 DTO 를 입력받는다.
     */
    @Override
    public RootOptimizeDTO useTMapPublic (RootOptimizeDTO rootOptimizeDTO) {
        RootInfoDTO baseInfo = RootInfoDTO.builder()
                .startTitle(rootOptimizeDTO.getPlaceList().getFirst().getTitle())
                .endTitle(rootOptimizeDTO.getPlaceList().getLast().getTitle())
                .build();

        RootInfoDTO result = getPublicTime(rootOptimizeDTO.getPlaceList().getFirst().getLongitude(),
                rootOptimizeDTO.getPlaceList().getFirst().getLatitude(),
                rootOptimizeDTO.getPlaceList().getLast().getLongitude(),
                rootOptimizeDTO.getPlaceList().getLast().getLatitude(), baseInfo);

        if (result.getStatus() == 0) {
            return tMapApiSuccessCode(rootOptimizeDTO, result);
        } else {
            rootOptimizeDTO.setOption(result.getStatus());
            rootOptimizeDTO.setSpotTime(tMapApiErrorCodeFilter(result.getStatus()));
            return rootOptimizeDTO;
        }
    }

    /*
    TMap Api 요청시 오류가 반환되었을 경우 error 코드내용을 분석해 처리하는 메소드
     */
    private List<String[]> tMapApiErrorCodeFilter (int code) {
        List<String[]> timeList = new ArrayList<>();
        StringBuilder time = new StringBuilder();
        switch (code) {
            //11 -출발지/도착지 간 거리가 가까워서 탐색된 경로 없음
            //12 -출발지에서 검색된 정류장이 없어서 탐색된 경로 없음
            //13 -도착지에서 검색된 정류장이 없어서 탐색된 경로 없음
            //14 -출발지/도착지 간 탐색된 대중교통 경로가 없음
            case 11:
            case 411:
                time.append("출발지/도착지 간 거리가 가까워서 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            case 12:
            case 412:
                time.append("출발지에서 검색된 정류장이 없어 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            case 13:
            case 413:
                time.append("도착지에서 검색된 정류장이 없어 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            case 14:
            case 414:
                time.append("출발지/도착지 간 탐색된 대중교통 경로가 없어 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            default:
                throw new CustomException(ErrorCode.ROOT_API_ERROR);
        }

        return timeList;
    }

    /*
    TMap Api 요청시 이미 조회된 경로와 아닌경우를 분류해서 DTO 로 변환하는 메소드
     */
    private RootOptimizeDTO tMapApiSuccessCode(RootOptimizeDTO rootOptimizeDTO, RootInfoDTO rootInfoDTO) {
        rootOptimizeDTO.setSpotTime(Collections.singletonList(new String[]{rootInfoDTO.timeToString(), String.valueOf(rootOptimizeDTO.getOption())}));

        if (rootInfoDTO.getPublicRoot() != null ) {
            List<PublicRootDTO> publicRootDTOList = new ArrayList<>();
            publicRootDTOList.add(rootInfoDTO.getPublicRoot());
            rootOptimizeDTO.setPublicRootList(publicRootDTOList);
            return rootOptimizeDTO;
        } else {
            return MakeRootInfo(rootOptimizeDTO, rootInfoDTO.getRootInfo());
        }
    }

    /*
    조회 결과 오류는 아니지만 경로가 없기때문에 해당 결과를 정제하여 반환하는 메소드
     */
    private RootInfoDTO ApiResponseHasNonRoot(int status, RootInfoDTO rootInfoDTO) {
        switch (status) {
            case 11:
            case 12:
            case 13:
            case 14:
                //11 -출발지/도착지 간 거리가 가까워서 탐색된 경로 없음
                //12 -출발지에서 검색된 정류장이 없어서 탐색된 경로 없음
                //13 -도착지에서 검색된 정류장이 없어서 탐색된 경로 없음
                //14 -출발지/도착지 간 탐색된 대중교통 경로가 없음
                int tmp = kakaoService.getDirections(rootInfoDTO.getStartLatitude(),
                        rootInfoDTO.getStartLongitude(),
                        rootInfoDTO.getEndLatitude(),
                        rootInfoDTO.getEndLongitude());
                if (tmp == 99999) {
                    rootInfoDTO.setStatus(400 + status);
                    rootInfoDTO.setTime(tmp);
                } else {
                    rootInfoDTO.setStatus(status);
                    rootInfoDTO.setTime(kakaoService.getDirections(rootInfoDTO.getStartLatitude(),
                            rootInfoDTO.getStartLongitude(),
                            rootInfoDTO.getEndLatitude(),
                            rootInfoDTO.getEndLongitude()));
                }
                break;
            default:
                throw new CustomException(ErrorCode.ROOT_API_ERROR);
        }
        return rootInfoDTO;
    }

    /*
    조회된 경로들 중 비현실적인 수단을 백트래킹하고, 남은 경로중 가장 시간이 적은 경로를 선택한 후
    정보들을 정제하여 반환하는 메소드
     */
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

    /*
    Json 정제
     */
    private RootOptimizeDTO MakeRootInfo(RootOptimizeDTO rootOptimizeDTO, JsonElement rootInfo) {
        if(rootInfo == null) {
            List<PublicRootDTO> rootList = new ArrayList<>();
            if(rootOptimizeDTO.getPublicRootList() != null) {
                rootList = rootOptimizeDTO.getPublicRootList();
            }
            rootList.add(null);
            rootOptimizeDTO.setPublicRootList(rootList);
            return rootOptimizeDTO;
        }
        JsonObject infoObject = rootInfo.getAsJsonObject();

        PublicRootDTO publicRoot = PublicRootDTO.fromJson(infoObject);


        List<PublicRootDTO> rootList = new ArrayList<>();
        if(rootOptimizeDTO.getPublicRootList() != null) {
            rootList = rootOptimizeDTO.getPublicRootList();
        }
        rootList.add(publicRoot);
        rootOptimizeDTO.setPublicRootList(rootList);

        return rootOptimizeDTO;
    }


}

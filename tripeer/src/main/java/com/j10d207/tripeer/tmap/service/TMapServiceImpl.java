package com.j10d207.tripeer.tmap.service;


import com.j10d207.tripeer.plan.db.TimeEnum;
import com.j10d207.tripeer.plan.dto.req.PlaceListReq;
import com.j10d207.tripeer.plan.dto.res.RootRes;
import com.j10d207.tripeer.tmap.db.TmapErrorCode;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

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
        ArrayList<Integer> startLocation = new ArrayList<>(List.of(0));
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

        IntStream.range(0, timeTable.length).forEach(i -> IntStream.range(0, timeTable[i].length)
                .forEach(j ->timeTable[i][j] = new RootInfoDTO()));

        //for 문을 사용하는 것이 가독성이 더 좋다고 판단하여 유지
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = 0; j < coordinates.size(); j++) {

                // 자신에게 or 출발지에서 목적지로는 계산하지 않음
                if(i == j || (i == 0 && j== coordinates.size() - 1) || (i == coordinates.size() - 1 && j == 0) ) continue;
                // 출발지, 목적지 이름 설정
                timeTable[i][j].setStartTitle(coordinates.get(i).getTitle());
                timeTable[i][j].setEndTitle(coordinates.get(j).getTitle());
                //경로 추적
//                timeTable[i][j] = getPublicTime(coordinates.get(i).getLongitude(),
//                        coordinates.get(i).getLatitude(),
//                        coordinates.get(j).getLongitude(),
//                        coordinates.get(j).getLatitude(),
//                        timeTable[i][j]);

                List<RootInfoDTO> tmp = getPublicTime3(coordinates.get(i).getLongitude(),
                        coordinates.get(i).getLatitude(),
                        coordinates.get(j).getLongitude(),
                        coordinates.get(j).getLatitude(),
                        timeTable[i][j]);

                int minTime = Math.min(Math.min(tmp.get(0).getTime(), tmp.get(1).getTime()), tmp.get(2).getTime());
                if (minTime == 0)System.out.println("0 감지");
                if(minTime == tmp.get(0).getTime()) timeTable[i][j] = tmp.get(0);
                else if(minTime == tmp.get(1).getTime()) timeTable[i][j] = tmp.get(1);
                else if(minTime == tmp.get(2).getTime()) timeTable[i][j] = tmp.get(2);

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
        return optionalPublicRoot
                //최근에 조회한 경로가 있는경우
                .map(publicRoot -> {
                    rootInfoDTO.setStatus(TmapErrorCode.SUCCESS);
                    rootInfoDTO.setPublicRoot(apiRequestService.getRootDTO(publicRoot));
                    rootInfoDTO.setTime(rootInfoDTO.getPublicRoot().getTotalTime());
                    return rootInfoDTO;
                })
                .orElseGet(() -> {
                    // A에서 B로 가는 경로의 정보를 조회
                    JsonObject result = apiRequestService.getResult(SX, SY, EX, EY);
                    // 조회 결과에 따라 적절한 응답 반환
                    return result.getAsJsonObject().has("result")
                            ? ApiResponseHasNonRoot(result.getAsJsonObject("result").get("status").getAsInt(), rootInfoDTO)
                            : ApiResponseHasRoot(result.getAsJsonObject("metaData"), rootInfoDTO);
                });
    }

    @Override
    public RootInfoDTO getPublicTime2(double SX, double SY, double EX, double EY, RootInfoDTO rootInfoDTO) {
        Optional<PublicRootEntity> optionalPublicRoot = publicRootRepository.findByStartLatAndStartLonAndEndLatAndEndLon(SX, SY, EX, EY);
        rootInfoDTO.setLocation(SX, SY, EX, EY);
        return optionalPublicRoot
                //최근에 조회한 경로가 있는경우
                .map(publicRoot -> {
                    rootInfoDTO.setStatus(TmapErrorCode.SUCCESS);
                    rootInfoDTO.setPublicRoot(apiRequestService.getRootDTO(publicRoot));
                    rootInfoDTO.setTime(rootInfoDTO.getPublicRoot().getTotalTime());
                    return rootInfoDTO;
                })
                .orElseGet(() -> {
                    // A에서 B로 가는 경로의 정보를 조회
                    JsonObject result = apiRequestService.getResult(SX, SY, EX, EY);
                    // 조회 결과에 따라 적절한 응답 반환
                    return result.getAsJsonObject().has("result")
                            ? ApiResponseHasNonRoot2(result.getAsJsonObject("result").get("status").getAsInt(), rootInfoDTO)
                            : ApiResponseHasRoot2(result.getAsJsonObject("metaData"), rootInfoDTO);
                });
    }

    @Override
    public List<RootInfoDTO> getPublicTime3(double SX, double SY, double EX, double EY, RootInfoDTO rootInfoDTO) {
        Optional<PublicRootEntity> optionalPublicRoot = publicRootRepository.findByStartLatAndStartLonAndEndLatAndEndLon(SX, SY, EX, EY);
        rootInfoDTO.setLocation(SX, SY, EX, EY);
        return optionalPublicRoot
                //최근에 조회한 경로가 있는경우
                .map(publicRoot -> {
                    rootInfoDTO.setStatus(TmapErrorCode.SUCCESS);
                    rootInfoDTO.setPublicRoot(apiRequestService.getRootDTO(publicRoot));
                    rootInfoDTO.setTime(rootInfoDTO.getPublicRoot().getTotalTime());
                    return List.of(rootInfoDTO);
                })
                .orElseGet(() -> {
                    // A에서 B로 가는 경로의 정보를 조회
                    JsonObject result = apiRequestService.getResult(SX, SY, EX, EY);
                    // 조회 결과에 따라 적절한 응답 반환
                    return result.getAsJsonObject().has("result")
                            ? ApiResponseHasNonRoot3(result.getAsJsonObject("result").get("status").getAsInt())
                            : ApiResponseHasRoot3(result.getAsJsonObject("metaData"));
                });
    }

    /*
    대중교통 길찾기를 사용하기 위해 TMap API 를 요청하는 메소드
    입력 파라미터로 출발지와 목적지의 정보를 담은 DTO 를 입력받는다.
     */
    @Override
    public RootRes useTMapPublic (RootRes rootRes) {
        RootInfoDTO baseInfo = RootInfoDTO.builder()
                .startTitle(rootRes.getPlaceList().getFirst().getTitle())
                .endTitle(rootRes.getPlaceList().getLast().getTitle())
                .build();

        RootInfoDTO result = getPublicTime(rootRes.getPlaceList().getFirst().getLongitude(),
                rootRes.getPlaceList().getFirst().getLatitude(),
                rootRes.getPlaceList().getLast().getLongitude(),
                rootRes.getPlaceList().getLast().getLatitude(), baseInfo);

        if (result.getStatus() == TmapErrorCode.SUCCESS) {
            return tMapApiSuccessCode(rootRes, result);
        } else {
            rootRes.setOption(result.getStatus().getCode());
            rootRes.setSpotTime(tMapApiErrorCodeFilter(result.getStatus()));
            return rootRes;
        }
    }

    @Override
    public RootInfoDTO useTMapPublic2 (PlaceListReq placeListReq) {
        RootInfoDTO baseInfo = RootInfoDTO.builder()
                .startTitle(placeListReq.getPlaceList().getFirst().getTitle())
                .endTitle(placeListReq.getPlaceList().getLast().getTitle())
                .build();

        return getPublicTime2(placeListReq.getPlaceList().getFirst().getLongitude(),
                placeListReq.getPlaceList().getFirst().getLatitude(),
                placeListReq.getPlaceList().getLast().getLongitude(),
                placeListReq.getPlaceList().getLast().getLatitude(), baseInfo);
    }

    @Override
    public List<RootInfoDTO> useTMapPublic3 (PlaceListReq placeListReq) {
        RootInfoDTO baseInfo = RootInfoDTO.builder()
                .startTitle(placeListReq.getPlaceList().getFirst().getTitle())
                .endTitle(placeListReq.getPlaceList().getLast().getTitle())
                .build();

        return getPublicTime3(placeListReq.getPlaceList().getFirst().getLongitude(),
                placeListReq.getPlaceList().getFirst().getLatitude(),
                placeListReq.getPlaceList().getLast().getLongitude(),
                placeListReq.getPlaceList().getLast().getLatitude(), baseInfo);
    }

    /*
    TMap Api 요청시 오류가 반환되었을 경우 error 코드내용을 분석해 처리하는 메소드
     */
    private List<String[]> tMapApiErrorCodeFilter (TmapErrorCode errorCode) {
        List<String[]> timeList = new ArrayList<>();
        StringBuilder time = new StringBuilder();
        switch (errorCode) {
            case NO_PUBLIC_ROUTE_START_END_NEAR:
            case NO_PUBLIC_AND_CAR_ROUTE_START_END_NEAR:
                time.append("출발지/도착지 간 거리가 가까워서 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            case NO_PUBLIC_ROUTE_FROM_START_POINT:
            case NO_PUBLIC_AND_CAR_ROUTE_FROM_START_POINT:
                time.append("출발지에서 검색된 정류장이 없어 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            case NO_PUBLIC_ROUTE_FROM_END_POINT:
            case NO_PUBLIC_AND_CAR_ROUTE_FROM_END_POINT:
                time.append("도착지에서 검색된 정류장이 없어 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            case NO_PUBLIC_TRANSPORT_ROUTE:
            case NO_PUBLIC_AND_CAR_TRANSPORT_ROUTE:
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
    private RootRes tMapApiSuccessCode(RootRes rootRes, RootInfoDTO rootInfoDTO) {
        rootRes.setSpotTime(Collections.singletonList(new String[]{rootInfoDTO.timeToString(), String.valueOf(rootRes.getOption())}));

        if (rootInfoDTO.getPublicRoot() != null ) {
            List<PublicRootDTO> publicRootDTOList = new ArrayList<>();
            publicRootDTOList.add(rootInfoDTO.getPublicRoot());
            rootRes.setPublicRootList(publicRootDTOList);
            return rootRes;
        } else {
            return MakeRootInfo(rootRes, rootInfoDTO.getRootInfo());
        }
    }

    /*
    조회 결과 오류는 아니지만 경로가 없기때문에 해당 결과를 정제하여 반환하는 메소드
     */
    private RootInfoDTO ApiResponseHasNonRoot(int status, RootInfoDTO rootInfoDTO) {
        TmapErrorCode errorCode = TmapErrorCode.fromCode(status);
        switch (errorCode) {
            case NO_PUBLIC_ROUTE_START_END_NEAR:
            case NO_PUBLIC_ROUTE_FROM_START_POINT:
            case NO_PUBLIC_ROUTE_FROM_END_POINT:
            case NO_PUBLIC_TRANSPORT_ROUTE:
                int tmp = kakaoService.getDirections(rootInfoDTO.getStartLatitude(),
                        rootInfoDTO.getStartLongitude(),
                        rootInfoDTO.getEndLatitude(),
                        rootInfoDTO.getEndLongitude());
                if (tmp == TimeEnum.ERROR_TIME.getTime()) {
                    rootInfoDTO.setStatus(TmapErrorCode.getNext(errorCode));
                    rootInfoDTO.setTime(tmp);
                } else {
                    rootInfoDTO.setStatus(errorCode);
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

    private RootInfoDTO ApiResponseHasNonRoot2(int status, RootInfoDTO rootInfoDTO) {
        rootInfoDTO.setStatus(TmapErrorCode.fromCode(status));
        return rootInfoDTO;
    }

    private List<RootInfoDTO> ApiResponseHasNonRoot3(int status) {
        return List.of(RootInfoDTO.builder().status(TmapErrorCode.fromCode(status)).build(),
                RootInfoDTO.builder().status(TmapErrorCode.fromCode(status)).build(),
                RootInfoDTO.builder().status(TmapErrorCode.fromCode(status)).build());
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
            rootInfoDTO.setStatus(TmapErrorCode.NO_PUBLIC_AND_CAR_TRANSPORT_ROUTE);
            rootInfoDTO.setTime(TimeEnum.ERROR_TIME.getTime());
            return rootInfoDTO;
        }
        //반환 정보 생성
        int totalTime = bestRoot.getAsJsonObject().get("totalTime").getAsInt();
        rootInfoDTO.setTime(totalTime / TimeEnum.HOUR_PER_MIN.getTime());
        rootInfoDTO.setRootInfo(bestRoot);
        rootInfoDTO.setStatus(TmapErrorCode.SUCCESS);

//        apiRequestService.saveRootInfo(bestRoot,
//                rootInfoDTO.getStartLatitude(),
//                rootInfoDTO.getStartLongitude(),
//                rootInfoDTO.getEndLatitude(),
//                rootInfoDTO.getEndLongitude(),
//                totalTime / TimeEnum.HOUR_PER_MIN.getTime());

        return rootInfoDTO;
    }

    /*
    조회된 경로들 중 비현실적인 수단을 백트래킹하고, 남은 경로중 가장 시간이 적은 경로를 선택한 후
    정보들을 정제하여 반환하는 메소드
     */

    private List<RootInfoDTO> ApiResponseHasRoot3 (JsonObject routeInfo) {
        List<RootInfoDTO> rootInfoDTOList = new ArrayList<>();
        //경로 정보중 제일 좋은 경로를 가져옴
        JsonElement bestRoot = apiRequestService.getBestTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries"));
        //경로 정보중 제일 좋은 경로를 가져옴
        JsonElement bestAirRoot = apiRequestService.getBestAirTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries"));
        //경로 정보중 제일 좋은 경로를 가져옴
        JsonElement bestFerryRoot = apiRequestService.getBestFerryTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries"));

        rootInfoDTOList.add(RootJsonRefactor(bestRoot, 1));
        rootInfoDTOList.add(RootJsonRefactor(bestFerryRoot, 2));
        rootInfoDTOList.add(RootJsonRefactor(bestAirRoot, 2));
        return rootInfoDTOList;
    }
    private RootInfoDTO ApiResponseHasRoot2(JsonObject routeInfo, RootInfoDTO rootInfoDTO) {
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
        rootInfoDTO.setTime(totalTime / TimeEnum.HOUR_PER_MIN.getTime());
//        rootInfoDTO.setRootInfo(bestRoot);
        rootInfoDTO.setPublicRoot(PublicRootDTO.fromJson(bestRoot.getAsJsonObject()));
        rootInfoDTO.setStatus(TmapErrorCode.SUCCESS);

//        apiRequestService.saveRootInfo(bestRoot,
//                rootInfoDTO.getStartLatitude(),
//                rootInfoDTO.getStartLongitude(),
//                rootInfoDTO.getEndLatitude(),
//                rootInfoDTO.getEndLongitude(),
//                totalTime / TimeEnum.HOUR_PER_MIN.getTime());

        return rootInfoDTO;
    }

    private RootInfoDTO RootJsonRefactor(JsonElement root, int option) {
        RootInfoDTO rootInfoDTO = new RootInfoDTO();
        //모든 경로가 백트래킹 됨
        if(root.getAsJsonObject().size() == 0) {
            rootInfoDTO.setStatus(TmapErrorCode.NO_PUBLIC_TRANSPORT_ROUTE);
            rootInfoDTO.setTime(TimeEnum.ERROR_TIME.getTime());
            return rootInfoDTO;
        }
        //반환 정보 생성
        int totalTime = root.getAsJsonObject().get("totalTime").getAsInt();
        rootInfoDTO.setTime(totalTime / TimeEnum.HOUR_PER_MIN.getTime());
//        rootInfoDTO.setRootInfo(bestRoot);
        rootInfoDTO.setPublicRoot(PublicRootDTO.fromJson(root.getAsJsonObject()));
        rootInfoDTO.setStatus(TmapErrorCode.fromCode(option));

//        apiRequestService.saveRootInfo(bestRoot,
//                rootInfoDTO.getStartLatitude(),
//                rootInfoDTO.getStartLongitude(),
//                rootInfoDTO.getEndLatitude(),
//                rootInfoDTO.getEndLongitude(),
//                totalTime / TimeEnum.HOUR_PER_MIN.getTime());

        return rootInfoDTO;
    }

    /*
    Json 정제
     */
    private RootRes MakeRootInfo(RootRes rootRes, JsonElement rootInfo) {
        if(rootInfo == null) {
            List<PublicRootDTO> rootList = new ArrayList<>();
            if(rootRes.getPublicRootList() != null) {
                rootList = rootRes.getPublicRootList();
            }
            rootList.add(null);
            rootRes.setPublicRootList(rootList);
            return rootRes;
        }
        JsonObject infoObject = rootInfo.getAsJsonObject();

        PublicRootDTO publicRoot = PublicRootDTO.fromJson(infoObject);


        List<PublicRootDTO> rootList = new ArrayList<>();
        if(rootRes.getPublicRootList() != null) {
            rootList = rootRes.getPublicRootList();
        }
        rootList.add(publicRoot);
        rootRes.setPublicRootList(rootList);

        return rootRes;
    }


}

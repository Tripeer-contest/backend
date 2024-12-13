package com.j10d207.tripeer.tmap.service;


import com.j10d207.tripeer.plan.db.TimeEnum;
import com.j10d207.tripeer.plan.dto.req.PlaceListReq;
import com.j10d207.tripeer.tmap.db.TmapErrorCode;
import com.j10d207.tripeer.tmap.db.dto.PublicRootDTO;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.j10d207.tripeer.tmap.db.dto.CoordinateDTO;
import com.j10d207.tripeer.tmap.db.dto.RootInfoDTO;
import com.j10d207.tripeer.tmap.db.entity.PublicRootEntity;
import com.j10d207.tripeer.tmap.db.repository.PublicRootRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TMapServiceImpl implements TMapService {

    @Value("${tmap.apikey}")
    private String apikey;

    private final ApiRequestService apiRequestService;
    private final PublicRootRepository publicRootRepository;

    /*
    일정의 목적지 위치 리스트를 받아서 이동 방법 배열을 생성 후 최적화 하는 메소드
    최적화가 완료된 정보를 담은 클래스를 반환한다.
     */
    @Override
    public FindRoot getOptimizingTime(List<CoordinateDTO> coordinates, int option) {
        RootInfoDTO[][] timeTable = getTimeTable(coordinates, option);
        ArrayList<Integer> startLocation = new ArrayList<>(List.of(0));
        FindRoot root = new FindRoot(timeTable);
        root.solve(0, 0, 0, new ArrayList<>(), startLocation);

        return root;
    }

    private RootInfoDTO[][] getTimeTable(List<CoordinateDTO> coordinates, int option) {
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
                if(option == 1) {
                    timeTable[i][j] = getPublicTime(coordinates.get(i).getLongitude(),
                            coordinates.get(i).getLatitude(),
                            coordinates.get(j).getLongitude(),
                            coordinates.get(j).getLatitude(),
                            timeTable[i][j]);
                } else {
                    List<RootInfoDTO> tmp = getPublicTimeList(coordinates.get(i).getLongitude(),
                            coordinates.get(i).getLatitude(),
                            coordinates.get(j).getLongitude(),
                            coordinates.get(j).getLatitude(),
                            timeTable[i][j]);

                    timeTable[i][j] = tmp.stream()
                            .min(Comparator.comparingInt(RootInfoDTO::getTime))
                            .orElseThrow(() -> new IllegalArgumentException("List is empty"));

                }

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
        Optional<PublicRootEntity> optionalPublicRoot = publicRootRepository.findByStartLatAndStartLonAndEndLatAndEndLonAndTypeOption(SX, SY, EX, EY, 1);
        rootInfoDTO.setLocation(SX, SY, EX, EY);
        return optionalPublicRoot
                //최근에 조회한 경로가 있는경우
                .map(publicRoot -> {
                    rootInfoDTO.setStatus(TmapErrorCode.SUCCESS_PUBLIC);
                    rootInfoDTO.setPublicRoot(apiRequestService.getRootDTO(publicRoot));
                    rootInfoDTO.setTime(rootInfoDTO.getPublicRoot().getTotalTime());
                    return rootInfoDTO;
                })
                .orElseGet(() -> {
                    // A에서 B로 가는 경로의 정보를 조회
                    JsonObject result = apiRequestService.getResult(SX, SY, EX, EY);
                    // 조회 결과에 따라 적절한 응답 반환
                    return result.getAsJsonObject().has("result")
                            ? ApiResponseHasNonRoot(result.getAsJsonObject("result").get("status").getAsInt())
                            : ApiResponseHasRoot(result.getAsJsonObject("metaData"), rootInfoDTO);
                });
    }


    @Override
    public List<RootInfoDTO> getPublicTimeList(double SX, double SY, double EX, double EY, RootInfoDTO rootInfoDTO) {
        Optional<List<PublicRootEntity>> optionalPublicRoot = Optional.ofNullable(publicRootRepository.findByStartLatAndStartLonAndEndLatAndEndLon(SX, SY, EX, EY));
        rootInfoDTO.setLocation(SX, SY, EX, EY);
        return optionalPublicRoot
                .filter(list -> !list.isEmpty())
                //최근에 조회한 경로가 있는경우
                .map(publicRoot -> {
                    rootInfoDTO.setStatus(TmapErrorCode.NO_PUBLIC_TRANSPORT_ROUTE);
                    rootInfoDTO.setTime(TimeEnum.ERROR_TIME.getTime());
                    List<RootInfoDTO> rootInfoDTOList = new ArrayList<>(Arrays.asList(rootInfoDTO, rootInfoDTO, rootInfoDTO));
                    for (PublicRootEntity publicRootEntity : publicRoot) {
                        int option = publicRootEntity.getTypeOption();
                        RootInfoDTO result = new RootInfoDTO();
                        // 시간 경로 상태
                        result.setStatus(TmapErrorCode.fromCode(publicRootEntity.getTypeOption()));
                        result.setPublicRoot(apiRequestService.getRootDTO(publicRootEntity));
                        result.setTime(result.getPublicRoot().getTotalTime());
                        rootInfoDTOList.set(option-1, result);
                    }
                    return rootInfoDTOList;
                })
                .orElseGet(() -> {
                    // A에서 B로 가는 경로의 정보를 조회
                    JsonObject result = apiRequestService.getResult(SX, SY, EX, EY);
                    // 조회 결과에 따라 적절한 응답 반환
                    return result.getAsJsonObject().has("result")
                            ? ApiResponseHasNonRoot3(result.getAsJsonObject("result").get("status").getAsInt())
                            : ApiResponseHasRoot3(result.getAsJsonObject("metaData"), rootInfoDTO);
                });
    }

    @Override
    public List<RootInfoDTO> useTMapPublic(PlaceListReq placeListReq) {
        RootInfoDTO baseInfo = RootInfoDTO.builder()
                .startTitle(placeListReq.getPlaceList().getFirst().getTitle())
                .endTitle(placeListReq.getPlaceList().getLast().getTitle())
                .build();

        return getPublicTimeList(placeListReq.getPlaceList().getFirst().getLongitude(),
                placeListReq.getPlaceList().getFirst().getLatitude(),
                placeListReq.getPlaceList().getLast().getLongitude(),
                placeListReq.getPlaceList().getLast().getLatitude(), baseInfo);
    }

    /*
    조회 결과 오류는 아니지만 경로가 없기때문에 해당 결과를 정제하여 반환하는 메소드
     */
    private RootInfoDTO ApiResponseHasNonRoot(int status) {
        return RootInfoDTO.fromStatus(status);
    }


    private List<RootInfoDTO> ApiResponseHasNonRoot3(int status) {
        return List.of(RootInfoDTO.fromStatus(status),
                RootInfoDTO.builder().time(TimeEnum.ERROR_TIME.getTime()).status(TmapErrorCode.fromCode(status)).build(),
                RootInfoDTO.builder().time(TimeEnum.ERROR_TIME.getTime()).status(TmapErrorCode.fromCode(status)).build());
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
        rootInfoDTO.setStatus(TmapErrorCode.SUCCESS_PUBLIC);

        RootJsonRefactor(apiRequestService.getBestFerryTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries")), 2, rootInfoDTO);
        RootJsonRefactor(apiRequestService.getBestAirTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries")), 3, rootInfoDTO);

        apiRequestService.saveRootInfo(bestRoot,
                rootInfoDTO.getStartLatitude(),
                rootInfoDTO.getStartLongitude(),
                rootInfoDTO.getEndLatitude(),
                rootInfoDTO.getEndLongitude(),
                totalTime / TimeEnum.HOUR_PER_MIN.getTime(),
                1);

        return rootInfoDTO;
    }

    /*
    조회된 경로들 중 비현실적인 수단을 백트래킹하고, 남은 경로중 가장 시간이 적은 경로를 선택한 후
    정보들을 정제하여 반환하는 메소드
     */

    private List<RootInfoDTO> ApiResponseHasRoot3 (JsonObject routeInfo, RootInfoDTO rootInfoDTO) {
        List<RootInfoDTO> rootInfoDTOList = new ArrayList<>();
        //경로 정보중 제일 좋은 경로를 가져옴
        JsonElement bestRoot = apiRequestService.getBestTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries"));
        //경로 정보중 제일 좋은 경로를 가져옴
        JsonElement bestAirRoot = apiRequestService.getBestAirTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries"));
        //경로 정보중 제일 좋은 경로를 가져옴
        JsonElement bestFerryRoot = apiRequestService.getBestFerryTime(routeInfo.getAsJsonObject("plan").getAsJsonArray("itineraries"));

        rootInfoDTOList.add(RootJsonRefactor(bestRoot, 1, rootInfoDTO));
        rootInfoDTOList.add(RootJsonRefactor(bestFerryRoot, 2, rootInfoDTO));
        rootInfoDTOList.add(RootJsonRefactor(bestAirRoot, 3, rootInfoDTO));
        return rootInfoDTOList;
    }

    private RootInfoDTO RootJsonRefactor(JsonElement root, int option, RootInfoDTO oldInfo) {
        RootInfoDTO rootInfoDTO = new RootInfoDTO();
        rootInfoDTO.setLocation(oldInfo.getStartLatitude(), oldInfo.getStartLongitude(), oldInfo.getEndLatitude(), oldInfo.getEndLongitude());
        //모든 경로가 백트래킹 됨
        if(root.getAsJsonObject().size() == 0) {
            rootInfoDTO.setStatus(TmapErrorCode.NO_PUBLIC_TRANSPORT_ROUTE);
            rootInfoDTO.setTime(TimeEnum.ERROR_TIME.getTime());
            return rootInfoDTO;
        }
        //반환 정보 생성
        int totalTime = root.getAsJsonObject().get("totalTime").getAsInt();
        rootInfoDTO.setTime(totalTime / TimeEnum.HOUR_PER_MIN.getTime());
        rootInfoDTO.setPublicRoot(PublicRootDTO.fromJson(root.getAsJsonObject()));
        rootInfoDTO.setStatus(TmapErrorCode.fromCode(option));

        apiRequestService.saveRootInfo(root,
                rootInfoDTO.getStartLatitude(),
                rootInfoDTO.getStartLongitude(),
                rootInfoDTO.getEndLatitude(),
                rootInfoDTO.getEndLongitude(),
                totalTime / TimeEnum.HOUR_PER_MIN.getTime(),
                option);

        return rootInfoDTO;
    }


}

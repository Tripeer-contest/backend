package com.j10d207.tripeer.tmap.service;

import com.j10d207.tripeer.plan.db.dto.RootOptimizeDTO;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class TMapServiceImpl implements TMapService {

    @Value("${tmap.apikey}")
    private String apikey;

    private final ApiRequestService apiRequestService;
    private final KakaoService kakaoService;
    private final PublicRootRepository publicRootRepository;

    @Override
    public FindRoot getOptimizingTime(List<CoordinateDTO> coordinates) {
        RootInfoDTO[][] timeTable = getTimeTable(coordinates);
        ArrayList<Integer> startLocation  = new ArrayList<>();
        startLocation.add(0);
        FindRoot root = new FindRoot(timeTable);
        root.solve(0, 0, 0, new ArrayList<>(), startLocation);

        return root;
    }

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

    @Override
    public RootInfoDTO getPublicTime(double SX, double SY, double EX, double EY, RootInfoDTO rootInfoDTO) {
        Optional<PublicRootEntity> optionalPublicRoot = publicRootRepository.findByStartLatAndStartLonAndEndLatAndEndLon(SX, SY, EX, EY);
        rootInfoDTO.setLocation(SX, SY, EX, EY);
        if(optionalPublicRoot.isPresent()){
            rootInfoDTO.setPublicRoot(apiRequestService.getRootDTO(optionalPublicRoot.get()));
            rootInfoDTO.setTime(rootInfoDTO.getPublicRoot().getTotalTime());
            return rootInfoDTO;
        } else {
            // A에서 B로 가는 경로의 정보를 조회
            JsonObject result = apiRequestService.getResult(SX, SY, EX, EY);

            if (result.getAsJsonObject().has("result")) {
                int status = result.getAsJsonObject("result").get("status").getAsInt();
                switch (status) {
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                        //11 -출발지/도착지 간 거리가 가까워서 탐색된 경로 없음
                        //12 -출발지에서 검색된 정류장이 없어서 탐색된 경로 없음
                        //13 -도착지에서 검색된 정류장이 없어서 탐색된 경로 없음
                        //14 -출발지/도착지 간 탐색된 대중교통 경로가 없음
                        int tmp = kakaoService.getDirections(SX, SY, EX, EY);
                        if (tmp == 99999) {
                            rootInfoDTO.setStatus(400 + status);
                            rootInfoDTO.setTime(tmp);
                        } else {
                            rootInfoDTO.setStatus(status);
                            rootInfoDTO.setTime(kakaoService.getDirections(SX, SY, EX, EY));
                        }
                        break;
                    default:
                        throw new CustomException(ErrorCode.ROOT_API_ERROR);
                }
                return rootInfoDTO;
            } else {
                // result.getAsJsonObject().has("metaData")
                JsonObject routeInfo = result.getAsJsonObject("metaData");

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

                apiRequestService.saveRootInfo(bestRoot, SX, SY, EX, EY, totalTime/60);

                return rootInfoDTO;
            }

        }
    }

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

    private List<String[]> tMapApiErrorCodeFilter (int code) {
        List<String[]> timeList = new ArrayList<>();
        StringBuilder time = new StringBuilder();
        switch (code) {
//            case 0:
//                if(result.getTime()/60 > 0) {
//                    time.append(result.getTime()/60).append("시간 ");
//                }
//                time.append(result.getTime()%60).append("분");
//
//                timeList.add(new String[]{time.toString(), String.valueOf(rootOptimizeDTO.getOption()) });
//                rootOptimizeDTO.setSpotTime(timeList);
//
//                JsonElement rootInfo = result.getRootInfo();
//                if (result.getPublicRoot() != null ) {
//                    List<PublicRootDTO> publicRootDTOList = new ArrayList<>();
//                    publicRootDTOList.add(result.getPublicRoot());
//                    rootOptimizeDTO.setPublicRootList(publicRootDTOList);
//                    return rootOptimizeDTO;
//                } else {
//                    return MakeRootInfo(rootOptimizeDTO, rootInfo);
//                }
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

        PublicRootDTO publicRoot = PublicRootDTO.JsonToDTO(infoObject);


        List<PublicRootDTO> rootList = new ArrayList<>();
        if(rootOptimizeDTO.getPublicRootList() != null) {
            rootList = rootOptimizeDTO.getPublicRootList();
        }
        rootList.add(publicRoot);
        rootOptimizeDTO.setPublicRootList(rootList);

        return rootOptimizeDTO;
    }


}

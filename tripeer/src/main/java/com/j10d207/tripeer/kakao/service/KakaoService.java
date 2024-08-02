package com.j10d207.tripeer.kakao.service;


import com.j10d207.tripeer.tmap.db.dto.CoordinateDTO;
import com.j10d207.tripeer.tmap.db.dto.RootInfoDTO;
import com.j10d207.tripeer.tmap.service.FindRoot;

import java.io.IOException;
import java.util.List;

public interface KakaoService {

    public int getDirections(double SX, double SY, double EX, double EY);
    public RootInfoDTO[][] getTimeTable(List<CoordinateDTO> coordinates) throws IOException;
    public FindRoot getOptimizingTime(List<CoordinateDTO> coordinates) throws IOException;


}
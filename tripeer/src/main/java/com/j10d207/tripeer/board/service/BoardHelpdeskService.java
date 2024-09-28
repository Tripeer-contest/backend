package com.j10d207.tripeer.board.service;

import com.j10d207.tripeer.board.dto.req.BoardWriteReq;
import com.j10d207.tripeer.board.dto.res.HelpdeskRes;

public interface BoardHelpdeskService {

    //문의사항 작성
    void writeHelpdesk(BoardWriteReq boardWriteReq, long userId);

    //문의사항 목록 조회
    HelpdeskRes getHelpdeskList(int page, long userId);

    //문의사항 내용 조회
    HelpdeskRes.Detail getDetail(long helpdeskId, long userId);

    //문의사항 삭제
    void deleteHelpdesk(long helpdeskId);
}

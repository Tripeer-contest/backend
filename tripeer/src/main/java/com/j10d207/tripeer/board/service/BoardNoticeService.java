package com.j10d207.tripeer.board.service;

import com.j10d207.tripeer.board.dto.req.BoardWriteReq;

public interface BoardNoticeService {

    //공지사항 작성
    public void writeNotice(BoardWriteReq boardWriteReq, long userId);
}

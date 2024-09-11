package com.j10d207.tripeer.board.service;

import com.j10d207.tripeer.board.dto.req.BoardWriteReq;
import com.j10d207.tripeer.board.dto.res.NoticeRes;

public interface BoardNoticeService {

    //공지사항 작성
    void writeNotice(BoardWriteReq boardWriteReq, long userId);

    //공지사항 목록 조회
    NoticeRes getNoticeList(int page);

    //공지사항 내용 조회
    NoticeRes.Detail getDetail(long noticeId);

    //공지사항 삭제
    void deleteNotice(long noticeId);
}

package com.j10d207.tripeer.board.service;

import com.j10d207.tripeer.board.db.entity.BoardNoticeEntity;
import com.j10d207.tripeer.board.db.repository.BoardNoticeRepository;
import com.j10d207.tripeer.board.dto.req.BoardWriteReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardNoticeServiceImpl implements BoardNoticeService{

    private final BoardNoticeRepository boardNoticeRepository;

    //공지사항 작성
    @Override
    public void writeNotice(BoardWriteReq boardWriteReq, long userId) {
        BoardNoticeEntity boardNoticeEntity = BoardNoticeEntity.ofReq(boardWriteReq, userId);
        boardNoticeRepository.save(boardNoticeEntity);
    }
}

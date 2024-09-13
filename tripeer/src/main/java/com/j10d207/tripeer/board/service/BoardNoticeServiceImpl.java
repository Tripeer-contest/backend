package com.j10d207.tripeer.board.service;

import com.j10d207.tripeer.board.db.entity.BoardNoticeEntity;
import com.j10d207.tripeer.board.db.repository.BoardNoticeRepository;
import com.j10d207.tripeer.board.dto.req.BoardWriteReq;
import com.j10d207.tripeer.board.dto.res.NoticeRes;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardNoticeServiceImpl implements BoardNoticeService{

    private final BoardNoticeRepository boardNoticeRepository;

    private final int NOTICE_PER_PAGE = 6;

    //공지사항 작성
    @Override
    public void writeNotice(BoardWriteReq boardWriteReq, long userId) {
        BoardNoticeEntity boardNoticeEntity = BoardNoticeEntity.ofReq(boardWriteReq, userId);
        boardNoticeRepository.save(boardNoticeEntity);
    }

    //공지사항 목록 조회
    @Override
    public NoticeRes getNoticeList(int page) {
        if(page < 1) throw new CustomException(ErrorCode.INVALID_PAGE);
        Pageable pageable = PageRequest.of(page-1, NOTICE_PER_PAGE, Sort.by(Sort.Direction.DESC, "noticeId"));
        Page<BoardNoticeEntity> boardNoticeEntityPage = boardNoticeRepository.findAll(pageable);

        return new NoticeRes(boardNoticeEntityPage.getTotalPages(),
                boardNoticeEntityPage.getContent().stream().map(NoticeRes.Detail::fromEntity).toList());
    }

    //공지사항 내용 조회
    @Override
    public NoticeRes.Detail getDetail(long noticeId) {
        BoardNoticeEntity boardNoticeEntity = boardNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEARCH_NULL));

        return NoticeRes.Detail.fromEntity(boardNoticeEntity);
    }

    @Override
    public void deleteNotice(long noticeId) {
        boardNoticeRepository.deleteById(noticeId);
    }


}

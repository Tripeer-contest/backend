package com.j10d207.tripeer.board.service;

import com.j10d207.tripeer.board.db.entity.BoardHelpdeskEntity;
import com.j10d207.tripeer.board.db.entity.BoardNoticeEntity;
import com.j10d207.tripeer.board.db.repository.BoardHelpdeskRepository;
import com.j10d207.tripeer.board.dto.req.BoardWriteReq;
import com.j10d207.tripeer.board.dto.res.HelpdeskRes;
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
public class BoardHelpdeskServiceImpl implements BoardHelpdeskService {

    private final BoardHelpdeskRepository boardHelpdeskRepository;

    private final int NOTICE_PER_PAGE = 6;

    //문의사항 작성
    @Override
    public void writeHelpdesk(BoardWriteReq boardWriteReq, long userId) {
        BoardHelpdeskEntity boardHelpdeskEntity = BoardHelpdeskEntity.ofReq(boardWriteReq, userId);
        boardHelpdeskRepository.save(boardHelpdeskEntity);
    }

    //문의사항 목록 조회
    @Override
    public HelpdeskRes getHelpdeskList(int page, long userId) {
        if(page < 1) throw new CustomException(ErrorCode.INVALID_PAGE);
        Pageable pageable = PageRequest.of(page-1, NOTICE_PER_PAGE, Sort.by(Sort.Direction.DESC, "helpdeskId"));
        Page<BoardHelpdeskEntity> boardHelpdeskEntityPage = boardHelpdeskRepository.findByUser_UserId(userId, pageable);

        return new HelpdeskRes(boardHelpdeskEntityPage.getTotalPages(),
                boardHelpdeskEntityPage.getContent().stream().map(HelpdeskRes.Detail::fromEntity).toList());
    }

    //문의사항 내용 조회
    @Override
    public HelpdeskRes.Detail getDetail(long helpdeskId, long userId) {
        BoardHelpdeskEntity boardHelpdeskEntity = boardHelpdeskRepository.findById(helpdeskId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEARCH_NULL));

        if(userId != boardHelpdeskEntity.getUser().getUserId()) throw new CustomException(ErrorCode.REQUEST_AUTHORIZATION);

        return HelpdeskRes.Detail.fromEntity(boardHelpdeskEntity);
    }

    @Override
    public void deleteHelpdesk(long helpdeskId) {
        boardHelpdeskRepository.deleteById(helpdeskId);
    }
}

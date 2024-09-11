package com.j10d207.tripeer.board.controller;

import com.j10d207.tripeer.board.dto.req.BoardWriteReq;
import com.j10d207.tripeer.board.dto.res.NoticeRes;
import com.j10d207.tripeer.board.service.BoardNoticeService;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/notice")
@Slf4j
public class NoticeController {

    private final BoardNoticeService boardNoticeService;

    //공지사항 작성
    @PostMapping("/write")
    public Response<?> writeNotice(@RequestBody @Valid BoardWriteReq boardWriteReq,
                                   @AuthenticationPrincipal CustomOAuth2User user) {
        boardNoticeService.writeNotice(boardWriteReq, user.getUserId());
        return Response.of(HttpStatus.OK, "공지사항 작성 완료", null);
    }

    //공지사항 목록 조회
    @GetMapping("/{page}")
    public Response<NoticeRes> getNoticeList(@PathVariable ("page") int page) {
        return Response.of(HttpStatus.OK, "공지사항 목록 조회 완료", boardNoticeService.getNoticeList(page));
    }

    //공지사항 내용 조회
    @GetMapping("/detail/{noticeId}")
    public Response<NoticeRes.Detail> getNoticeDetail(@PathVariable ("noticeId") long noticeId) {
        return Response.of(HttpStatus.OK, "공지사항 내용 조회 완료", boardNoticeService.getDetail(noticeId));
    }

    //공지사항 삭제
    @DeleteMapping("/detail/{noticeId}")
    public Response<?> deleteNotice(@PathVariable ("noticeId") long noticeId) {
        boardNoticeService.deleteNotice(noticeId);
        return Response.of(HttpStatus.OK, "공지사항 삭제 완료", null);
    }
}

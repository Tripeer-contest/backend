package com.j10d207.tripeer.board.controller;

import com.j10d207.tripeer.board.dto.req.BoardWriteReq;
import com.j10d207.tripeer.board.dto.res.HelpdeskRes;
import com.j10d207.tripeer.board.dto.res.NoticeRes;
import com.j10d207.tripeer.board.service.BoardHelpdeskService;
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
@RequestMapping("/board/helpdesk")
@Slf4j
public class HelpdeskController {

    private final BoardHelpdeskService boardHelpdeskService;

    //문의사항 작성
    @PostMapping("/write")
    public Response<?> writeHelpdesk(@RequestBody @Valid BoardWriteReq boardWriteReq,
                                   @AuthenticationPrincipal CustomOAuth2User user) {
        boardHelpdeskService.writeHelpdesk(boardWriteReq, user.getUserId());
        return Response.of(HttpStatus.OK, "문의사항 작성 완료", null);
    }

    //문의사항 목록 조회
    @GetMapping("/{page}")
    public Response<HelpdeskRes> getHelpdeskList(@PathVariable ("page") int page,
                                                 @AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "문의사항 목록 조회 완료", boardHelpdeskService.getHelpdeskList(page, user.getUserId()));
    }

    //문의사항 내용 조회
    @GetMapping("/detail/{helpdeskId}")
    public Response<HelpdeskRes.Detail> getHelpdeskDetail(@PathVariable ("helpdeskId") long helpdeskId,
                                                          @AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "문의사항 내용 조회 완료", boardHelpdeskService.getDetail(helpdeskId, user.getUserId()));
    }

    //문의사항 삭제
    @DeleteMapping("/detail/{helpdeskId}")
    public Response<?> deleteHelpdesk(@PathVariable ("helpdeskId") long helpdeskId) {
        boardHelpdeskService.deleteHelpdesk(helpdeskId);
        return Response.of(HttpStatus.OK, "문의사항 삭제 완료", null);
    }
}

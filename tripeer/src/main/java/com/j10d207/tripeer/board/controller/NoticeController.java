package com.j10d207.tripeer.board.controller;

import com.j10d207.tripeer.board.dto.req.BoardWriteReq;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/notice")
@Slf4j
public class NoticeController {

    //공지사항 작성
    @PostMapping("/write")
    public Response<?> writeNotice(@RequestBody BoardWriteReq boardWriteReq,
                                   @AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "공지사항 작성 완료", null);
    }
}

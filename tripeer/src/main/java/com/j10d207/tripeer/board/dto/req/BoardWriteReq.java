package com.j10d207.tripeer.board.dto.req;

import lombok.Getter;

@Getter
public class BoardWriteReq {

    private long boardId;
    private String title;
    private String content;
}

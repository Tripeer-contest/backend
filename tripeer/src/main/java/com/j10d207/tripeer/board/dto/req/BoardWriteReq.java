package com.j10d207.tripeer.board.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BoardWriteReq {

    private long boardId;
    @Size(min = 2, max = 45, message = "2글자 미만이거나 45글자가 초과된 값이 입력되었습니다. ${validatedValue}")
    private String title;
    @NotBlank(message = "내용이 없습니다.")
    @Size(max = 2000, message = "2000글자가 초과된 값이 입력되었습니다.")
    private String content;
}

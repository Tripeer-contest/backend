package com.j10d207.tripeer.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {

    private String title;
    private String content;
    private Long userId;

    public static EmailDTO MakeInvitedEmail(String planTitle, String nickname, long userId) {
        // userId -> 초대 받는이, nickname -> 초대 하는사람
        String title = nickname + "님의 초대입니다";
        String content = nickname + "님이 " + planTitle + " 여행계획에 초대하셨습니다.";

        return EmailDTO.builder()
                .userId(userId)
                .content(content)
                .title(title)
                .build();
    }
}

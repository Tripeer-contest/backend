package com.j10d207.tripeer.board.db.entity;

import com.j10d207.tripeer.board.dto.req.BoardWriteReq;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity(name = "board_helpdesk")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardHelpdeskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long helpdeskId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    private String title;
    private String content;
    private LocalDateTime createTime;
    private int status;


    public static BoardHelpdeskEntity ofReq(BoardWriteReq boardWriteReq, long writerId) {
        return BoardHelpdeskEntity.builder()
                .helpdeskId(boardWriteReq.getBoardId())
                .user(UserEntity.builder().userId(writerId).build())
                .title(boardWriteReq.getTitle())
                .content(boardWriteReq.getContent())
                .createTime(LocalDateTime.now(ZoneId.of("Asia/Seoul"))).build();
    }
}

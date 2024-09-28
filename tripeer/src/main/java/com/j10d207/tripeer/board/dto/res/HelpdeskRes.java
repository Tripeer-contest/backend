package com.j10d207.tripeer.board.dto.res;

import com.j10d207.tripeer.board.db.entity.BoardHelpdeskEntity;
import com.j10d207.tripeer.common.CommonMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class HelpdeskRes {

    private long totalPage;
    private List<Detail> helpdeskList;

    @Getter
    @Builder
    public static class Detail {

        private long noticeId;
        private String writerNickname;
        private String writerProfileImage;
        private String title;
        private String content;
        private LocalDateTime createTime;
        private String status;

        public static Detail fromEntity(BoardHelpdeskEntity boardHelpdeskEntity) {
            return Detail.builder()
                    .noticeId(boardHelpdeskEntity.getHelpdeskId())
                    .writerNickname(boardHelpdeskEntity.getUser().getNickname())
                    .writerProfileImage(boardHelpdeskEntity.getUser().getProfileImage())
                    .title(boardHelpdeskEntity.getTitle())
                    .content(boardHelpdeskEntity.getContent())
                    .createTime(boardHelpdeskEntity.getCreateTime())
                    .status(CommonMethod.convertStatus(boardHelpdeskEntity.getStatus()))
                    .build();
        }

    }
}

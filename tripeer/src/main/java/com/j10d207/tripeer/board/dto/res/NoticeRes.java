package com.j10d207.tripeer.board.dto.res;

import com.j10d207.tripeer.board.db.entity.BoardNoticeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeRes {

    private long totalPage;
    private List<Detail> noticeList;

    @Getter
    @Builder
    public static class Detail {

        private long noticeId;
        private String writerNickname;
        private String writerProfileImage;
        private String title;
        private String content;
        private LocalDateTime createTime;

        public static Detail fromEntity(BoardNoticeEntity boardNoticeEntity) {
            return Detail.builder()
                    .noticeId(boardNoticeEntity.getNoticeId())
                    .writerNickname(boardNoticeEntity.getUser().getNickname())
                    .writerProfileImage(boardNoticeEntity.getUser().getProfileImage())
                    .title(boardNoticeEntity.getTitle())
                    .content(boardNoticeEntity.getContent())
                    .createTime(boardNoticeEntity.getCreateTime())
                    .build();
        }

    }

    @Getter
    @Builder
    public static class Summary {

        private long noticeId;
        private String title;
        private LocalDateTime createTime;

        public static Summary fromEntity(BoardNoticeEntity boardNoticeEntity) {
            return Summary.builder()
                    .noticeId(boardNoticeEntity.getNoticeId())
                    .title(boardNoticeEntity.getTitle())
                    .createTime(boardNoticeEntity.getCreateTime())
                    .build();
        }

    }
}

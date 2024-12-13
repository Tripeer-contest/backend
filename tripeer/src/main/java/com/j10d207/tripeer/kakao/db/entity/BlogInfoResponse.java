package com.j10d207.tripeer.kakao.db.entity;

import lombok.Getter;

import java.util.List;

/*
내부 클래스/변수 이름은 kakao API 네이밍과 동일하게 작성
 */
@Getter
public class BlogInfoResponse {

    private Meta meta;
    private List<Document> documents;

    public static class Meta {
        private int total_count;
        private int pageable_count;
        private boolean is_end;
    }

    @Getter
    public static class Document {
        private String title;
        private String contents;
        private String url;
        private String blogname;
        private String thumbnail;
        private String datetime;
    }
}

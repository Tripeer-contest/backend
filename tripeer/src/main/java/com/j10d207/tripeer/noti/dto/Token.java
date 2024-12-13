package com.j10d207.tripeer.noti.dto;

import com.j10d207.tripeer.noti.db.entity.FirebaseToken;

public record Token(String firebaseToken, Long tokenId, FirebaseToken.Type type) {

    public static Token of(final String firebaseToken, final Long tokenId, final FirebaseToken.Type type) {
        return new Token(firebaseToken, tokenId, type);
    }

}

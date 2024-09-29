package com.j10d207.tripeer.noti.dto;

public record Token(String firebaseToken, Long tokenId) {

    public static Token of(final String firebaseToken, final Long tokenId) {
        return new Token(firebaseToken, tokenId);
    }

}

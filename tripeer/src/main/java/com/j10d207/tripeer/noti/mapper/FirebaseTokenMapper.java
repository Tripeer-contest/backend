package com.j10d207.tripeer.noti.mapper;

import com.j10d207.tripeer.noti.db.entity.FirebaseToken;
import com.j10d207.tripeer.noti.dto.Token;
import com.j10d207.tripeer.noti.dto.TokenMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FirebaseTokenMapper {

    private FirebaseTokenMapper () {

    }

    public static TokenMap toTokenMap(final List<FirebaseToken> tokens) {
        final Map<Long, List<Token>> tokenMap = tokens.stream()
                .collect(Collectors.groupingBy(
                        token -> token.getUser().getUserId(),
                        Collectors.mapping(token -> Token.of(token.getToken(), token.getId()), Collectors.toList())
                ));
        return new TokenMap(tokenMap);
    }

    public static Token toTokenDto(final FirebaseToken token) {
        return Token.of(token.getToken(), token.getId());
    }
}

package com.j10d207.tripeer.noti.dto;

import com.j10d207.tripeer.noti.dto.Token;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class TokenMap {

    private final Map<Long, List<Token>> tokenMap;

    public TokenMap(final Map<Long, List<Token>> tokenMap) {
        this.tokenMap = Map.copyOf(tokenMap);
        log.info("tokenMap: {}", tokenMap);
    }

    public List<Token> getTokens(final Long userId) {
        List<Token> tokens = tokenMap.getOrDefault(userId, Collections.emptyList());
        if (tokens.isEmpty()) return tokens;
        return List.copyOf(tokens);
    }

    public List<Long> getUserIds() {
        return List.copyOf(tokenMap.keySet());
    }

}

package com.j10d207.tripeer.user.dto.res;

import com.j10d207.tripeer.user.config.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JWTDto {

    private String category;
    private String name;
    private String role;
    private long userId;

    public static JWTDto ofToken(Claims payload) {
        return JWTDto.builder()
                .category(payload.get("category", String.class))
                .name(payload.get("name", String.class))
                .role(payload.get("role", String.class))
                .userId(payload.get("userId", Long.class))
                .build();

    }
}

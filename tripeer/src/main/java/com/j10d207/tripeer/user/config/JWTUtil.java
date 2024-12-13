package com.j10d207.tripeer.user.config;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.user.dto.res.JWTDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Component
public class JWTUtil {

    private static SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        if(secret != null) {
            secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        }
    }

    public Claims getPayload(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }


    public String createJWT(JWTDto jwtDto, Long expiredMs) {

        return Jwts.builder()
                .claim("category", jwtDto.getCategory())
                .claim("name", jwtDto.getName())
                .claim("role", jwtDto.getRole())
                .claim("userId", jwtDto.getUserId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public Optional<JWTDto> verifyJWT(String token) {
        if ( token != null && !token.split(" ")[0].startsWith("Bearer")) {
            throw new CustomException(ErrorCode.EXPIRED_JWT);
        }
        if ( token == null ) {
            return Optional.empty();
        }

        String[] tokenFrags = token.split(" ");
        if( tokenFrags.length < 2 ) {
            throw new CustomException(ErrorCode.EXPIRED_JWT);
        }

        Claims payload = getPayload(tokenFrags[1]);

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = payload.get("category", String.class);
        if (!category.equals("Authorization")) {
            throw new CustomException(ErrorCode.EXPIRED_JWT);
        }

        JWTDto result = JWTDto.ofToken(payload);
        return Optional.of(result);
    }



}

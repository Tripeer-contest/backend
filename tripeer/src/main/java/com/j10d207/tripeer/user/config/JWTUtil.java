package com.j10d207.tripeer.user.config;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;
import com.j10d207.tripeer.user.dto.res.TestResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

@Component
public class JWTUtil {

    private static SecretKey secretKey;

    private static final JWTUtil instance = new JWTUtil(null);

    public static JWTUtil getInstance() { return instance; }

    private JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        if(secret != null) {
            secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        }
    }

    public static Claims getPayload(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }


    public static String createJWT(String category, String name, String role, long userId, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("name", name)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public static int verifyJWT(String token) {
        if ( token == null ) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                setContext(0L, "ROLE_NONE");
                return 2;   // context 가 없는 미로그인 상태
            }
            return 3;   // context 가 있는 미로그인 상태
        }

        if(!token.split(" ")[0].equals("Bearer")) {
            return 4;   // JWT 토큰의 bearer 가 없는상태 (변조)
        }

        String accessToken = token.split(" ")[1];
        Claims payload = JWTUtil.getPayload(accessToken);

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = payload.get("category", String.class);
        if (!category.equals("Authorization")) {
            return 5; // JWT 토큰이 카테고리가 다른 상태(다른 토큰 or 변조)
        }
        setContext(payload.get("userId", Long.class), payload.get("role", String.class));
        return 1;
    }



    private static void setContext(long userId, String role) {
        TestResponse testResponse = new TestResponse();
        CustomOAuth2User test = new CustomOAuth2User(testResponse, role, userId);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(test, null, getAuthorities(role));
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(String role) {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return role;
            }
        });

        return collection;
    }

}

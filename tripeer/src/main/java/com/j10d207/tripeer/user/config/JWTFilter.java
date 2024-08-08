package com.j10d207.tripeer.user.config;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;
import com.j10d207.tripeer.user.dto.res.TestResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //request에서 access 헤더를 찾음
        String access = request.getHeader("Authorization");
        int verifyResult;
        try {   //만료검사가 같이됨
            verifyResult = JWTUtil.verifyJWT(access);
        } catch (ExpiredJwtException e) {
            setErrorResponse(response);
            return;
        }
        switch (verifyResult) {
            case 2 :    // context 가 없는 미로그인 상태
            case 3 :    // context 가 있는 미로그인 상태
                filterChain.doFilter(request, response);
                return;
            case 4 :    // JWT 토큰의 bearer 가 없는상태 (변조)
                log.error("JWT 토큰 변조 우려 - Bearer 없음");
            case 5 :    // JWT 토큰이 카테고리가 다른 상태(다른 토큰 or 변조)
                setErrorResponse(response);
                return;
            case 1 :    // JWT 검증 성공
                filterChain.doFilter(request, response);
                return;
            default :   // 나올일 없음
                log.error("JWT Filter 처리 중 작성되지 않은 상태 발생");
                setErrorResponse(response);
        }
    }

    private void setErrorResponse (HttpServletResponse response) throws IOException {
        //response body
        PrintWriter writer = response.getWriter();
        writer.print("invalid access token");
        //response status code
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}



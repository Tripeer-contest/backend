package com.j10d207.tripeer.user.config;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;
import com.j10d207.tripeer.user.dto.res.JWTDto;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //request에서 access 헤더를 찾음
        String access = request.getHeader("Authorization");

        // try {
        setContextAfterVerify(access);
        filterChain.doFilter(request, response);
        // } catch (Exception e) {
        //     if(e instanceof ExpiredJwtException) {
        //         log.error("JWT 토큰이 만료된 상태입니다.");
        //     } else {
        //         log.error(e.getMessage());
        //         log.error("JWT 토큰의 변조가 의심되거나 형식이 불일치한 상태입니다.");
        //     }
        //     setErrorResponse(response);
        // }

    }

    private void lostAccessHandler() {
        //로그아웃 시에 context 를 비로그인 상태로 반드시 set 했다고 가정
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            setContext(0L, "ROLE_NONE");
        }
    }

    public void setContextAfterVerify(String token) {
         Optional<JWTDto> dto = jwtUtil.verifyJWT(token);
        // 비로그인 상태의 경우 즉, 컨텍스트는 존재하나 토큰이 null 인 경우와 둘다 null 인 경우에 대한 처리
        if (dto.isEmpty()){
            lostAccessHandler();
            return;
        }
        setContext(dto.get().getUserId(), dto.get().getRole());
    }


    private void setContext(long userId, String role) {
        TestResponse testResponse = new TestResponse();
        CustomOAuth2User test = new CustomOAuth2User(testResponse, role, userId);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(test, null, getAuthorities(role));
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return role;
            }
        });

        return collection;
    }

    private void setErrorResponse (HttpServletResponse response) throws IOException {
        //response body
        PrintWriter writer = response.getWriter();
        writer.print("invalid access token");
        //response status code
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}



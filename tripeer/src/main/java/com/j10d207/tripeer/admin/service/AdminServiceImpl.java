package com.j10d207.tripeer.admin.service;

import com.j10d207.tripeer.admin.dto.req.AdminLoginReq;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.user.config.JWTUtil;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;
import com.j10d207.tripeer.user.dto.res.JWTDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    @Value("${admin.login}")
    private String loginKey;

    @Value("${spring.jwt.access}")
    private long accessTime;
    @Value("${spring.jwt.refresh}")
    private long refreshTime;

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public String adminLogin(AdminLoginReq adminLoginReq, HttpServletResponse response) {
        //접근키가 일치하지 않거나 admin 으로 접근하지 않은 경우
        if(!adminLoginReq.getPassword().equals(loginKey) || !adminLoginReq.getId().startsWith("admin")) {
            throw new CustomException(ErrorCode.INVALID_ADMIN);
        }
        UserEntity user = userRepository.findByProviderId(adminLoginReq.getId());

        String result = jwtUtil.createJWT(new JWTDto("Authorization", user.getNickname(), user.getRole(), user.getUserId()), accessTime);
        String refresh = jwtUtil.createJWT(new JWTDto("Authorization-re", user.getNickname(), user.getRole(), user.getUserId()), refreshTime);

        response.addCookie(createCookie("Authorization-re", refresh));
        response.setHeader("AccessTime", accessTime + "");
        response.setHeader("RefreshTime", refreshTime + "");
        response.setHeader("Authorization", "Bearer " + result);

        return "Bearer " + result;
    }


    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setSecure(true);
        cookie.setPath("/");
        if(key.equals("Authorization-re")) {
            cookie.setHttpOnly(true);
        }

        return cookie;
    }
}

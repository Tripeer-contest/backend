package com.j10d207.tripeer.admin.controller;

import com.j10d207.tripeer.admin.dto.req.AdminLoginReq;
import com.j10d207.tripeer.admin.service.AdminService;
import com.j10d207.tripeer.response.Response;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /*
    더미 사용자로 로그인 전용, admin 이지만 특별히 제어할 수 있는 기능은 없고
    provide_id가 admin 인 유저 중 하나로 들어갈 수 있음
     */
    @PostMapping("/login")
    public Response<String> adminLogin(@RequestBody AdminLoginReq adminLoginReq, HttpServletResponse response) {
        String result = adminService.adminLogin(adminLoginReq, response);
        return Response.of(HttpStatus.OK, "admin", result);

    }
}

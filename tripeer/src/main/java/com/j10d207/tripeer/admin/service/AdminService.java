package com.j10d207.tripeer.admin.service;

import com.j10d207.tripeer.admin.dto.req.AdminLoginReq;
import jakarta.servlet.http.HttpServletResponse;

public interface AdminService {

    public String adminLogin(AdminLoginReq adminLoginReq, HttpServletResponse response);
}

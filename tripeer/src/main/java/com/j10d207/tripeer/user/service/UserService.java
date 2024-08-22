package com.j10d207.tripeer.user.service;

import com.j10d207.tripeer.user.dto.req.InfoReq;
import com.j10d207.tripeer.user.dto.req.JoinReq;
import com.j10d207.tripeer.user.dto.res.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// access 토큰 재발급
public interface UserService {

    //회원가입
    public String memberSignup(JoinReq join, HttpServletResponse response);
    //프로필 사진 변경
    public String uploadProfileImage(MultipartFile file, long userId);
    //내 정보 수정
    public void modifyMyInfo(long userId, InfoReq infoReq);
    //소셜 정보 획득
    public UserDTO.Social getSocialInfo();
    //닉네임 중복체크
    public boolean nicknameDuplicateCheck(String nickname);
    //유저 검색
    public List<UserDTO.Search> userSearch(String nickname);
    //내 정보 불러오기
    public UserDTO.Info getMyInfo(long userId);
    //내 찜목록 전체 불러오기
    public List<UserDTO.Wishlist> getMyWishlist(long userId);
    //즐겨찾기 추가
    public void addWishList(int spotInfoId, long userId);
    //access 토큰 재발급
    public void tokenRefresh(String token, Cookie[] cookies, HttpServletResponse response);

    //테스트용 JWT 발급
    public String getSuper(HttpServletResponse response, long userId);
    //테스트용 JWT 발급2
    public String getSuper2(HttpServletResponse response, long userId);


}

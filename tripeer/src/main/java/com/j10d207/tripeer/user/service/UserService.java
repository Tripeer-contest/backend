package com.j10d207.tripeer.user.service;

import com.j10d207.tripeer.user.dto.req.CustomJoinReq;
import com.j10d207.tripeer.user.dto.req.CustomLoginReq;
import com.j10d207.tripeer.user.dto.req.EmailVerifyReq;
import com.j10d207.tripeer.user.dto.req.InfoReq;
import com.j10d207.tripeer.user.dto.req.JoinReq;
import com.j10d207.tripeer.user.dto.req.NotiReq;
import com.j10d207.tripeer.user.dto.req.PasswordChangeReq;
import com.j10d207.tripeer.user.dto.req.WishlistReq;
import com.j10d207.tripeer.user.dto.res.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

// access 토큰 재발급
public interface UserService {

    //회원가입
    public String memberSignup(JoinReq join, HttpServletResponse response);
    //커스텀 회원가입
    public String customSignup(CustomJoinReq join, HttpServletResponse response);
    //커스텀 로그인
    public String customLogin(CustomLoginReq loginReq, HttpServletResponse response);
    //비밀번호 변경
    public void changePassword(PasswordChangeReq passwordChangeReq);
    //프로필 사진 변경
    public String uploadProfileImage(MultipartFile file, long userId);
    //내 정보 수정
    public void modifyMyInfo(long userId, InfoReq infoReq);
    //소셜 정보 획득
    public UserDTO.Social getSocialInfo();
    //인증 이메일 발송
    public boolean sendValidEmail(String email);
    //인증 이메일 발송 (for 비밀번호 변경)
    public boolean sendValidPassword(String email);
    //닉네임 중복체크
    public boolean nicknameDuplicateCheck(String nickname);
    //인증 이메일 검증
    public boolean emailVerification(String email, String code);
    //인증 이메일 검증 (for 비밀번호 변경)
    public boolean passwordVerification(String email, String code);
    //유저 검색
    public List<UserDTO.Search> userSearch(String nickname);
    //내 정보 불러오기
    public UserDTO.Info getMyInfo(long userId);
    //내 찜목록 전체 불러오기
    public List<UserDTO.Wishlist> getMyWishlist(long userId);
    //즐겨찾기 추가
    public void addWishList(WishlistReq wishlistReq, long userId);
    //access 토큰 재발급
    public void tokenRefresh(String token, Cookie[] cookies, HttpServletResponse response);

    //테스트용 JWT 발급
    public String getSuper(HttpServletResponse response, long userId);
    //테스트용 JWT 발급2
    public String getSuper2(HttpServletResponse response, long userId);
    // 알람 설정 변경
    public String changeNoti(long userId, NotiReq notiReq);

    // 해당 하는 유저의 notification allow 상태 확인
   Boolean getAllowNotificationById(final Long userIds);

}
